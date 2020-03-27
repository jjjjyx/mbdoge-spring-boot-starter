package cn.mbdoge.jyx.jwt.filter;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.filter.BearerAuthenticationFilterAdapter;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtTokenFilterConfigured extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;
    private final BearerAuthenticationFilterAdapter authenticationFilterAdapter;

    public JwtTokenFilterConfigured(JwtTokenProvider jwtTokenProvider, BearerAuthenticationFilterAdapter authenticationFilterAdapter) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationFilterAdapter = authenticationFilterAdapter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
//        BearerAuthenticationFilterAdapter customFilter = new BearerAuthenticationFilterAdapter(jwtTokenProvider);

        http.addFilterBefore(authenticationFilterAdapter, UsernamePasswordAuthenticationFilter.class);
    }
}
