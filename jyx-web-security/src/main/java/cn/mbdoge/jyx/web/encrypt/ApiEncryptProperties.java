package cn.mbdoge.jyx.web.encrypt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author jyx
 */
@Getter
@Setter
@ConfigurationProperties("mbdoge.web.security.api.encrypt")
public class ApiEncryptProperties {
    private boolean enabled = false;
    private String secret = "";
}
