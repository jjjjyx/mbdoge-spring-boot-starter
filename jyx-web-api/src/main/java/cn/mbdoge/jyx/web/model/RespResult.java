package cn.mbdoge.jyx.web.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author jyx
 */
@Getter
@Setter
public class RespResult<T> {
    static final int SUCCESS = 0;
    static final int ERROR = 1;
    static final int WARNING = 2;
    static final int INFO = 3;


    public static RespResult<Object> success() {
        return new RespResult<>(SUCCESS);
    }

    public static RespResult<Object> info(String msg) {
        return new RespResult<>(INFO, msg);
    }

    public static RespResult<Object> error(String msg) {
        return new RespResult<>(ERROR, msg);
    }

    public static RespResult<Object> warning(String msg) {
        return new RespResult<>(WARNING, msg);
    }

    public static <T> RespResult<T> success(T data) {
        return new RespResult<>(SUCCESS, "", data);
    }

    public static <T> RespResult<T> success(String msg, T data) {
        return new RespResult<>(SUCCESS, msg, data);
    }

    public static <T> RespResult<T> info(String msg, T data) {
        return new RespResult<>(INFO, msg, data);
    }

    public static <T> RespResult<T> error(String msg, T data) {
        return new RespResult<>(ERROR, msg, data);
    }

    public static <T> RespResult<T> warning(String msg, T data) {
        return new RespResult<>(WARNING, msg, data);
    }

    private String msg;
    private int level;
    private T data;
    private Date timestamp;

    public RespResult(int level) {
        this(level, "");
    }

    public RespResult(int level, String msg) {
        this(level, msg, null);
    }

    public RespResult(int code, String msg, T data) {
        this.level = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = new Date();
    }

}
