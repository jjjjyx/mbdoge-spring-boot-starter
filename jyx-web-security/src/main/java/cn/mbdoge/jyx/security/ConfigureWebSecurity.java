package cn.mbdoge.jyx.security;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * @author jyx
 */
public interface ConfigureWebSecurity {

    /**
     * 自定义配置
     * @param web web
     */
    void configure(WebSecurity web);
}
