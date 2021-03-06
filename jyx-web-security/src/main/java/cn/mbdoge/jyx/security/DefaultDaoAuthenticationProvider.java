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


/**
 * @author jyx
 */
public class DefaultDaoAuthenticationProvider extends DaoAuthenticationProvider {

    public DefaultDaoAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
//        this.messages = new MessageSourceAccessor(messageSource);
        this.setPasswordEncoder(passwordEncoder);
        this.setUserDetailsService(userDetailsService);
    }

//    @Override
//    public void setMessageSource(MessageSource messageSource) {
//        // 不允许修改
//        // 在运行的时候会被修改 org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
//        System.out.println("messageSource222 = " + messageSource);
//        String message = this.messages.getMessage("ExceptionTranslationFilter.insufficientAuthentication");
//        System.out.println("message1 = " + message);
//
//        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
//        message = messageSourceAccessor.getMessage("ExceptionTranslationFilter.insufficientAuthentication");
//        System.out.println("message2 = " + message);
//
//        System.out.println("this.messages = " + this.messages);
//    }

}
