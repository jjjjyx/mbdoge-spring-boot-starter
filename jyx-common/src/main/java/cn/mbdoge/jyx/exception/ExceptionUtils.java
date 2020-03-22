package cn.mbdoge.jyx.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

/**
 * @author jyx
 */
public final class ExceptionUtils {
    private ExceptionUtils() {
    }

    /**
     * 获取异常深的 getCause
     *
     * @param e Throwable
     * @return Cause
     */
    public static Throwable getCause(Throwable e) {
        Objects.requireNonNull(e);

        Throwable throwable = e;
        Throwable last = e;

        while (throwable != null) {
            last = throwable;
            throwable = throwable.getCause();
        }

        return last;
    }

    public static String getExceptionToString(Throwable e) {
        if (e == null){
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}
