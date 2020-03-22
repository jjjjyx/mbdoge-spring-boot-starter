package cn.mbdoge.jyx.web.language;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties("mbdoge.api.message")
public class ApiMessageProperties {
    private List<String> source = Collections.emptyList();
    private List<String> languages = Collections.emptyList();
}
