package cn.mbdoge.jyx.security;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.filter.BearerAuthenticationFilterAdapter;
import cn.mbdoge.jyx.jwt.filter.DefaultBearerAuthenticationFilter;
import cn.mbdoge.jyx.jwt.handler.JwtAuthenticationEntryPoint;
import cn.mbdoge.jyx.security.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;


@Configuration(proxyBeanMethods =false)
public abstract class EnableSecurityConfigure {
    @Autowired
    private SecurityProperties securityProperties;

//    @Autowired
//    private MessageSource messageSource;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder(securityProperties.getSecret());
    }

    @Bean
    @ConditionalOnMissingBean(BearerAuthenticationFilterAdapter.class)
    public BearerAuthenticationFilterAdapter bearerAuthenticationFilter () {
        return new DefaultBearerAuthenticationFilter(jwtTokenProvider);
    }

    public abstract UserDetailsService userDetailsService ();


    @Bean
    @ConditionalOnMissingBean(ConfigureHttpSecurity.class)
    private ConfigureHttpSecurity configureHttpSecurity () {
        return (httpSecurity) -> { };
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the ROLE_ prefix
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        // 后续有需要在参数化出来
        configuration.setExposedHeaders(Arrays.asList("Accept-Language", "Authorization", "Content-Disposition", "Content-Length"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(securityProperties.getCorsUrl(), configuration);
        return source;
    }

//    httpSecurity.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));

//    @Bean
//    public AuthenticationEntryPoint authenticationEntryPoint() {
//        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint(messageSourceAccessor, apiEncrypt);
//        jwtAuthenticationEntryPoint.setApiEncryptEnable(apiEncryptProperties.isEnabled());
//        return jwtAuthenticationEntryPoint;
//    }
}
