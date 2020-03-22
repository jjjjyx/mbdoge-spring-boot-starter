package cn.mbdoge.jyx.web.api;


import cn.mbdoge.jyx.web.language.ApiMessageProperties;
import cn.mbdoge.jyx.web.language.SmartLocaleResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主要是做一些常用的全局配置，减少重复的代码
 * @author jyx
 */
@Slf4j
@EnableConfigurationProperties(ApiMessageProperties.class)
@Configuration
public class ApiConfigure implements WebMvcConfigurer {

    @Autowired
    private ApiMessageProperties apiMessageProperties;

    /**
     * 配置默认的网页编码为 utf-8
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    /**
     * 配置api的 多语言
     * @return
     */
    @Bean(name = "apiMessageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
//        messageSource.addBasenames("classpath:org/springframework/security/messages");
//        messageSource.addBasenames("classpath:com/ls/starter/messages");
        List<String> source = apiMessageProperties.getSource();
        for (String s : source) {
            messageSource.addBasenames(s);
        }

        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * 配置api的 多语言
     * @return
     * @see cn.mbdoge.jyx.web.handler.ControllerHandlerAdvice
     */
    @Bean(name = "apiMessageSourceAccessor")
    public MessageSourceAccessor messageSourceAccessor() {
        return new MessageSourceAccessor(messageSource());
    }

    /**
     * 配置多语言使用，根据请求中的header = Accept-Language 来决定使用什么语言
     * @return ControllerHandlerAdvice
     */
    @Bean
    @ConditionalOnMissingBean(LocaleResolver.class)
    public LocaleResolver localeResolver() {
        List<String> languages = apiMessageProperties.getLanguages();
        if (languages.isEmpty()) {
            return new SmartLocaleResolver();
        }
        return new SmartLocaleResolver();
    }

    /**
     * 让验证时快熟失效
     * 当验证多个字段时，第一个错误字段出现，后续的不在验证
     * @return
     */
    @Bean
    public LocalValidatorFactoryBean localValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        Map<String, String> map = new HashMap<>(1);
        map.put("hibernate.validator.fail_fast", "true");
        bean.setValidationPropertyMap(map);
        return bean;
    }

// 需要在 应用类注册，这里注册会报 beans 重复错误 ，暂时没有解决办法
//    @Bean
//    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
//        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
//        factory.addConnectorCustomizers(connector -> {
//            connector.setProperty("relaxedPathChars", "\"<>[\\]^`{|}");
//            connector.setProperty("relaxedQueryChars", "\"<>[\\]^`{|}");
//        });
//        return factory;
//    }



//    @Override
//    public void addReturnValueHandlers(final List<HandlerMethodReturnValueHandler> returnValueHandlers) {
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//        messageConverters.add(new MappingJackson2HttpMessageConverter());
//        returnValueHandlers.add(new ApiEncryptReturnValueHandler(messageConverters));
//    }

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
////        List<ApiEncryptHttpMessageConverter> apiEncryptHttpMessageConverters = Collections.singletonList();
//        converters.add(new ApiEncryptHttpMessageConverter());
//
//    }

//    @Override
//    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
//        handlers.add(new ApiEncryptReturnValueHandler());
//    }


    //    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry
//                .addInterceptor(new ApiEncryptFilter())
//                .addPathPatterns("/**");
//    }

    //    @Override
//    public RequestMappingHandlerAdapter requestMappingHandlerAdapter(@Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
//                                                                     @Qualifier("mvcConversionService") FormattingConversionService conversionService,
//                                                                     @Qualifier("mvcValidator") Validator validator) {
//        System.out.println("111 = " + 111);
//        RequestMappingHandlerAdapter requestMappingHandlerAdapter = super.requestMappingHandlerAdapter(contentNegotiationManager, conversionService, validator);
//
//        log.info("是否开启api请求 结果加密a = {}", properties.isEnabled());
//        if (properties.isEnabled()) {
//            requestMappingHandlerAdapter.setResponseBodyAdvice(Collections.singletonList(new EncodeResponseBodyAdvice(properties, objectMapper)));
//        }
//        return requestMappingHandlerAdapter;
//    }
}
