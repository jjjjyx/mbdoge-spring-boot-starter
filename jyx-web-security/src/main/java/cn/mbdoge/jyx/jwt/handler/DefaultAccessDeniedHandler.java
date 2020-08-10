package cn.mbdoge.jyx.jwt.handler;

import cn.mbdoge.jyx.web.encrypt.ApiEncrypt;
import cn.mbdoge.jyx.web.encrypt.ApiEncryptProperties;
import cn.mbdoge.jyx.web.model.RespResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * @author jyx
 */
@Slf4j
public class DefaultAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final ApiEncrypt apiEncrypt;
    private final ApiEncryptProperties apiEncryptProperties;
    private final MessageSourceAccessor messageSourceAccessor;

    public DefaultAccessDeniedHandler(ApiEncryptProperties apiEncryptProperties, ObjectMapper objectMapper, ApiEncrypt apiEncrypt, MessageSourceAccessor messageSourceAccessor) {
        this.objectMapper = objectMapper;
        this.apiEncrypt = apiEncrypt;
        this.apiEncryptProperties = apiEncryptProperties;
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {

        response.setContentType("application/json; charset=utf-8");
        // 访问了无权访问的接口
        log.info("DefaultAccessDeniedHandler : message = {}, Exception = {}", e.getMessage(), e.getClass());
        String ret = e.getMessage();

        PrintWriter out = response.getWriter();
        response.setStatus(HttpStatus.FORBIDDEN.value());
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
