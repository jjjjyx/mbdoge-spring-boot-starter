package cn.mbdoge.jyx.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.*;

/**
 * @author jyx
 */
@Getter
@Setter
@ConfigurationProperties("mbdoge.web.security")
public class WebSecurityProperties {

    /**
     * 默认加密秘钥 请务必覆盖！
     */
    private String secret = "secret1";
    private String corsUrl = "/api/**";
    private List<String> corsAllowOrigin = new ArrayList<>();
    private List<String> corsAllowMethod = Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH");
    private List<String> corsAllowHeader = Collections.singletonList("*");
    private List<String> corsExposedHeaders = Arrays.asList("Accept-Language", "Authorization", "Content-Disposition", "Content-Length");
    private boolean corsCredentials = false;
    private String apiPrefix = "/api";

    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Jwt {
        /**
         * 默认加密秘钥 请务必覆盖！
         */
        private String secret = "secret2";
        /**
         * 1 小时
         */
        private long expiration = 3600L;

        //    private String headerKey = "Authorization";
        private String redisKeyPrefix = "mbdoge:jti:";
        /**
         * 用户签名最大个数
         */
        private int jitMax = 3;

    }

}
