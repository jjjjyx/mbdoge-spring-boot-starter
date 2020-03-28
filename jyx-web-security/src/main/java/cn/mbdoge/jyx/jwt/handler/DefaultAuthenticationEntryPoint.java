package cn.mbdoge.jyx.jwt.handler;

import lombok.extern.slf4j.Slf4j;
import cn.mbdoge.jyx.web.encrypt.ApiEncrypt;
import cn.mbdoge.jyx.web.encrypt.ApiEncryptProperties;
import cn.mbdoge.jyx.web.model.RespResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final ApiEncrypt apiEncrypt;
    private final ApiEncryptProperties apiEncryptProperties;

    public DefaultAuthenticationEntryPoint(ApiEncryptProperties apiEncryptProperties, ObjectMapper objectMapper, ApiEncrypt apiEncrypt) {
        this.objectMapper = objectMapper;
        this.apiEncrypt = apiEncrypt;
        this.apiEncryptProperties = apiEncryptProperties;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json; charset=utf-8");

        String ret = e.getMessage();
//        if (e instanceof InsufficientAuthenticationException) {
//            ret = messageSourceAccessor.getMessage("ExceptionTranslationFilter.insufficientAuthentication");
//        }

        log.debug("JwtAuthenticationEntryPoint : message = {}, Exception = {}", e.getMessage(), e.getClass());
        PrintWriter out = response.getWriter();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // 是否需要加密
        String respStr = objectMapper.writeValueAsString(RespResult.error(ret));
        if (apiEncryptProperties.isEnabled()) {
            out.write("\"" + apiEncrypt.encrypt(respStr) + "\"");
        } else {
            out.write(respStr);
        }

        out.close();
    }
}
