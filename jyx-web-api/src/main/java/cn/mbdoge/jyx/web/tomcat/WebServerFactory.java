package cn.mbdoge.jyx.web.tomcat;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;


public class WebServerFactory extends TomcatServletWebServerFactory {
    public WebServerFactory() {
        addConnectorCustomizers(connector -> {
            // https://stackoverflow.com/questions/51703746/setting-relaxedquerychars-for-embedded-tomcat
            ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            connector.setAttribute("relaxedPathChars", "<>[\\]^`{|}");
            connector.setAttribute("relaxedQueryChars", "<>[\\]^`{|}");
        });
    }
}
