package nl.sikken.bertrik.cayenne;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for CayenneException.
 */
public class CayenneExceptionTest {

    @Test
    void testExceptionMessage() {
        String testMessage = "Test CayenneException message";
        CayenneException ex = new CayenneException(testMessage);
        assertEquals(testMessage, ex.getMessage());
    }

    @Test
    void testExceptionCause() {
        Throwable cause = new RuntimeException("cause");
        CayenneException ex = new CayenneException(cause);
        assertEquals(cause, ex.getCause());
    }
}
