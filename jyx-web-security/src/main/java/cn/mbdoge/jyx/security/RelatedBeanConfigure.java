package cn.mbdoge.jyx.security;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.handler.DefaultAuthenticationEntryPoint;
import cn.mbdoge.jyx.web.encrypt.ApiEncrypt;
import cn.mbdoge.jyx.web.encrypt.ApiEncryptProperties;
import cn.mbdoge.jyx.web.encrypt.DefaultApiAesEncrypt;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configurable
public class RelatedBeanConfigure {
    @Autowired
    private WebSecurityProperties webSecurityProperties;

    @Autowired
    private ApiEncryptProperties apiEncryptProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider (
            RedisTemplate<String, Object> redisTemplate,
            MessageSourceAccessor messageSourceAccessor) {
        return new JwtTokenProvider(webSecurityProperties, redisTemplate, messageSourceAccessor);
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint authenticationEntryPoint (ApiEncrypt apiEncrypt, MessageSourceAccessor messageSourceAccessor) {
        return new DefaultAuthenticationEntryPoint(apiEncryptProperties, objectMapper, apiEncrypt, messageSourceAccessor);
    }

    @Bean
    @ConditionalOnMissingBean(ApiEncrypt.class)
    public ApiEncrypt apiEncrypt () {
        return new DefaultApiAesEncrypt(apiEncryptProperties, objectMapper);
    }

}
