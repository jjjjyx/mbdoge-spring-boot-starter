package cn.mbdoge.jyx.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

    public CustomDaoAuthenticationProvider(
            @Qualifier("webMessageSourceAccessor") MessageSourceAccessor messageSourceAccessor,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService
    ) {
        this.messages = messageSourceAccessor;
        this.setPasswordEncoder(passwordEncoder);
        this.setUserDetailsService(userDetailsService);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        // 不允许修改
        // 在运行的时候会被修改 org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
//        System.out.println("messageSource = " + messageSource);
    }

}
