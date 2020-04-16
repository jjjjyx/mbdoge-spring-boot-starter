package cn.mbdoge.jyx.exception;

/**
 * 请求频繁
 */
public class RequestLimitException extends RuntimeException {
    public RequestLimitException() {
    }

    public RequestLimitException(String message) {
        super(message);
    }
}
