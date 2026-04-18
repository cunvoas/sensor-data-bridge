package nl.bertriksikken.pm.sps30;

import nl.bertriksikken.pm.PayloadParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Sps30MessageTest {
    // Validates correct parsing with a full (happy path) payload
    @Test
    void testParseValidPayload() throws Exception {
        // Arrange: Build a valid payload (10 shorts, big endian)
        byte[] data = new byte[20];
        // Values for pm1.0, pm2.5, pm4.0, pm10, n0.5, n1, n2.5, n4, n10, tps
        int[] values = {123, 234, 345, 456, 1000, 2000, 3000, 4000, 5000, 678};
        for (int i = 0; i < values.length; i++) {
            int v = values[i];
            data[2*i] = (byte)((v >> 8) & 0xFF);
            data[2*i+1] = (byte)(v & 0xFF);
        }
        // Act
        Sps30Message msg = Sps30Message.parse(data);
        // Assert: Scaled values
        assertEquals(12.3, msg.getPm1_0(), 1e-3);
        assertEquals(23.4, msg.getPm2_5(), 1e-3);
        assertEquals(34.5, msg.getPm4_0(), 1e-3);
        assertEquals(45.6, msg.getPm10(), 1e-3);
        assertEquals(1000, msg.getN0_5(), 1e-3);
        assertEquals(2000, msg.getN1_0(), 1e-3);
        assertEquals(3000, msg.getN2_5(), 1e-3);
        assertEquals(4000, msg.getN4_0(), 1e-3);
        assertEquals(5000, msg.getN10(), 1e-3);
        assertEquals(0.678, msg.getTps(), 1e-3);
    }

    // Validates exception for payload too short
    @Test
    void testParsePayloadTooShort() {
        byte[] shortPayload = new byte[5];
        assertThrows(PayloadParseException.class, () -> Sps30Message.parse(shortPayload));
    }

    // Minor: Test all-zero payload parses (should yield all zero fields)
    @Test
    void testAllZerosPayload() throws Exception {
        byte[] zeros = new byte[20];
        Sps30Message msg = Sps30Message.parse(zeros);
        assertEquals(0, msg.getPm1_0(), 1e-8);
        assertEquals(0, msg.getPm2_5(), 1e-8);
        assertEquals(0, msg.getPm4_0(), 1e-8);
        assertEquals(0, msg.getPm10(), 1e-8);
        assertEquals(0, msg.getN0_5(), 1e-8);
        assertEquals(0, msg.getN1_0(), 1e-8);
        assertEquals(0, msg.getN2_5(), 1e-8);
        assertEquals(0, msg.getN4_0(), 1e-8);
        assertEquals(0, msg.getN10(), 1e-8);
        assertEquals(0, msg.getTps(), 1e-8);
    }
}
