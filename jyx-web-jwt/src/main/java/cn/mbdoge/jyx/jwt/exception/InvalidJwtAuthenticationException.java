package cn.mbdoge.jyx.jwt.exception;

/**
 * @author jyx
 */
public class InvalidJwtAuthenticationException extends RuntimeException {

    public InvalidJwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
