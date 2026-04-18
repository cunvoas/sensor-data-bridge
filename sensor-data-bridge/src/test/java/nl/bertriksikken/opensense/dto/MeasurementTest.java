package nl.bertriksikken.opensense.dto;

import org.junit.jupiter.api.Test;

class MeasurementTest {

    @Test
    void testGetValue() throws Exception {
        Measurement m = new Measurement();
        java.lang.reflect.Field valueField = Measurement.class.getDeclaredField("value");
        valueField.setAccessible(true);
        valueField.set(m, "42.0");
        org.junit.jupiter.api.Assertions.assertEquals("42.0", m.getValue());
    }

    @Test
    void testGetCreatedAt() throws Exception {
        Measurement m = new Measurement();
        java.lang.reflect.Field f = Measurement.class.getDeclaredField("createdAt");
        f.setAccessible(true);
        f.set(m, "2021-01-01T00:00:00Z");
        org.junit.jupiter.api.Assertions.assertEquals("2021-01-01T00:00:00Z", m.getCreatedAt());
    }

    @Test
    void testToString() throws Exception {
        Measurement m = new Measurement();
        java.lang.reflect.Field field = Measurement.class.getDeclaredField("value");
        field.setAccessible(true);
        field.set(m, "3.14");
        org.junit.jupiter.api.Assertions.assertEquals("3.14", m.toString());
    }
}

