package ru.telegrambot.service.telegram;

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
import ru.telegrambot.entity.Alarm;
import ru.telegrambot.entity.Company;
import ru.telegrambot.entity.TelegramConnection;
import ru.telegrambot.exception.EntityNotFoundException;
import ru.telegrambot.model.TelegramConnectionModel;
import ru.telegrambot.service.AlarmService;
import ru.telegrambot.service.CompanyService;
import ru.telegrambot.service.KeyGeneratorService;


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
    private final int companiesOnThePage = 5; // TODO: 23.11.2022 вынести в конфиг

    private final TelegramConnectionService telegramConnectionService;
    private final KeyGeneratorService keyGeneratorService;
    private final TelegramAuthService telegramAuthService;
    private final AlarmService alarmService;


    @Autowired
    private CompanyService companyService;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken, @Value("${telegram.bot.name}") String botName,
                       TelegramConnectionService telegramConnectionService, KeyGeneratorService keyGeneratorService,
                       TelegramAuthService telegramAuthService, AlarmService alarmService) {
        this.botToken = botToken;
        this.botName = botName;
        this.telegramConnectionService = telegramConnectionService;
        this.keyGeneratorService = keyGeneratorService;
        this.telegramAuthService = telegramAuthService;
        this.alarmService = alarmService;

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
            if (!telegramAuthService.hasChatAuth(update.getCallbackQuery().getMessage().getChatId())) {
                sendNoAuthMessage(update.getCallbackQuery().getMessage().getChatId());
                return;
            }
            handleCallBackQuery(update.getCallbackQuery());
        }

        //если это сообщение, если это команда
        if (update.hasMessage() && update.getMessage().isCommand()) {
            Message message = update.getMessage();

            if (message.getText().startsWith("/key")) {
                handleAuthMessage(message);
            }

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
                log.info("chat:" + chatMemberUpdated.getChat().getUserName() + " " + chatMemberUpdated.getChat().getId() + ". Left the chat");
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
            log.info("chat:" + chatMemberUpdated.getChat().getUserName() + " " + chatMemberUpdated.getChat().getId() + ". Join the chat");
            telegramAuthService.authorizationAttempt(chatMemberUpdated.getChat().getId(), "");
            telegramConnectionService.save(telegramConnectionService.toEntity(telegramConnectionModel));
            updateChatConnections();
        }
    }

    private void handleAuthMessage(Message message) {
        //валидация, можно вынести в метод

        String messageLog = "Chat: " + message.getChat().getId() + ". Authorization attempt";


        TelegramConnectionModel telegramConnectionModel = chatConnections.get(message.getChatId());
        String key = message.getText().replace("/key ", "");

        try {
            boolean result = telegramAuthService.authorizationAttempt(message.getChatId(), key);
            if (telegramConnectionModel.getAuthorized()) {
                execute(SendMessage.builder().chatId(message.getChatId()).text("Вы уже авторизованы, введите /set_company, что бы выбрать компании").build());
                messageLog += ". Authorization already done, key: " + key;
            } else if (result) {
                updateChatConnections();
                execute(SendMessage.builder().chatId(message.getChatId()).text("Авторизация прошла успешно, введите /set_company, что бы выбрать компании").build());
                messageLog += ". Authorization completed successfully, key: " + key;
            } else {
                execute(SendMessage.builder().chatId(message.getChatId()).text("Неправильный ключ, попробуйте еще раз").build());
                messageLog += ". Authorization failed: " + key;
            }

            log.info(messageLog);

        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }


    private void handleCallBackQuery(CallbackQuery callbackQuery) {
        String callBack = callbackQuery.getData();
        String messageLog = "Chat: " + callbackQuery.getMessage().getChat().getId();

        TelegramConnectionModel telegramConnectionModel = chatConnections.get(callbackQuery.getMessage().getChatId());

        if (telegramConnectionModel == null) {
            // TODO: 21.11.2022
            log.error("handleCallBackQuery method. telegramConnection model = null");
            return;
        }
        int page = 1;
        if (callBack.contains("p")) {
            telegramConnectionModel.setPage(Integer.parseInt(callBack.substring(1)));
            messageLog += ". Pressed the button: " + callBack;

        } else if (telegramConnectionModel.getActivatedCompanies().contains(Long.parseLong(callBack))) {
            telegramConnectionModel.getActivatedCompanies().remove(Long.parseLong(callBack));
            messageLog += ". Deactivated the button: " + callBack;
        } else {
            telegramConnectionModel.getActivatedCompanies().add(Long.parseLong(callBack));
            messageLog += ". Activated the button: " + callBack;
        }
        log.info(messageLog);

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

            String command = commandEntity.get().getText();
            if (command.startsWith("/set_company")) {
                setCompanyCommand(message);
            } else if (command.startsWith("/get_alarm_")) {
                getAlarmCommand(message);
            }

        }
        log.info("Chat: " + message.getChat().getId() + ". Sent a command: " + message.getText());

    }

    private void getAlarmCommand(Message message) {

        long alarmId;
        Alarm a;

        try {
            alarmId = Long.parseLong(message.getText().replace("/get_alarm_", "").replace("@truck_alert_bot", ""));
            a = alarmService.getAlarmById(alarmId);
        } catch (NumberFormatException e) {
            log.error("Method getAlarmTruck parse error: " + message.getText());
            return;
            // TODO: 23.11.2022 можно сообщение об ошибке сюда добавить
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return;
        }


        try {
            execute(SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(a.toString())
                    .allowSendingWithoutReply(true)
                    .replyToMessageId(message.getMessageId())
                    .build());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }


    private void setCompanyCommand(Message message) {
        TelegramConnectionModel telegramConnectionModel = chatConnections.get(message.getChatId());
        telegramConnectionModel.setPage(1);
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
        InlineKeyboardMarkup inlineKeyboardMarkup = getDefaultMarkup(telegramConnectionModel.getPage());
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


    private InlineKeyboardMarkup getDefaultMarkup(int page) {
        //Создаем кнопки
        List<List<InlineKeyboardButton>> defaultButtons = new ArrayList<>();
        List<Company> companies = companyService.getAll();

        if (page <= 0)
            throw new IllegalArgumentException("page cannot be less than 1. value:" + page);


        defaultButtons.add(Collections.singletonList(
                InlineKeyboardButton.builder().text("<Все>").callbackData(Long.toString(0)).build()
        ));

        int condition = Math.min(companies.size(), companiesOnThePage * page);

        for (int i = (page * companiesOnThePage) - companiesOnThePage; i < condition; i++) {
            defaultButtons.add(Collections.singletonList(
                    InlineKeyboardButton.builder().text(companies.get(i).getShortName()).callbackData(Long.toString(companies.get(i).getId())).build()
            ));
        }
        List<InlineKeyboardButton> prevNext = new ArrayList<>(companiesOnThePage);

        if (page != 1) {
            prevNext.add(InlineKeyboardButton.builder().text("⏪").callbackData("p" + (page - 1)).build());
        }
        if (condition % companiesOnThePage == 0 && page * companiesOnThePage != companies.size()) {
            prevNext.add(InlineKeyboardButton.builder().text("⏩").callbackData("p" + (page + 1)).build());
        }
        defaultButtons.add(prevNext);

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
