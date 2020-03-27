package cn.mbdoge.jyx.jwt.filter;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

public class DefaultBearerAuthenticationFilter extends BearerAuthenticationFilterAdapter {

    public DefaultBearerAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        super(jwtTokenProvider);
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return super.getToken(request);
    }

    @Override
    public UserDetails getUserDetails(Jws<Claims> claimsFromToken) {
        return super.getUserDetails(claimsFromToken);
    }
}
