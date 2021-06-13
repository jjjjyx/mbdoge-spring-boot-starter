package cn.mbdoge.jyx.security;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.filter.AbstractBearerAuthenticationFilterAdapter;
import cn.mbdoge.jyx.jwt.filter.DefaultAbstractBearerAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author jyx
 */
@Slf4j
@Import({RelatedBeanConfigure.class})
public class EnableSecurityConfigure {
    @Bean(name = "bearerAuthenticationFilterAdapter")
    @ConditionalOnMissingBean(AbstractBearerAuthenticationFilterAdapter.class)
    public AbstractBearerAuthenticationFilterAdapter bearerAuthenticationFilterAdapter(
            JwtTokenProvider jwtTokenProvider,
            AuthenticationEntryPoint authenticationEntryPoint) {
        return new DefaultAbstractBearerAuthenticationFilter(jwtTokenProvider, authenticationEntryPoint);
    }

    @Bean(name = "defaultDaoAuthenticationProvider")
    @ConditionalOnMissingBean(DefaultDaoAuthenticationProvider.class)
    public DefaultDaoAuthenticationProvider customDaoAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        return new DefaultDaoAuthenticationProvider(passwordEncoder, userDetailsService);
    }


    @Bean
    @ConditionalOnMissingBean(ConfigureHttpSecurity.class)
    public ConfigureHttpSecurity configureHttpSecurity() {
        return (httpSecurity) -> {
        };
    }

    @Bean
    @ConditionalOnMissingBean(ConfigureWebSecurity.class)
    public ConfigureWebSecurity configureWebSecurity() {
        return (webSecurity) -> {
        };
    }

    @Bean
    @ConditionalOnMissingBean(ConfigureAuthenticationManager.class)
    public ConfigureAuthenticationManager configureAuthenticationManager() {
        return (builder) -> {
        };
    }
}
