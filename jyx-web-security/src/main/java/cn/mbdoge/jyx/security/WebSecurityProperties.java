package cn.mbdoge.jyx.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties("mbdoge.web.security")
public class WebSecurityProperties {

    /**
     * 默认加密秘钥 请务必覆盖！
     */
    private String secret = "secret1";
    private String corsUrl = "/api/**";
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
