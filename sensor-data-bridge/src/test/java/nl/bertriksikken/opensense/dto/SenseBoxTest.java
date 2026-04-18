package nl.bertriksikken.opensense.dto;

import org.junit.jupiter.api.Test;

class SenseBoxTest {

    @Test
    void testToStringEmptySensors() throws Exception {
        SenseBox box = new SenseBox();
        java.lang.reflect.Field nameField = SenseBox.class.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(box, "box1");
        java.lang.reflect.Field sensorsField = SenseBox.class.getDeclaredField("sensors");
        sensorsField.setAccessible(true);
        sensorsField.set(box, java.util.Collections.emptyList());
        org.junit.jupiter.api.Assertions.assertEquals("{name=box1,sensors=[]}", box.toString());
    }

    @Test
    void testToStringWithOneSensor() throws Exception {
        Sensor s = new Sensor();
        // Set required fields for deterministic Sensor.toString()
        java.lang.reflect.Field idField = Sensor.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(s, "s1");
        java.lang.reflect.Field typeField = Sensor.class.getDeclaredField("sensorType");
        typeField.setAccessible(true);
        typeField.set(s, "typeX");
        java.lang.reflect.Field titleField = Sensor.class.getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(s, "sensorTitle");
        java.lang.reflect.Field unitField = Sensor.class.getDeclaredField("unit");
        unitField.setAccessible(true);
        unitField.set(s, "unitX");
        // measurement left null
        SenseBox box = new SenseBox();
        java.lang.reflect.Field nameField = SenseBox.class.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(box, "box2");
        java.lang.reflect.Field sensorsField = SenseBox.class.getDeclaredField("sensors");
        sensorsField.setAccessible(true);
        java.util.List<Sensor> sensors = java.util.Arrays.asList(s);
        sensorsField.set(box, sensors);
        String expected = "{name=box2,sensors=[{id=s1,sensorType=typeX,title=sensorTitle,unit=unitX,measurement=null}]}";
        org.junit.jupiter.api.Assertions.assertEquals(expected, box.toString());
    }
}

