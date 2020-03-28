package cn.mbdoge.jyx.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties("mbdoge.web.api")
public class WebApiProperties {
    //    private List<String> source = Collections.emptyList();
//    private List<String> languages = Collections.emptyList();

    private Message message = new Message();

    @Getter
    @Setter
    public static class Message {
        private List<String> source = Collections.emptyList();
        private List<String> languages = Collections.emptyList();
    }
}
