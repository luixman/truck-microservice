package ru.truckfollower.service.telegram;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    //chatId,TelegramConnectionModel
    @Getter
    private Map<Long, TelegramConnectionModel> chatConnections = new ConcurrentHashMap<>();

    private final String botToken;
    private final String botName;

    private final TelegramConnectionService telegramConnectionService;
    private final KeyGeneratorService keyGeneratorService;

    private final TelegramAuthService telegramAuthService;


    @Autowired
    private CompanyService companyService;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.name}") String botName,
                       TelegramConnectionService telegramConnectionService,
                       KeyGeneratorService keyGeneratorService,
                       TelegramAuthService telegramAuthService) {
        this.botToken = botToken;
        this.botName = botName;
        this.telegramConnectionService = telegramConnectionService;
        this.keyGeneratorService = keyGeneratorService;
        this.telegramAuthService = telegramAuthService;

    }

    @PostConstruct
    public void updateChatConnections() {
        List<TelegramConnection> connections = telegramConnectionService.getAll();
        Map<Long, TelegramConnectionModel> chatConnections = new ConcurrentHashMap<>();

        for (TelegramConnection connection : connections) {
            chatConnections.put(connection.getChatId(), telegramConnectionService.toModel(connection));
        }
        this.chatConnections = chatConnections;
    }

    @Override
    public void onUpdateReceived(Update update) {
        //если это кнопка
        if (update.hasCallbackQuery()) {
            if (telegramAuthService.hasChatAuth(update.getCallbackQuery().getMessage().getChatId())) {
                sendNoAuthMessage(update.getCallbackQuery().getMessage().getChatId());
                return;
            }
            handleCallBackQuery(update.getCallbackQuery());
        }

        //если это сообщение, если это команда
        if (update.hasMessage() && update.getMessage().isCommand()) {
            Message message = update.getMessage();

            if (message.getText().startsWith("/key"))
                handleAuthMessage(message);

            if (!telegramAuthService.hasChatAuth(message.getChatId())) {
                sendNoAuthMessage(message.getChatId());
                return;
            }
            handleCommandMessage(message);
        }


        //если это связано с участником чата (кик, добавление пользователя)
        if (update.hasMyChatMember()) {
            ChatMemberUpdated chatMemberUpdated = update.getMyChatMember();

            handleMyChatMember(chatMemberUpdated);
        }

    }

    private void handleMyChatMember(ChatMemberUpdated chatMemberUpdated) {
        if ((chatMemberUpdated.getNewChatMember().getStatus().equals("kicked")
                || chatMemberUpdated.getNewChatMember().getStatus().equals("left"))) {
            if (chatMemberUpdated.getNewChatMember().getUser().getUserName().equals(getBotUsername())) {
                //бота кикнули

                telegramAuthService.authorizationDelete(chatMemberUpdated.getChat().getId());
                updateChatConnections();
            }
        } else {
            //создаем новый коннекшн
            TelegramConnectionModel telegramConnectionModel = TelegramConnectionModel.builder()
                    .chatId(chatMemberUpdated.getChat().getId())
                    .authKey(keyGeneratorService.getNewTelegramRandomKey())
                    .firstAuthTime(Instant.now())//фикс
                    .authorized(false)
                    .activatedCompanies(new HashSet<>())
                    .build();
            telegramAuthService.authorizationAttempt(chatMemberUpdated.getChat().getId(), "");
            telegramConnectionService.save(telegramConnectionService.toEntity(telegramConnectionModel));
            updateChatConnections();
        }
    }

    private void handleAuthMessage(Message message) {
        //валидация, можно вынести в метод
        TelegramConnectionModel telegramConnectionModel = chatConnections.get(message.getChatId());
        String key = message.getText().replace("/key ", "");

        try {
            boolean result = telegramAuthService.authorizationAttempt(message.getChatId(), key);
            if(telegramConnectionModel.getAuthorized()){
                execute(SendMessage.builder().chatId(message.getChatId()).text("Вы уже авторизованы, введите /set_company, что бы выбрать компании").build());

            }
            else if (result) {
                updateChatConnections();
                execute(SendMessage.builder().chatId(message.getChatId()).text("Авторизация прошла успешно").build());
            } else {
                execute(SendMessage.builder().chatId(message.getChatId()).text("Неправильный ключ, попробуйте еще раз").build());
            }

        } catch (TelegramApiException e) {
            log.error(e.getMessage());
}
    }


    private void handleCallBackQuery(CallbackQuery callbackQuery) {
        String callBack = callbackQuery.getData();
       
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

        InlineKeyboardMarkup inlineKeyboardMarkup = updateMarkup(telegramConnectionModel);
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
        InlineKeyboardMarkup inlineKeyboardMarkup = updateMarkup(telegramConnectionModel);

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

    private InlineKeyboardMarkup updateMarkup(TelegramConnectionModel telegramConnectionModel) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getDefaultMarkup();
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
        return inlineKeyboardMarkup;
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
