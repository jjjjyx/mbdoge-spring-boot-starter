package cn.mbdoge.jyx.jwt.filter;

import cn.mbdoge.jyx.jwt.JwtProperties;
import cn.mbdoge.jyx.jwt.TokenFactory;
import cn.mbdoge.jyx.jwt.core.Constant;
import cn.mbdoge.jyx.jwt.exception.InvalidJwtAuthenticationException;
import cn.mbdoge.jyx.jwt.util.TokenUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public abstract class BearerAuthenticationFilterAdapter extends OncePerRequestFilter {


    public BearerAuthenticationFilterAdapter() {
    }

    public String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.toLowerCase().startsWith(Constant.AUTHENTICATION_SCHEME_BEARER)) {
            return header.substring(Constant.AUTHENTICATION_SCHEME_BEARER.length());
        }
        return null;
    }

    public abstract Authentication getAuthentication(String token);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication auth = getAuthentication(token);
            this.logger.trace("Authentication success: " + auth);
            // 这里没有验证服务端限制的过期， 因为user的设计 没有过期项，也没有禁用相关
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            this.logger.debug("Authentication request for failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
