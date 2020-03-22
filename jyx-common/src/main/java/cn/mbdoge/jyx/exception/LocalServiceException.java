package cn.mbdoge.jyx.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jyx
 */

public class LocalServiceException extends RuntimeException {
    @Getter
    @Setter
    private Object data;
    private Object[] params;

    public LocalServiceException(String message) {
        this(message, new Object[]{});
    }

    public LocalServiceException(String message, Throwable cause) {
        this(message, null, cause);
    }

    public LocalServiceException(String message, Object[] params) {
        this(message, params, null);
    }

    public LocalServiceException(String message, Object[] params, Throwable cause) {
        super(message, cause);
        this.params = params;
    }


    public Object[] getParams() {
        return this.params;
    }
}
