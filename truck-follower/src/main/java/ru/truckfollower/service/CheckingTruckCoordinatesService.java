package ru.truckfollower.service;

import lombok.extern.slf4j.Slf4j;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.Company;
import ru.truckfollower.entity.ForbiddenZone;
import ru.truckfollower.entity.Truck;
import ru.truckfollower.model.ForbiddenZoneModel;
import ru.truckfollower.model.TruckRabbitModel;
import ru.truckfollower.service.polygon.Polygon;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.*;


@Service
@Slf4j
public class CheckingTruckCoordinatesService {

    private final ForbiddenZoneService forbiddenZoneService;
    private final CompanyService companyService;

    //private final TruckService truckService;


    //мапа ид компании, валуе лист запретных зон
    private Map<Long, List<ForbiddenZoneModel>> companyZones;

    @Autowired
    public CheckingTruckCoordinatesService(ForbiddenZoneService forbiddenZoneService, CompanyService companyService) {
        this.forbiddenZoneService = forbiddenZoneService;
        this.companyService = companyService;
        //this.truckService = truckService;
    }

    @PostConstruct
    private void initialize() {
        Map<Long, List<ForbiddenZoneModel>> map = new HashMap<>();

        companyService.getAll().forEach(x -> map.put(x.getId(), new ArrayList<>()));

        List<ForbiddenZone> forbiddenZoneList = forbiddenZoneService.getAll();

        List<ForbiddenZoneModel> forbiddenZoneModelList = new ArrayList<>();

        for (ForbiddenZone forbiddenZone : forbiddenZoneList) {

            ForbiddenZoneModel model = forbiddenZoneService.toModel(forbiddenZone);

            if (!map.containsKey(model.getCompanyId())) {
                log.error(forbiddenZone + " has no connection with the company");
            } else
                map.get(model.getCompanyId()).add(model);
        }

        companyZones = map;

    }


    public void check(TruckRabbitModel truckRabbitModel, Company company) {
        // TODO: 05.11.2022 сделать таблицу и репозиторий для запретных зон. Хранить их где-нибудь, запретные зоны шедулить кажждый час или сделать контроллер по ручной синхронизации
        List<ForbiddenZoneModel> companyList = companyZones.get(company.getId());

        if (companyList == null) {
            log.warn(company + " not registered in db");
            return;
        }

        //log.info(truckRabbitModel.getX()+" "+ truckRabbitModel.getY()+" "+ truckRabbitModel.getUid()+": "+ company.getFullName());

        for (ForbiddenZoneModel forbiddenZoneModel : companyList) {
            Polygon polygon = Polygon.geometryToPolygon(forbiddenZoneModel.getGeometry());
            boolean result = polygon.contains(new Point(truckRabbitModel.getX(), truckRabbitModel.getY()));
            if (result) {
                //Optional<Truck> truck =truckService.rabbitModelToEntity(truckRabbitModel);
                //log.warn(truckRabbitModel+"ЕБАТЬ ОНО В ЗАПРЕТНОЙ ЗОНЕ");
            }
        }

        // TODO: 05.11.2022 логика проверки

        //сделать forbidenZoneService, в котором также будет содержаться мапа с ключом company_id и value Лист из запретных зон
    }
}
