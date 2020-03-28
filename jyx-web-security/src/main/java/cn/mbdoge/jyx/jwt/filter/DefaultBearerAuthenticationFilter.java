package cn.mbdoge.jyx.jwt.filter;


import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import javax.servlet.http.HttpServletRequest;


public class DefaultBearerAuthenticationFilter extends BearerAuthenticationFilterAdapter {

    public DefaultBearerAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        super(jwtTokenProvider);
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return super.getToken(request);
    }

}
