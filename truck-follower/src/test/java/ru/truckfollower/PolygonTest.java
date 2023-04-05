package ru.truckfollower;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgis.Point;
import ru.truckfollower.service.polygon.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PolygonTest {

    private static Polygon polygon;


    @Test
    public void testConstructor() {
        List<Point> polygon = Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 0));
        Polygon p = new Polygon(polygon);
        assertTrue(p.getPolygon().equals(polygon));
    }

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

        assertTrue(polygon.contains(p1));
        assertFalse(polygon.contains(p2));
        assertFalse(polygon.contains(p3));
        assertFalse(polygon.contains(p4));
        assertTrue(polygon.contains(p5));
    }

    @Test
    public void testContainsPointInsidePolygon() {
        List<Point> polygon = Arrays.asList(new Point(0, 0), new Point(0, 2), new Point(2, 2), new Point(2, 0));
        Polygon p = new Polygon(polygon);
        assertTrue(p.contains(new Point(1, 1)));
    }

    @Test
    public void testContainsPointOutsidePolygon() {
        List<Point> polygon = Arrays.asList(new Point(0, 0), new Point(0, 2), new Point(2, 2), new Point(2, 0));
        Polygon p = new Polygon(polygon);
        assertFalse(p.contains(new Point(3, 3)));
    }

    @Test
    public void testContainsPointOnPolygonBoundary() {
        List<Point> polygon = Arrays.asList(new Point(0, 0), new Point(0, 2), new Point(2, 2), new Point(2, 0));
        Polygon p = new Polygon(polygon);
        assertFalse(p.contains(new Point(1, 0)));
    }


}
