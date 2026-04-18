package nl.sikken.bertrik.cayenne;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for ECayenneItem.
 */
public class ECayenneItemTest {

    @Test
    void testParseValidType() throws CayenneException {
        assertEquals(ECayenneItem.DIGITAL_INPUT, ECayenneItem.parse(0));
        assertEquals(ECayenneItem.DIGITAL_OUTPUT, ECayenneItem.parse(1));
        assertEquals(ECayenneItem.ANALOG_INPUT, ECayenneItem.parse(2));
    }

    @Test
    void testParseInvalidTypeThrows() {
        Exception exception = assertThrows(CayenneException.class, () -> ECayenneItem.parse(999));
        assertTrue(exception.getMessage().contains("Invalid cayenne type"));
    }

    @Test
    void testGetType() {
        assertEquals(0, ECayenneItem.DIGITAL_INPUT.getType());
        assertEquals(136, ECayenneItem.GPS_LOCATION.getType());
    }

    @Test
    void testFormatterRoundTrip() {
        ByteBuffer bb = ByteBuffer.allocate(32);
        Number[] values = new Number[] { 10 };
        ECayenneItem.DIGITAL_INPUT.encode(bb, values);
        bb.flip();
        Number[] parsed = ECayenneItem.DIGITAL_INPUT.parse(bb);
        assertNotNull(parsed);
        assertEquals(10, parsed[0]);
    }
}
