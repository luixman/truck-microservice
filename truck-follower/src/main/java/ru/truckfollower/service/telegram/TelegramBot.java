package ru.truckfollower.service.telegram;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.truckfollower.repo.AlarmRepo;

import java.util.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {


    //мапа chatid listKey
    private final String botToken;
    private final String botName;
    private final AlarmRepo repo;


    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.name}") String botName,
                       AlarmRepo repo) {
        this.botToken = botToken;
        this.botName = botName;
        this.repo = repo;

    }


    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {


        //если это кнопка
        if (update.hasCallbackQuery()) {

            if (!isChatVerified(update.getCallbackQuery().getMessage().getChatId())) {
                sendNoAuthMessage(update.getCallbackQuery().getMessage().getChatId());
                return;
            }

            // TODO: 17.11.2022 логика для кнопки

            execute(SendMessage.builder()
                    .allowSendingWithoutReply(true)
                    .chatId(update.getCallbackQuery().getMessage().getChatId())
                    .text("вы нажали на кнопку:" + update.getCallbackQuery().getData())
                    .build());
        }


        //если это сообщение
        if (update.hasMessage()) {

            if (!isChatVerified(update.getMessage().getChatId())) {
                sendNoAuthMessage(update.getMessage().getChatId());
                return;
            }

            //если это команда
            if (update.getMessage().isCommand()) {
                handleCommandMessage(update.getMessage());
            }

        }


    }

    @SneakyThrows
    private void sendNoAuthMessage(Long chatId) {
        execute(SendMessage.builder()
                .text("Для того, что бы пользоваться функциями бота, введите секретный ключ")
                .chatId(chatId)
                .build());

    }

    private boolean isChatVerified(Long chatId) {
        // TODO: 17.11.2022

        return chatId == -839595209L;
    }

    private void handleCommandMessage(Message message) throws TelegramApiException {
        Optional<MessageEntity> commandEntity = message.getEntities().stream().findFirst();

        if (commandEntity.isPresent()) {

            switch (commandEntity.get().getText()) {
                case ("/start"):
                    log.info("Telegram bot: new user: " + message.getFrom().getUserName() + ", chat: " + message.getChat().getTitle());
                    if (!isChatVerified(message.getChatId()))
                        sendNoAuthMessage(message.getChatId());
                    else
                        execute(SendMessage.builder().chatId(message.getChatId())
                                .text("Бот активирован и готов к работе")
                                .replyToMessageId(message.getMessageId())
                                .build());
                    break;
                case ("/set_company"):
                    System.out.println("set company");

                    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

                    buttons.add(Collections.singletonList(
                            InlineKeyboardButton.builder().text("Деловые линии").callbackData("1").build()));
                    buttons.add(Collections.singletonList(
                            InlineKeyboardButton.builder().text("Газелькин").callbackData("2").build()));
                    buttons.add(Collections.singletonList(
                            InlineKeyboardButton.builder().text("Везет всем").callbackData("3").build()));
                    buttons.add(Collections.singletonList(
                            InlineKeyboardButton.builder().text("это сообщение содержит 30 сиce").callbackData("jh").build()));


                    InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboard(buttons)
                            .build();


                    execute(SendMessage.builder()
                            .chatId(message.getChatId())
                            .text("Выберите компанию(-нии):")
                            .allowSendingWithoutReply(true)
                            .replyMarkup(inlineKeyboardMarkup)
                            .build());
                    break;
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}
