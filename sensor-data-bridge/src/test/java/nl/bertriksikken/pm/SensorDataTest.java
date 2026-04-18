package nl.bertriksikken.pm;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.time.Instant;

class SensorDataTest {
    @Test
    void testCreationTimestampSet() {
        SensorData data = new SensorData();
        Instant creation = data.getCreationTime();
        assertNotNull(creation, "Creation time should not be null");
        assertTrue(creation.isBefore(Instant.now().plusSeconds(2)), "Creation time is in the future?");
    }

    @Test
    void testPutAndGetValue() {
        SensorData data = new SensorData();
        data.putValue(ESensorItem.TEMPERATURE, 42.5);
        assertTrue(data.hasValue(ESensorItem.TEMPERATURE));
        assertEquals(42.5, data.getValue(ESensorItem.TEMPERATURE));
        assertEquals(42.5, data.get(ESensorItem.TEMPERATURE));
    }

    @Test
    void testPutNullIsIgnored() {
        SensorData data = new SensorData();
        data.putValue(ESensorItem.TEMPERATURE, null);
        assertFalse(data.hasValue(ESensorItem.TEMPERATURE));
    }

    @Test
    void testPutNonFiniteDoubleIgnored() {
        SensorData data = new SensorData();
        data.putValue(ESensorItem.TEMPERATURE, Double.POSITIVE_INFINITY);
        data.putValue(ESensorItem.TEMPERATURE, Double.NaN);
        assertFalse(data.hasValue(ESensorItem.TEMPERATURE));
    }

    @Test
    void testGetValueThrowsOnMissing() {
        SensorData data = new SensorData();
        assertThrows(NullPointerException.class, () -> data.getValue(ESensorItem.HUMIDITY));
    }

    @Test
    void testHasValidReflectsInRange() {
        SensorData data = new SensorData();
        data.putValue(ESensorItem.HUMIDITY, 50.0); // valid
        assertTrue(data.hasValid(ESensorItem.HUMIDITY));
        data.putValue(ESensorItem.HUMIDITY, -1.0); // out of range
        assertFalse(data.hasValid(ESensorItem.HUMIDITY));
    }

    @Test
    void testToStringFormat() {
        SensorData data = new SensorData();
        data.putValue(ESensorItem.PM10, 12.5);
        String s = data.toString();
        assertTrue(s.contains("PM10=12.5ug/m3"), "toString format");
        assertTrue(s.startsWith("{"));
        assertTrue(s.endsWith("}"));
    }
}
