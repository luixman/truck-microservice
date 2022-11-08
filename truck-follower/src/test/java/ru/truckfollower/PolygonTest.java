package ru.truckfollower;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgis.Point;
import ru.truckfollower.service.polygon.Polygon;

import java.util.ArrayList;
import java.util.List;

public class PolygonTest {

    private static Polygon polygon;


    @BeforeAll
    public static void beforeAll() {

        List<Point> list = new ArrayList<>();
        list.add(new Point(-7.4, -6.4));
        list.add(new Point(-7.8, 8.7));
        list.add(new Point(7.9, 5.4));
        list.add(new Point(9.9, -5.4));
        polygon = new Polygon(list);
    }


    @Test
    public void containsTest() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(9.0, 9.0);
        Point p3 = new Point(-11, 0);
        Point p4 = new Point(4, 8.5);
        Point p5 = new Point(7.89999, 5.39999);

        Assertions.assertTrue(polygon.contains(p1));
        Assertions.assertFalse(polygon.contains(p2));
        Assertions.assertFalse(polygon.contains(p3));
        Assertions.assertFalse(polygon.contains(p4));
        Assertions.assertTrue(polygon.contains(p5));

    }


}
