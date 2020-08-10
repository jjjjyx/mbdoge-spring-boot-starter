package cn.mbdoge.jyx.security;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.filter.BearerAuthenticationFilterAdapter;
import cn.mbdoge.jyx.jwt.filter.DefaultBearerAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;
import cn.mbdoge.jyx.web.encrypt.ApiEncryptProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.security.GeneralSecurityException;
import java.util.Objects;

@Slf4j
@Import({RelatedBeanConfigure.class})
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

    @Bean("userDetailsServiceImpl")
    @ConditionalOnMissingBean(value = UserDetailsService.class, name = "userDetailsServiceImpl")
    public UserDetailsService userDetailsService () {
        return (username) -> {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        };
    }

    @Bean(name = "bearerAuthenticationFilterAdapter")
    @ConditionalOnMissingBean(BearerAuthenticationFilterAdapter.class)
    public BearerAuthenticationFilterAdapter bearerAuthenticationFilterAdapter (
            JwtTokenProvider jwtTokenProvider,
            AuthenticationEntryPoint authenticationEntryPoint) {
        return new DefaultBearerAuthenticationFilter(jwtTokenProvider, authenticationEntryPoint);
    }

    @Bean
    @ConditionalOnMissingBean(ConfigureHttpSecurity.class)
    public ConfigureHttpSecurity configureHttpSecurity  () throws Exception {
        return (httpSecurity) -> { };
    }

    @Bean
    @ConditionalOnMissingBean(ConfigureWebSecurity.class)
    public ConfigureWebSecurity configureWebSecurity  () throws Exception {
        return (webSecurity) -> { };
    }

    @Bean(name = "customDaoAuthenticationProvider")
    @ConditionalOnMissingBean(DaoAuthenticationProvider.class)
    public DaoAuthenticationProvider customDaoAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);

        return daoAuthenticationProvider;
    }
}
