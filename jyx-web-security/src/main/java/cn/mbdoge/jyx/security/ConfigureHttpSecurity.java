package cn.mbdoge.jyx.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author jyx
 */
public interface ConfigureHttpSecurity {

    /**
     * 实现自定义配置
     * @param httpSecurity httpSecurity
     * @throws Exception ex
     */
    void configure(HttpSecurity httpSecurity) throws Exception;
}
