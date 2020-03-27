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
public class SecurityProperties {

    /**
     * 默认加密秘钥 请务必覆盖！
     */
    private String secret = "secret1";
    private String corsUrl = "/api/**";
    private Map<String, List<String>> permitAll = new HashMap<>();

}
