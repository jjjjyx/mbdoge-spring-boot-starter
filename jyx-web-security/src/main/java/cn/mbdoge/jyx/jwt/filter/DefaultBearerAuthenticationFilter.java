package cn.mbdoge.jyx.jwt.filter;


import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;


public class DefaultBearerAuthenticationFilter extends BearerAuthenticationFilterAdapter {

    public DefaultBearerAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthenticationEntryPoint authenticationEntryPoint) {
        super(jwtTokenProvider, authenticationEntryPoint);
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return super.getToken(request);
    }

}
