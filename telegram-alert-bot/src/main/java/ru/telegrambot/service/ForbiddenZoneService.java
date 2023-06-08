package ru.telegrambot.service;

import lombok.SneakyThrows;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.telegrambot.entity.ForbiddenZone;
import ru.telegrambot.exception.EntityNotFoundException;
import ru.telegrambot.repo.ForbiddenZoneRepo;

import java.sql.SQLException;
import java.util.Optional;

@Service
public class ForbiddenZoneService {

    @SneakyThrows
    public Geometry getGeometryByForbiddenZone(ForbiddenZone forbiddenZone) {

        return new PGgeometry(forbiddenZone.getPolygon()).getGeometry();
    }

}
