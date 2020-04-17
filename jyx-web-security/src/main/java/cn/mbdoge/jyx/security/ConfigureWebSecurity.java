package cn.mbdoge.jyx.security;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

public interface ConfigureWebSecurity {
    void configure(WebSecurity web);
}
