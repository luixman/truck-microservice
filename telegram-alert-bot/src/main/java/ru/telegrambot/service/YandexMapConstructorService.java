package ru.telegrambot.service;

import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.telegrambot.entity.Alarm;
import ru.telegrambot.entity.ForbiddenZone;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

@Service
public class YandexMapConstructorService {

    public static final String POLYGON_FILL_COLOR = "bd09097F";
    public static final String ROW_COLOR = "bd0909FF";
    public static final Integer ROW_WIDTH = 5;
    private final ForbiddenZoneService forbiddenZoneService;

    @Autowired
    public YandexMapConstructorService(ForbiddenZoneService forbiddenZoneService) {
        this.forbiddenZoneService = forbiddenZoneService;
    }

    public InputStream getInStreamByURL(URL url) throws IOException {
        return url.openConnection().getInputStream();
    }

    public URL getURLByAlarm(Alarm a) {
        ForbiddenZone forbiddenZone = a.getForbiddenZone();

        Geometry g = forbiddenZoneService.getGeometryByForbiddenZone(forbiddenZone);

        StringBuilder sb = new StringBuilder();

        //префикс ссылки
        sb.append("https://static-maps.yandex.ru/1.x/?l=map&");

        //настройка визуала
        sb.append("pl=f:" + POLYGON_FILL_COLOR + ",c:" + ROW_COLOR);
        sb.append(",w:").append(ROW_WIDTH).append(",");

        for (int i = 0; i < g.numPoints(); i++) {
            Point p = g.getPoint(i);
            sb.append(p.getY()).append(",").append(p.getX()).append(",");
        }
        sb.setLength(sb.length() - 1);

        //точка въезда
        sb.append("&pt=").append(a.getPointEntry().getY()).append(",").append(a.getPointEntry().getX()).append(",");
        sb.append("pm2rdl");

        //Точка выезда
        if (a.getPointExit() != null) {
            sb.append("~");
            sb.append(a.getPointExit().getY()).append(",").append(a.getPointExit().getX()).append(",");
            sb.append("pm2gnl");
        }

        URL url = null;
        try {
            url = new URL(sb.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return url;
    }

}
