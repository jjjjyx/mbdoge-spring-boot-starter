package cn.mbdoge.jyx.web;


import cn.mbdoge.jyx.web.handler.ControllerHandlerAdvice;
import cn.mbdoge.jyx.web.language.LanguageConfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


import java.util.*;

/**
 * 主要是做一些常用的全局配置，减少重复的代码
 *
 * @author jyx
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 11)
@Import({LanguageConfigure.class, ControllerHandlerAdvice.class})
public class WebApiAutoConfigure {


    public WebApiAutoConfigure() {
//        this.webApiProperties = webApiProperties;
    }

    /**
     * 让验证时快熟失效
     * 当验证多个字段时，第一个错误字段出现，后续的不在验证
     *
     * @return
     */
    @Bean
    public LocalValidatorFactoryBean localValidator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        Map<String, String> map = new HashMap<>(1);
        map.put("hibernate.validator.fail_fast", "true");
        bean.setValidationPropertyMap(map);
        return bean;
    }


    /**
     * 解决 url 查询参数中特殊字符的问题
     * @link https://stackoverflow.com/questions/51703746/setting-relaxedquerychars-for-embedded-tomcat
     * @return
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {
                connector.setAttribute("relaxedPathChars", "<>[\\]^`{|}");
                connector.setAttribute("relaxedQueryChars", "<>[\\]^`{|}");
            });
        };
    }
}
