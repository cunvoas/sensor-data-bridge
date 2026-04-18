package nl.sikken.bertrik.cayenne.formatter;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.*;

class DummyFormatter extends BaseFormatter {
    public Number[] parse(ByteBuffer bb) { return new Number[0]; }
    public String[] format(Number[] values) { return new String[0]; }
    public void encode(ByteBuffer bb, Number[] values) {}

    public int callGetValue(ByteBuffer bb, int n, boolean signed) {
        return getValue(bb, n, signed);
    }
    public void callPutValue(ByteBuffer bb, int n, int value) {
        putValue(bb, n, value);
    }
}

public class BaseFormatterTest {
    @Test
    void testGetValueUnsigned() {
        DummyFormatter fmt = new DummyFormatter();
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0x01, 0x02});
        int value = fmt.callGetValue(bb, 2, false);
        assertEquals(0x0102, value);
    }
    @Test
    void testGetValueSigned() {
        DummyFormatter fmt = new DummyFormatter();
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{(byte)0xFF, (byte)0x80});
        int value = fmt.callGetValue(bb, 2, true);
        assertEquals(-128, value); // 0xFF80 = -128 signed 16-bit
    }
    @Test
    void testPutValue() {
        DummyFormatter fmt = new DummyFormatter();
        ByteBuffer bb = ByteBuffer.allocate(2);
        fmt.callPutValue(bb, 2, 0x1234);
        bb.flip();
        assertEquals((byte)0x12, bb.get());
        assertEquals((byte)0x34, bb.get());
    }
}
