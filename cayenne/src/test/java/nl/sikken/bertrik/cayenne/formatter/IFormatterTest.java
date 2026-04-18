package nl.sikken.bertrik.cayenne.formatter;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.*;

class IFormatterDummy implements IFormatter {
    public Number[] parse(ByteBuffer bb) { return new Number[]{42}; }
    public String[] format(Number[] values) { return new String[]{"ok"}; }
    public void encode(ByteBuffer bb, Number[] values) { bb.put((byte) values[0].intValue()); }
}

public class IFormatterTest {
    @Test
    void testImplementsInterface() {
        IFormatter formatter = new IFormatterDummy();
        Number[] nums = formatter.parse(ByteBuffer.allocate(1));
        assertEquals(42, nums[0]);
        String[] strs = formatter.format(new Number[]{5});
        assertEquals("ok", strs[0]);
        ByteBuffer buf = ByteBuffer.allocate(1);
        formatter.encode(buf, new Number[]{23});
        buf.flip();
        assertEquals(23, buf.get());
    }
}