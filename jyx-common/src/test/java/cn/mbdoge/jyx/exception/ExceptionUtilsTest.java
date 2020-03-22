package cn.mbdoge.jyx.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionUtilsTest {

    @Test
    void getCause() {
        Exception ex = new RuntimeException("true reason");
        LocalServiceException e = new LocalServiceException("xxxx", ex);

        Throwable cause = ExceptionUtils.getCause(e);
        assertEquals(cause, ex);


        LocalServiceException e2 = new LocalServiceException("xxxx");
        cause = ExceptionUtils.getCause(e2);
        assertEquals(cause, e2);


        LocalServiceException e3 = new LocalServiceException("xxxx", new Object[]{});
        cause = ExceptionUtils.getCause(e3);
        assertEquals(cause, e3);
        Object[] params = e3.getParams();
        assertEquals(0, params.length);


        Assertions.assertThrows(NullPointerException.class, () -> {
            Throwable cause1 = ExceptionUtils.getCause(null);
        });
    }

    @Test
    void getExceptionToString() {
        Exception ex = null;
        String rest = ExceptionUtils.getExceptionToString(ex);
        assertEquals("", rest);

        ex = new RuntimeException("true reason");
        rest = ExceptionUtils.getExceptionToString(ex);
        assertNotEquals("", rest);

    }
}