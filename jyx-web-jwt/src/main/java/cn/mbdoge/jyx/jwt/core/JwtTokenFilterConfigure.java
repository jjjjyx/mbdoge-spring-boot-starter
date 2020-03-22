package cn.mbdoge.jyx.jwt.core;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * @author jyx
 */
public class JwtTokenFilterConfigure extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
//    private JwtTokenProvider jwtTokenProvider;
//    private String headerKey;
//
//    public JwtTokenFilterConfigurer(JwtTokenProvider jwtTokenProvider) {
//        this(jwtTokenProvider, "Authorization");
//    }
//
//    public JwtTokenFilterConfigurer(JwtTokenProvider jwtTokenProvider, String headerKey) {
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.headerKey = headerKey;
//    }
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        BearerAuthenticationFilter customFilter = new BearerAuthenticationFilter(jwtTokenProvider);
//        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
//    }
}
