package ru.truckfollower.service.telegram;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.truckfollower.entity.Company;
import ru.truckfollower.model.TelegramChatModel;
import ru.truckfollower.repo.AlarmRepo;
import ru.truckfollower.service.CompanyService;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    //chatid,companyid
    @Getter
    //   private final Map<Long,Set<Long>> activatedCompanies = new HashMap<>();

    private final Map<Long, TelegramChatModel> activatedCompanies2 = new HashMap<>();


    //мапа chatid listKey
    private final String botToken;
    private final String botName;
    private final AlarmRepo repo;

    @Autowired
    private CompanyService companyService;


    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.name}") String botName,
                       AlarmRepo repo) {
        this.botToken = botToken;
        this.botName = botName;
        this.repo = repo;

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

            handleCallBackQuery(update.getCallbackQuery());
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
    private void handleCallBackQuery(CallbackQuery callbackQuery) {
        InlineKeyboardMarkup inlineKeyboardMarkup = callbackQuery.getMessage().getReplyMarkup();

        List<Company> companyList = companyService.getAll();
        String callBack = callbackQuery.getData();

        for (List<InlineKeyboardButton> list : inlineKeyboardMarkup.getKeyboard()) {
            for (InlineKeyboardButton inlineKeyboardButton : list) {
                if (Integer.parseInt(inlineKeyboardButton.getCallbackData()) == Integer.parseInt(callBack)) {

                    if (!activatedCompanies2.containsKey(callbackQuery.getMessage().getChatId())) {

                        activatedCompanies2.put(callbackQuery.getMessage().getChatId(),
                                TelegramChatModel.
                                        builder()
                                        .companyIds(ConcurrentHashMap.newKeySet())
                                        .inlineKeyboardMarkup(getDefaultMarkup())
                                        .build());
                    }

                    TelegramChatModel telegramChatModel = activatedCompanies2.get(callbackQuery.getMessage().getChatId());

                    if (inlineKeyboardButton.getText().contains("✅")) {
                        inlineKeyboardButton.setText(inlineKeyboardButton.getText().substring(1));
                        telegramChatModel.getCompanyIds().remove(Long.parseLong(callBack));
                        //currentChatMessage.remove();
                    } else {
                        inlineKeyboardButton.setText("✅" + inlineKeyboardButton.getText());
                        telegramChatModel.getCompanyIds().add(Long.parseLong(callBack));

                    }
                }
            }
        }


        activatedCompanies2.get(callbackQuery.getMessage().getChat().getId()).setInlineKeyboardMarkup(inlineKeyboardMarkup);


        execute(EditMessageReplyMarkup.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .replyMarkup(inlineKeyboardMarkup)
                .messageId(callbackQuery.getMessage().getMessageId()).build());


        System.out.println(activatedCompanies2);
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

        if (chatId == 315136544)
            return true;

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
                                .text("Бот активирован и готов к работе, введите /set_company чтобы выбрать отслеживаемые грузовики компаний")
                                .replyToMessageId(message.getMessageId())
                                .build());
                    break;
                case ("/set_company@truck_alert_bot"):
                case ("/set_company"):
                    setCompanyCommand(message);
                    break;
            }
        }
    }

    @SneakyThrows
    private void setCompanyCommand(Message message) {


        TelegramChatModel telegramChatModel = activatedCompanies2.get(message.getChatId());

        InlineKeyboardMarkup inlineKeyboardMarkup = null;
        if (telegramChatModel != null && telegramChatModel.getInlineKeyboardMarkup() != null)
            inlineKeyboardMarkup = telegramChatModel.getInlineKeyboardMarkup();
        else
            inlineKeyboardMarkup = getDefaultMarkup();

        execute(SendMessage.builder()
                .chatId(message.getChatId())
                .text("Выберите компанию(-нии):")
                .allowSendingWithoutReply(true)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
    }


    private InlineKeyboardMarkup getDefaultMarkup() {
        //Создаем кнопки
        List<List<InlineKeyboardButton>> defaultButtons = new ArrayList<>();
        defaultButtons.add(Collections.singletonList(
                InlineKeyboardButton.builder().text("Все").callbackData(Long.toString(0)).build()
        ));

        for (Company company : companyService.getAll()) {
            defaultButtons.add(Collections.singletonList(
                    InlineKeyboardButton.builder().text(company.getShortName()).callbackData(Long.toString(company.getId())).build()
            ));
        }
        return InlineKeyboardMarkup.builder().keyboard(defaultButtons).build();
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
