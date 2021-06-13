package cn.mbdoge.jyx.security;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.handler.DefaultAccessDeniedHandler;
import cn.mbdoge.jyx.jwt.handler.DefaultAuthenticationEntryPoint;
import cn.mbdoge.jyx.web.encrypt.ApiEncrypt;
import cn.mbdoge.jyx.web.encrypt.ApiEncryptProperties;
import cn.mbdoge.jyx.web.encrypt.DefaultApiAesEncrypt;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * @author jyx
 */
@Configurable
@Slf4j
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

    @Bean("userDetailsServiceImpl")
    @ConditionalOnMissingBean(value = UserDetailsService.class, name = "userDetailsServiceImpl")
    public UserDetailsService userDetailsService() {
        log.warn("Please override the default UserDetailsService！");
        return (username) -> {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        };
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder(webSecurityProperties.getSecret());
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint authenticationEntryPoint (ApiEncrypt apiEncrypt, MessageSourceAccessor messageSourceAccessor) {
        return new DefaultAuthenticationEntryPoint(apiEncryptProperties, objectMapper, apiEncrypt, messageSourceAccessor);
    }

    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler accessDeniedHandler (ApiEncrypt apiEncrypt, MessageSourceAccessor messageSourceAccessor) {
        return new DefaultAccessDeniedHandler(apiEncryptProperties, objectMapper, apiEncrypt, messageSourceAccessor);
    }

    @Bean
    @ConditionalOnMissingBean(ApiEncrypt.class)
    public ApiEncrypt apiEncrypt () {
        return new DefaultApiAesEncrypt(apiEncryptProperties, objectMapper);
    }
}
