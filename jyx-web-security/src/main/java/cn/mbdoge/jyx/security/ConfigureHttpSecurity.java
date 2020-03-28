package cn.mbdoge.jyx.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface ConfigureHttpSecurity {

    // 实现自定义配置
    void configure(HttpSecurity httpSecurity) throws Exception;
}
