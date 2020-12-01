package cn.mbdoge.jyx.jwt.filter;

import lombok.extern.slf4j.Slf4j;
import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.Constant;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jyx
 */
@Slf4j
public abstract class AbstractBearerAuthenticationFilterAdapter extends OncePerRequestFilter {

    protected final JwtTokenProvider jwtTokenProvider;
    protected final AuthenticationEntryPoint authenticationEntryPoint;

    public AbstractBearerAuthenticationFilterAdapter(JwtTokenProvider jwtTokenProvider, AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    public String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.toLowerCase().startsWith(Constant.AUTHENTICATION_SCHEME_BEARER)) {
            return header.substring(Constant.AUTHENTICATION_SCHEME_BEARER.length());
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
//        BasicAuthenticationFilter
        UserDetails userDetails = null;
        try {
            userDetails = jwtTokenProvider.getUserDetails(token);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

            log.trace("Authentication success: " + auth);
            // 这里没有验证服务端限制的过期， 因为user的设计 没有过期项，也没有禁用相关
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (AuthenticationException e) {
            if (userDetails != null) {
                log.debug("user {} Authentication request for failed: {}",  userDetails.getUsername(), e.getMessage());
            } else {
                log.debug("Authentication request for failed: {}", e.getMessage());
            }
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
            return;
            // 提交了token 却验证错误
        }

        filterChain.doFilter(request, response);
    }
}
