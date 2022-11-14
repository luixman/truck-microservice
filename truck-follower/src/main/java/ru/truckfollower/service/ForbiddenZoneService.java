package ru.truckfollower.service;


import lombok.extern.slf4j.Slf4j;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.ForbiddenZone;
import ru.truckfollower.model.ForbiddenZoneModel;
import ru.truckfollower.repo.ForbiddenZoneRepo;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ForbiddenZoneService {

    private final ForbiddenZoneRepo forbiddenZoneRepo;
    @Autowired
    public ForbiddenZoneService(ForbiddenZoneRepo forbiddenZoneRepo) {
        this.forbiddenZoneRepo = forbiddenZoneRepo;
    }

    public List<ForbiddenZone> getAll() {
        return forbiddenZoneRepo.findAll();
    }

    public ForbiddenZoneModel toModel(ForbiddenZone forbiddenZone) {

        Geometry g = null;
        try {
            g = new PGgeometry(forbiddenZone.getPolygon()).getGeometry();
        } catch (SQLException e) {
            //log.error("Object" + forbiddenZone + " has an exception");
            g = new Point(0, 0);
            throw new RuntimeException(e);
        }

        return ForbiddenZoneModel
                .builder()
                .id(forbiddenZone.getId())
                .zoneName(forbiddenZone.getZoneName())
                .companyId(forbiddenZone.getCompanyId())
                .geometry(g)
                .build();
    }

    public Optional<ForbiddenZone> toEntity(ForbiddenZoneModel forbiddenZoneModel) {
        return forbiddenZoneRepo.findById(forbiddenZoneModel.getId());
    }
}
