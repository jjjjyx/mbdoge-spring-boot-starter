package cn.mbdoge.jyx.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("mbdoge.web.jwt")
public class JwtProperties {
    /**
     * 默认加密秘钥 请务必覆盖！
     */
    private String secret = "secret2";
    /**
     * 1 小时
     */
    private long expiration = 3600L;

//    private String headerKey = "Authorization";
//    private String redisKeyPrefix = "mbdoge:jti:";

}
