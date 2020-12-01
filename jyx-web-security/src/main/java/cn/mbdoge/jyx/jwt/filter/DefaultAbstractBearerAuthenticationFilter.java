package cn.mbdoge.jyx.jwt.filter;


import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;


/**
 * @author jyx
 */
public class DefaultAbstractBearerAuthenticationFilter extends AbstractBearerAuthenticationFilterAdapter {

    public DefaultAbstractBearerAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthenticationEntryPoint authenticationEntryPoint) {
        super(jwtTokenProvider, authenticationEntryPoint);
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return super.getToken(request);
    }

}
