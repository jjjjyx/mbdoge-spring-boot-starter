package cn.mbdoge.jyx.security;

//import cn.mbdoge.jyx.jwt.core.JwtTokenFilterConfigure;
import cn.mbdoge.jyx.encrypt.AesEncrypt;
import cn.mbdoge.jyx.jwt.JwtProperties;
import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.filter.BearerAuthenticationFilterAdapter;
import cn.mbdoge.jyx.jwt.filter.JwtTokenFilterConfigured;
import cn.mbdoge.jyx.web.encrypt.ApiEncrypt;
import cn.mbdoge.jyx.web.encrypt.ApiEncryptProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
//import org.springframework.boot.autoconfigure.AutoConfigureOrder;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.core.Ordered;

/**
 * 需要使用者提供一些操作类，
 * 比如，保存 token
 * 验证用户
 * redis
 */
@Slf4j
@Configuration(proxyBeanMethods =false)
@EnableConfigurationProperties({SecurityProperties.class, JwtProperties.class, ApiEncryptProperties.class})
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 12)
@Import({EnableSecurityConfigure.class})
public class SecurityConfigure extends WebSecurityConfigurerAdapter {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private SecurityProperties securityProperties;
    @Autowired private ApiEncryptProperties apiEncryptProperties;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private MessageSource messageSource;
    @Autowired private MessageSourceAccessor messageSourceAccessor;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private BearerAuthenticationFilterAdapter authenticationFilterAdapter;
    @Autowired private ConfigureHttpSecurity configureHttpSecurity;

//    private final PasswordEncoder passwordEncoder;
//    private final UserDetailsService userDetailsService;
//
//    // 需要 UserDetailsService
//    // PasswordEncoder
//    // 设计到api 加密的情况，在处理 AuthenticationEntryPoint 需要暴露

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        provider.setMessageSource(messageSource);
        return provider;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

//        httpSecurity
//                .authorizeRequests()
//                .antMatchers(HttpMethod.GET, "/", "/*.html", "/favicon.ico", "/**/*.html", "/**/*.css", "/**/*.js", "/webjars/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/v1/auth", "/api/v1/auth/8d0KWAYxAYrw5z7D").permitAll()
//                // .antMatchers(HttpMethod.GET, "/api/v1/call/config").permitAll()
//                // .antMatchers(HttpMethod.GET,"/ws").permitAll()
//                // .antMatchers(AppConfig.ADMIN_SERVLET_URL_MATCH).permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .exceptionHandling()
////                .authenticationEntryPoint(authenticationEntryPoint())
//                .and()
//                .cors().and()
//                // 由于使用的是JWT，我们这里不需要 csrf
//                .csrf().disable();

        if (configureHttpSecurity!=null) {
            configureHttpSecurity.configure(httpSecurity);
        }

        httpSecurity.cors();
        httpSecurity.csrf().disable();

        httpSecurity.apply(new JwtTokenFilterConfigured(jwtTokenProvider, authenticationFilterAdapter));
        httpSecurity.headers().cacheControl();
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return super.authenticationManager();
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


//    public AuthenticationEntryPoint authenticationEntryPoint () {
//        return (request, response, e) -> {
//            response.setContentType("application/json; charset=utf-8");
//            log.debug("JwtAuthenticationEntryPoint err.class = {}", e.getClass());
//            String ret = e.getMessage();
//            if (e instanceof InsufficientAuthenticationException) {
//                ret = messageSourceAccessor.getMessage("ExceptionTranslationFilter.insufficientAuthentication");
//            }
//
//            log.debug("JwtAuthenticationEntryPoint : message = {}, Exception = {}", e.getMessage(), e.getClass());
//            PrintWriter out = response.getWriter();
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            // 是否需要加密
//            String respStr = objectMapper.writeValueAsString(RespResult.error(ret));
//            if (apiEncryptEnable) {
//                out.write("\"" + apiEncrypt.encrypt(respStr) + "\"");
//            } else {
//                out.write(respStr);
//            }
//
//            out.close();
//        };
//    }

//    @Bean
//    public TokenFactory tokenFactory () {
//        final String secret = jwtProperties.getSecret();
//        final byte[] encode = Base64.getEncoder().encode(secret.getBytes());
//        long expiration = jwtProperties.getExpiration();
//        return new TokenFactory() {
//            @Override
//            public String createToken(User user) {
//                return TokenUtils.createToken(user, expiration, encode);
//            }
//
//            @Override
//            public Jws<Claims> getTokenClaims(String token) {
//                return TokenUtils.getClaimsFromToken(token, encode);
//            }
//        };
//    }

}
