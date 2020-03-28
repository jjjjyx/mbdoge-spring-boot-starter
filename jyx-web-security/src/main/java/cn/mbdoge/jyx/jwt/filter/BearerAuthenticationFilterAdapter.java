package cn.mbdoge.jyx.jwt.filter;

import lombok.extern.slf4j.Slf4j;
import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.Constant;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public abstract class BearerAuthenticationFilterAdapter extends OncePerRequestFilter {

    protected final JwtTokenProvider jwtTokenProvider;

    public BearerAuthenticationFilterAdapter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
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

        try {
            UserDetails userDetails = jwtTokenProvider.getUserDetails(token);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

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
