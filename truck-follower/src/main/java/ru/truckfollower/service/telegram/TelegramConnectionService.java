package ru.truckfollower.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.TelegramConnection;
import ru.truckfollower.exception.EntityNotFoundException;
import ru.truckfollower.model.TelegramConnectionModel;
import ru.truckfollower.repo.TelegramConnectionRepo;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class TelegramConnectionService {

    private final TelegramConnectionRepo telegramConnectionRepo;

    @Autowired
    public TelegramConnectionService(TelegramConnectionRepo telegramConnectionRepo) {
        this.telegramConnectionRepo = telegramConnectionRepo;
    }

    public List<TelegramConnection> getAll() {
        return telegramConnectionRepo.findAll();
    }

    public List<TelegramConnection> getAllByAuthorized() {
        return telegramConnectionRepo.getAllByAuthorized(true);
    }

    public void deleteByChatID(Long chatId) {

        try {
            telegramConnectionRepo.delete(getByChatId(chatId));
        } catch (EntityNotFoundException e) {

        }
    }

    public TelegramConnection getByChatId(Long chatId) throws EntityNotFoundException {
        Optional<TelegramConnection> telegramConnection = telegramConnectionRepo.findFirstByChatId(chatId);

        if (telegramConnection.isEmpty()) {
            throw new EntityNotFoundException("TelegramConnection entity not found by chatId: " + chatId);
        }
        return telegramConnection.get();
    }



    public TelegramConnection save(TelegramConnection telegramConnection) {

        if(telegramConnection.getActivatedCompanies()==null)
            telegramConnection.setActivatedCompanies("{}");

        try {
            TelegramConnection bd =getByChatId(telegramConnection.getChatId());
            telegramConnection.setId(bd.getId());
            telegramConnectionRepo.save(telegramConnection);
        } catch (EntityNotFoundException e) {
            telegramConnectionRepo.save(telegramConnection);
        }

        return telegramConnection;
    }

    public TelegramConnection toEntity(TelegramConnectionModel telegramConnectionModel) {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (!telegramConnectionModel.getActivatedCompanies().isEmpty()) {
            telegramConnectionModel.getActivatedCompanies().forEach(c -> sb.append(c).append(","));
            sb.setLength(sb.length() - 1);
        }
        sb.append("}");

        return TelegramConnection.builder()
                .id(telegramConnectionModel.getId())
                .chatId(telegramConnectionModel.getChatId())
                .authKey(telegramConnectionModel.getAuthKey())
                .firstAuthTime(telegramConnectionModel.getFirstAuthTime())
                .activatedCompanies(sb.toString())
                .authorized(telegramConnectionModel.getAuthorized())
                .build();
    }


    public TelegramConnectionModel toModel(TelegramConnection connection) {

        Set<Long> activatedCompanies = new HashSet<>();
        String[] s =connection.getActivatedCompanies().split("(\\{)|(,)|(\\})");

        for (int i = 1; i < s.length; i++) {
            activatedCompanies.add(Long.parseLong(s[i]));
        }

        return TelegramConnectionModel.builder()
                .id(connection.getId())
                .chatId(connection.getChatId())
                .authKey(connection.getAuthKey())
                .authorized(connection.getAuthorized())
                .firstAuthTime(connection.getFirstAuthTime())
                .activatedCompanies(activatedCompanies)
                .page(1)
                .build();
    }
}
