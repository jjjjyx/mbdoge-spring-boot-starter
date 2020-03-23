package cn.mbdoge.jyx.jwt.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BearerAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    }
}
