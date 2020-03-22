package cn.mbdoge.jyx.web.encrypt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("mbdoge.api.encrypt")
public class ApiEncryptProperties {

    private String secret = "";
}