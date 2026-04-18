package nl.sikken.bertrik.cayenne.formatter;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.*;

class IntegerFormatterTest {

    @Test
    void testRoundtripEncodeParseUnsigned() {
        IntegerFormatter formatter = new IntegerFormatter(3, 1, false);
        Number[] input = {1, 42, 255};
        ByteBuffer buffer = ByteBuffer.allocate(3);
        formatter.encode(buffer, input);
        buffer.flip();
        Number[] output = formatter.parse(buffer);
        assertArrayEquals(input, output);
    }

    @Test
    void testRoundtripEncodeParseSigned() {
        IntegerFormatter formatter = new IntegerFormatter(2, 1, true);
        Number[] input = {-128, 127};
        ByteBuffer buffer = ByteBuffer.allocate(2);
        formatter.encode(buffer, input);
        buffer.flip();
        Number[] output = formatter.parse(buffer);
        assertArrayEquals(input, output);
    }

    @Test
    void testFormat() {
        IntegerFormatter formatter = new IntegerFormatter(2, 1, false);
        Number[] values = {123, 45};
        String[] formatted = formatter.format(values);
        assertEquals("123", formatted[0]);
        assertEquals("45", formatted[1]);
    }
}