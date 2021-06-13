package cn.mbdoge.jyx.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author jyx
 */
public interface ConfigureAuthenticationManager {

    /**
     * 实现自定义配置
     * @param builder builder
     * @throws Exception ex
     */
    void configure(AuthenticationManagerBuilder builder);
}
