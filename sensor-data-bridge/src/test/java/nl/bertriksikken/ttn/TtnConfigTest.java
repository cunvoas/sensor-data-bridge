package nl.bertriksikken.ttn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TtnConfigTest {
    @Test
    void testLoadFromYaml() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File file = new File("src/test/resources/sensor-data-bridge.yaml");
        var root = mapper.readTree(file);
        var ttnNode = root.get("ttn");
        assertNotNull(ttnNode, "ttn section should exist");
        TtnConfig config = mapper.treeToValue(ttnNode, TtnConfig.class);
        assertEquals("tcp://eu1.cloud.thethings.network:1883", config.getMqttUrl());
        assertEquals("https://eu1.cloud.thethings.network", config.getIdentityServerUrl());
        assertEquals(1, config.getApps().size());
        var app = config.getApps().get(0);
        assertEquals("sensorcommunity-fake-id", app.getName());
        assertEquals("CAFEFADE9012345678901234567890123456789", app.getKey());
        assertNotNull(app.getDecoder());
        assertEquals("JSON", app.getDecoder().getEncoding().name());
        assertTrue(app.getDecoder().getProperties().isArray());
    }

    @Test
    void testLoadFromYamlExtAndCopyConstructor() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File file = new File("src/test/resources/sensor-data-bridge-ext.yaml");
        var root = mapper.readTree(file);
        var ttnNode = root.get("ttn");
        assertNotNull(ttnNode, "ttn section should exist");
        TtnConfig config = mapper.treeToValue(ttnNode, TtnConfig.class);
        assertEquals(7, config.getApps().size());
        // Only the first app has a decoder, others should get defaultDecoder after copy
        TtnConfig copy = new TtnConfig(config);
        assertEquals(config.getApps().size(), copy.getApps().size());
        for (int i = 0; i < copy.getApps().size(); i++) {
            var app = copy.getApps().get(i);
            assertNotNull(app.getDecoder(), "App " + i + " should have a decoder after copy");
        }
        // Check defaultDecoder encoding via getter
        assertEquals("JSON", copy.getDefaultDecoder().getEncoding().name());
    }
}