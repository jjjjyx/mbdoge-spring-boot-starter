package cn.mbdoge.jyx.web.encrypt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("mbdoge.web.api.encrypt")
public class ApiEncryptProperties {
    private boolean enabled = false;
    private String secret = "";
}
