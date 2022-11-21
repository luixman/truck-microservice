package ru.truckfollower.service.telegram;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.truckfollower.entity.Company;
import ru.truckfollower.entity.TelegramConnection;
import ru.truckfollower.model.TelegramConnectionModel;
import ru.truckfollower.service.CompanyService;
import ru.truckfollower.service.KeyGeneratorService;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    //chatid,companyid
    @Getter
    private final Map<Long, TelegramConnectionModel> chatConnections = new HashMap<>();//состояние кнопок

    private final String botToken;
    private final String botName;

    private final TelegramConnectionService telegramConnectionService;
    private final KeyGeneratorService keyGeneratorService;


    @Autowired
    private CompanyService companyService;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.name}") String botName,
                       TelegramConnectionService telegramConnectionService,
                       KeyGeneratorService keyGeneratorService) {
        this.botToken = botToken;
        this.botName = botName;
        this.telegramConnectionService = telegramConnectionService;
        this.keyGeneratorService = keyGeneratorService;

    }

    @PostConstruct
    public void init() {
        List<TelegramConnection> authorizedConnections = telegramConnectionService.getAll();

        for (TelegramConnection connection : authorizedConnections) {
            chatConnections.put(connection.getChatId(), telegramConnectionService.toModel(connection));
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        //если это кнопка
        if (update.hasCallbackQuery()) {
            if (!isChatVerified(update.getCallbackQuery().getMessage().getChatId())) {
                sendNoAuthMessage(update.getCallbackQuery().getMessage().getChatId());
                return;
            }
            handleCallBackQuery(update.getCallbackQuery());
        }

        //если это сообщение, если это команда
        if (update.hasMessage() && update.getMessage().isCommand()) {
            Message message = update.getMessage();

            if (message.getText().startsWith("/key")) {
                //валидация, можно вынести в метод
                TelegramConnectionModel telegramConnectionModel = chatConnections.get(message.getChatId());
                String key = message.getText().replace("/key ", "");
                try {
                    if (telegramConnectionModel.getAuthKey().equals(key)) {
                        telegramConnectionModel.setAuthorized(true);
                        telegramConnectionService.save(telegramConnectionService.toEntity(telegramConnectionModel));
                        execute(SendMessage.builder().chatId(message.getChatId()).text("Авторизация прошла успешно").build());
                    } else {
                        execute(SendMessage.builder().chatId(message.getChatId()).text("Неправильный ключ, попробуйте еще раз").build());
                        return;
                    }
                }catch (TelegramApiException e){
                    log.error(e.getMessage());
                }
            }

            if (!isChatVerified(update.getMessage().getChatId())) {
                sendNoAuthMessage(message.getChatId());
                return;
            }
            handleCommandMessage(message);
        }


        if (update.hasMyChatMember()) {
            ChatMemberUpdated chatMemberUpdated = update.getMyChatMember();

            if (chatMemberUpdated.getNewChatMember().getStatus().equals("kicked") && chatMemberUpdated.getNewChatMember().getUser().getUserName().equals(getBotUsername())) {
                //бота кикнули
                chatConnections.remove(chatMemberUpdated.getChat().getId());
                telegramConnectionService.deleteByChatID(chatMemberUpdated.getChat().getId());
                return;
            } else {
                //создаем новый коннекшн
                TelegramConnectionModel telegramConnectionModel = TelegramConnectionModel.builder()
                        .chatId(chatMemberUpdated.getChat().getId())
                        .authKey(keyGeneratorService.getNewTelegramRandomKey())
                        .firstAuthTime(Instant.now())//фикс
                        .authorized(false)
                        .activatedCompanies(new HashSet<>())
                        .build();
                chatConnections.put(telegramConnectionModel.getChatId(), telegramConnectionModel);
                telegramConnectionService.save(telegramConnectionService.toEntity(telegramConnectionModel));
                sendNoAuthMessage(chatMemberUpdated.getChat().getId());
            }
        }

    }


    private void handleCallBackQuery(CallbackQuery callbackQuery) {
        String callBack = callbackQuery.getData();
        InlineKeyboardMarkup inlineKeyboardMarkup = getDefaultMarkup();
        TelegramConnectionModel telegramConnectionModel = chatConnections.get(callbackQuery.getMessage().getChatId());


        if (telegramConnectionModel == null) {
            // TODO: 21.11.2022
            log.error("handleCallBackQuery method. telegramConnection model = null");
            return;
        }

        if (telegramConnectionModel.getActivatedCompanies().contains(Long.parseLong(callBack))) {
            telegramConnectionModel.getActivatedCompanies().remove(Long.parseLong(callBack));
        } else {
            telegramConnectionModel.getActivatedCompanies().add(Long.parseLong(callBack));
        }

        telegramConnectionService.save(telegramConnectionService.toEntity(telegramConnectionModel));

        for (Long companyId : telegramConnectionModel.getActivatedCompanies()) {
            List<List<InlineKeyboardButton>> buttons = inlineKeyboardMarkup.getKeyboard();
            for (List<InlineKeyboardButton> buttonLine : buttons) {
                for (InlineKeyboardButton button : buttonLine) {
                    if (button.getCallbackData().equals(String.valueOf(companyId))) {
                        button.setText("✅" + button.getText());
                    }
                }
            }
        }

        try {
            execute(EditMessageReplyMarkup.builder()
                    .chatId(callbackQuery.getMessage().getChatId())
                    .replyMarkup(inlineKeyboardMarkup)
                    .messageId(callbackQuery.getMessage().getMessageId()).build());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }


    private void sendNoAuthMessage(Long chatId) {
        try {
            execute(SendMessage.builder()
                    .text("\"Для того, что бы пользоваться сервисом, введите одноразовый секретный ключ, в формате /key ваш_ключ. Он уже сгенерирован и находится у администратора\"")
                    .chatId(chatId)
                    .build());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

    private boolean isChatVerified(Long chatId) {
        // TODO: 17.11.2022
        if (!chatConnections.containsKey(chatId)) {
            TelegramConnection telegramConnection = TelegramConnection.builder()
                    .chatId(chatId)
                    .authKey(keyGeneratorService.getNewTelegramRandomKey())
                    .authorized(false)
                    .activatedCompanies("")
                    .build();

            chatConnections.put(telegramConnection.getChatId(), telegramConnectionService.toModel(telegramConnection));
            telegramConnectionService.save(telegramConnection);
            return false;
        } else {
            return chatConnections.get(chatId).getAuthorized();
        }

    }

    private void handleCommandMessage(Message message) {
        Optional<MessageEntity> commandEntity = message.getEntities().stream().findFirst();

        if (commandEntity.isPresent()) {

            switch (commandEntity.get().getText()) {
                case ("/set_company@truck_alert_bot"):
                case ("/set_company"):
                    setCompanyCommand(message);
                    break;
            }
        }
    }


    private void setCompanyCommand(Message message) {
        TelegramConnectionModel telegramConnectionModel = chatConnections.get(message.getChatId());

        InlineKeyboardMarkup inlineKeyboardMarkup = getDefaultMarkup();

        for (Long companyId : telegramConnectionModel.getActivatedCompanies()) {
            List<List<InlineKeyboardButton>> buttons = inlineKeyboardMarkup.getKeyboard();

            for (List<InlineKeyboardButton> buttonLine : buttons) {
                for (InlineKeyboardButton button : buttonLine) {
                    if (button.getCallbackData().equals(String.valueOf(companyId))) {
                        button.setText("✅" + button.getText());
                    }/*else if(button.getText().contains("✅")){
                        button.setText(button.getText().substring(1));
                    }*/
                }
            }
        }

        try {
            execute(SendMessage.builder()
                    .chatId(message.getChatId())
                    .text("Выберите компанию(-нии):")
                    .allowSendingWithoutReply(true)
                    .replyMarkup(inlineKeyboardMarkup)
                    .build());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
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
