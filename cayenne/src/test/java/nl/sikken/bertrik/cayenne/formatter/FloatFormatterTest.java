package nl.sikken.bertrik.cayenne.formatter;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for FloatFormatter.
 */
public class FloatFormatterTest {
    @Test
    void testFormatAndParseAndEncode() {
        FloatFormatter f = new FloatFormatter(2, 2, 0.1, true);
        Number[] input = new Number[] {1.2, -4.5};
        String[] formatted = f.format(input);
        assertEquals(2, formatted.length);
        assertEquals("1.2", formatted[0].replace(",", "."));
        assertTrue(formatted[1].contains("-4.5"));

        ByteBuffer buf = ByteBuffer.allocate(10);
        f.encode(buf, input);
        buf.flip();
        Number[] parsed = f.parse(buf);
        assertEquals(input.length, parsed.length);
        assertEquals(Math.round(input[0].doubleValue()*10), Math.round(parsed[0].doubleValue()*10));
        assertEquals(Math.round(input[1].doubleValue()*10), Math.round(parsed[1].doubleValue()*10));
    }
}
