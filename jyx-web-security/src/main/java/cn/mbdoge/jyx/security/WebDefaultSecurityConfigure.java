package cn.mbdoge.jyx.security;




import cn.mbdoge.jyx.jwt.filter.BearerAuthenticationFilterAdapter;
import cn.mbdoge.jyx.jwt.handler.AccessExceptionAdvice;
import cn.mbdoge.jyx.web.encrypt.ApiEncryptProperties;
import cn.mbdoge.jyx.web.encrypt.EncodeResponseBodyAdvice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * 需要使用者提供一些操作类，
 * 比如，保存 token
 * 验证用户
 * redis
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties({WebSecurityProperties.class, ApiEncryptProperties.class})
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 12)
@Import({EnableSecurityConfigure.class, EncodeResponseBodyAdvice.class, AccessExceptionAdvice.class})
public class WebDefaultSecurityConfigure extends WebSecurityConfigurerAdapter {

    @Autowired
    private WebSecurityProperties webSecurityProperties;

    @Autowired
    private ConfigureHttpSecurity configureHttpSecurity;


    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private BearerAuthenticationFilterAdapter bearerAuthenticationFilterAdapter;

    @Autowired @Qualifier("customDaoAuthenticationProvider")
    private DaoAuthenticationProvider customDaoAuthenticationProvider;


//    @Autowired
//    private CorsConfigurationSource corsConfigurationSource;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        super.configure(auth);
        auth.authenticationProvider(customDaoAuthenticationProvider);

    }

    //    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        // 后续有需要在参数化出来
        configuration.setExposedHeaders(Arrays.asList("Accept-Language", "Authorization", "Content-Disposition", "Content-Length"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(webSecurityProperties.getCorsUrl(), configuration);
        return source;
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the ROLE_ prefix
        return new GrantedAuthorityDefaults("");
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
        httpSecurity.exceptionHandling()
//                .accessDeniedHandler(defaultAccessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint);
        httpSecurity.cors().configurationSource(corsConfigurationSource());

        httpSecurity.csrf().disable();
        if (bearerAuthenticationFilterAdapter !=null) {
            httpSecurity.addFilterBefore(bearerAuthenticationFilterAdapter, UsernamePasswordAuthenticationFilter.class);
        }

        httpSecurity.headers().cacheControl();
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        AuthenticationManager authenticationManager = super.authenticationManager();

        return authenticationManager;
    }


}