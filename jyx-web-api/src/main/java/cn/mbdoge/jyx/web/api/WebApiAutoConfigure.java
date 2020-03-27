package cn.mbdoge.jyx.web.api;


import cn.mbdoge.jyx.encrypt.AesEncrypt;
import cn.mbdoge.jyx.web.encrypt.ApiEncrypt;
import cn.mbdoge.jyx.web.encrypt.ApiEncryptProperties;
import cn.mbdoge.jyx.web.language.ApiMessageProperties;
import cn.mbdoge.jyx.web.language.SmartLocaleResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * 主要是做一些常用的全局配置，减少重复的代码
 * @author jyx
 */
@Slf4j
@Configuration(proxyBeanMethods =false)
@EnableConfigurationProperties({ApiMessageProperties.class, ApiEncryptProperties.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 11)

public class WebApiAutoConfigure {

    private final ApiMessageProperties apiMessageProperties;
    private final ApiEncryptProperties apiEncryptProperties;
    private final ObjectMapper objectMapper;

    public WebApiAutoConfigure(ApiMessageProperties apiMessageProperties, ApiEncryptProperties apiEncryptProperties, ObjectMapper objectMapper) {
        this.apiMessageProperties = apiMessageProperties;
//        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
//        System.out.println("requestMappingHandlerAdapter = " + requestMappingHandlerAdapter);

//        requestMappingHandlerAdapter.setResponseBodyAdvice(Collections.singletonList(encodeResponseBodyAdvice()));
//        requestMappingHandlerAdapter.afterPropertiesSet();
        this.apiEncryptProperties = apiEncryptProperties;
        this.objectMapper = objectMapper;
//        WebMvcConfigurer
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

    @Bean
    @ConditionalOnMissingBean(ApiEncrypt.class)
    public ApiEncrypt apiAesEncrypt () {
        final String secret = apiEncryptProperties.getSecret();
        return new ApiEncrypt() {
            @Override
            public String encrypt(String plainText) {
                try {
                    return AesEncrypt.encrypt(plainText, secret);
                } catch (GeneralSecurityException e) {
                    log.warn("加密失败 reason: {}", e.getMessage());
                    return "";
                }
            }

            @Override
            public String encryptObj(Object obj) {
                Objects.requireNonNull(obj);
                try {
                    String json = objectMapper.writeValueAsString(obj);
                    return this.encrypt(json);
                } catch (JsonProcessingException e) {
                    log.warn("加密对象失败 reason: {}", e.getMessage());
                }
                return "";
            }

            @Override
            public String decrypt(String content) {
                try {
                    return AesEncrypt.decrypt(content, secret);
                } catch (GeneralSecurityException e) {
                    log.debug("解密失败 reason: {}", e.getMessage());
                    return "";
                }
            }

            @Override
            public <T> T decrypt(String content, Class<T> cla) {
                try {
                    String decrypt = AesEncrypt.decrypt(content, secret);
                    return objectMapper.readValue(decrypt, cla);
                } catch (GeneralSecurityException | JsonProcessingException e) {
                    log.debug("转换密文失败 reason: {}", e.getMessage());
                }
                return (T) null;
            }
        };
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



//    @ConditionalOnProperty(prefix = "mbdoge.api.encrypt",value = "enabled",havingValue = "true")
    @PostConstruct
    public void init () {

//        requestMappingHandlerAdapter.afterPropertiesSet();
//        requestMappingHandlerAdapter.setResponseBodyAdvice( Collections.singletonList(encodeResponseBodyAdvice()));
    }

//    @ControllerAdvice
//    @ConditionalOnProperty(prefix = "mbdoge.api.encrypt",value = "enabled",havingValue = "true")
//    public static class WebEncodeResponseBodyAdvice extends EncodeResponseBodyAdvice {
//        public WebEncodeResponseBodyAdvice(ApiEncryptProperties properties, ObjectMapper objectMapper) {
//            super(properties, objectMapper);
//        }
//    }


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
