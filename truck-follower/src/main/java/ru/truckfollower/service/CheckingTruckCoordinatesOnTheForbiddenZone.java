package ru.truckfollower.service;

import org.springframework.stereotype.Service;
import ru.truckfollower.entity.Company;
import ru.truckfollower.model.TruckRabbitModel;


@Service
public class CheckingTruckCoordinatesOnTheForbiddenZone {


    public void check(TruckRabbitModel truckRabbitModel, Company company_id) {
        // TODO: 05.11.2022 сделать таблицу и репозиторий для запретных зон. Хранить их где-нибудь, запретные зоны шедулить кажждый час или сделать контроллер по ручной синхронизации

        //сделать forbidenZoneService, в котором также будет содержаться мапа с ключом company_id и value Лист из запретных зон
    }
}
