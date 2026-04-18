package nl.sikken.bertrik.cayenne;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for CayenneItem.
 */
public class CayenneItemTest {

    @Test
    void testConstructorAndGetters() {
        CayenneItem item = new CayenneItem(4, ECayenneItem.TEMPERATURE, 19.5);
        assertEquals(4, item.getChannel());
        assertEquals(ECayenneItem.TEMPERATURE, item.getType());
        assertEquals(19.5, item.getValue().doubleValue(), 0.0001);
        assertNotNull(item.getValues());
    }

    @Test
    void testToStringNotNull() {
        CayenneItem item = new CayenneItem(1, ECayenneItem.ILLUMINANCE, new Number[] { 1000 });
        assertNotNull(item.toString());
    }

    @Test
    void testEncodeDecodeRoundtrip() throws CayenneException {
        CayenneItem item = new CayenneItem(2, ECayenneItem.DIGITAL_INPUT, 7);
        ByteBuffer bb = ByteBuffer.allocate(16);
        item.encode(bb);
        bb.flip();
        CayenneItem decoded = CayenneItem.parse(bb);
        assertEquals(item.getChannel(), decoded.getChannel());
        assertEquals(item.getType(), decoded.getType());
        assertEquals(item.getValue(), decoded.getValue());
    }

    @Test
    void testParsePacked() throws CayenneException {
        CayenneItem item = new CayenneItem(6, ECayenneItem.DIGITAL_OUTPUT, 9);
        ByteBuffer bb = ByteBuffer.allocate(16);
        item.encode(bb);
        bb.flip();
        bb.get(); // channel
        CayenneItem packed = CayenneItem.parsePacked(bb, 6);
        assertEquals(6, packed.getChannel());
        assertEquals(ECayenneItem.DIGITAL_OUTPUT, packed.getType());
        assertEquals(9, packed.getValue());
    }
}
