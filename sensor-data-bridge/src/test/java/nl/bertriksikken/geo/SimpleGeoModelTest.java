package nl.bertriksikken.geo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleGeoModelTest {
    private final SimpleGeoModel geoModel = new SimpleGeoModel();

    @Test
    void testSamePoint() {
        double[] p = {0.0, 0.0};
        assertEquals(0.0, geoModel.distance(p, p), 0.00001, "Distance must be 0 for identical points");
    }

    @Test
    void testOneDegreeNorth() {
        double[] p1 = {0.0, 0.0};
        double[] p2 = {1.0, 0.0};
        double result = geoModel.distance(p1, p2);
        // 1 degree latitude should be approx circumference/360
        assertTrue(result > 111000 && result < 112500, "Distance for 1 deg latitude is about 111 km: " + result);
    }

    @Test
    void testOneDegreeEastOnEquator() {
        double[] p1 = {0.0, 0.0};
        double[] p2 = {0.0, 1.0};
        double result = geoModel.distance(p1, p2);
        // 1 degree longitude at equator should be approx circumference/360
        assertTrue(result > 111000 && result < 112500, "Distance for 1 deg longitude at equator is about 111 km: " + result);
    }
    

    @Test
    void testOneDegreeDateChangingLineOnEquator() {
        double[] p1 = {0.0, -179.5};
        double[] p2 = {0.0,  179.5};
        double result = geoModel.distance(p1, p2);
        // 1 degree longitude at equator should be approx circumference/360
        assertTrue(result > 111000 && result < 112500, "Distance for 1 deg longitude at equator is about 111 km: " + result);
    }

    @Test
    void testNegativeLatitude() {
        double[] p1 = {-45.0, 0.0};
        double[] p2 = {-44.0, 0.0};
        double result = geoModel.distance(p1, p2);
        assertTrue(result > 111000 && result < 112500, "Distance for 1 deg latitude at -45° is about 111 km: " + result);
    }
    
    /**
     * Vérifie que la distance entre deux points très proches du pôle Nord (89.9°N, 0° et 89.9°N, 1°)
     * est très faible (moins de 2 km), car la distance entre deux longitudes diminue fortement près des pôles.
     * Ce test valide le comportement du calcul de distance dans les cas extrêmes de latitude.
     */
    @Test
    void test89DegreeNorthPole() {
        double[] p1 = {89.9, 0.0};
        double[] p2 = {89.9, 1.0};
        double result = geoModel.distance(p1, p2);
        assertTrue(result > 0 && result < 2000, "Distance for 1 deg on pole is less than 2 km: " + result);
    }
    

    @Test
    void test90DegreeNorthPole() {
        double[] p1 = {90, 0.0};
        double[] p2 = {90, 1.0};
        double result = geoModel.distance(p1, p2);
        // test between 0 and 1mm to evict floating point error
        assertTrue(result > 0 && result < 1e-3, "Distance for 1 deg on pole is 0 km: " + result);
    }
}