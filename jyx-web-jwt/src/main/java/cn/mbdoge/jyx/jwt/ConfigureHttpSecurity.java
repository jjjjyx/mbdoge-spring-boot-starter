package cn.mbdoge.jyx.jwt;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface ConfigureHttpSecurity {
    // 实现自定义配置
    void configure(HttpSecurity httpSecurity);
}
