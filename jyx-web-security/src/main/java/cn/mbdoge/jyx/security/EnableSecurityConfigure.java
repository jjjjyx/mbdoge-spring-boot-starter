package cn.mbdoge.jyx.security;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.filter.BearerAuthenticationFilterAdapter;
import cn.mbdoge.jyx.jwt.filter.DefaultBearerAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;


import cn.mbdoge.jyx.encrypt.AesEncrypt;
import cn.mbdoge.jyx.web.encrypt.ApiEncrypt;
import cn.mbdoge.jyx.web.encrypt.ApiEncryptProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.security.GeneralSecurityException;
import java.util.Objects;

@Slf4j
@Configuration(proxyBeanMethods =false)
public class EnableSecurityConfigure {
    @Autowired
    private WebSecurityProperties webSecurityProperties;

    @Autowired
    private ApiEncryptProperties apiEncryptProperties;



    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder(webSecurityProperties.getSecret());
    }


    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.afterPropertiesSet();
        return template;
    }
    @Bean("userDetailsServiceImpl")
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailsService (){
        return (username) -> {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        };
    }
    //    @Autowired
//    @Qualifier("webMessageSourceAccessor")
//    private MessageSourceAccessor messageSourceAccessor;
    @Bean
    @ConditionalOnMissingBean(BearerAuthenticationFilterAdapter.class)
    public BearerAuthenticationFilterAdapter bearerAuthenticationFilterAdapter (
            JwtTokenProvider jwtTokenProvider,
            AuthenticationEntryPoint authenticationEntryPoint) {
        return new DefaultBearerAuthenticationFilter(jwtTokenProvider, authenticationEntryPoint);
    }

    @Bean
    @ConditionalOnMissingBean(ConfigureHttpSecurity.class)
    public ConfigureHttpSecurity configureHttpSecurity () {
        return (httpSecurity) -> { };
    }

    @Bean
    @ConditionalOnMissingBean(ApiEncrypt.class)
    public ApiEncrypt apiAesEncrypt (ObjectMapper objectMapper) {
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
}
