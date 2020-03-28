package cn.mbdoge.jyx.web.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "mbdoge.web.api.encrypt",value = "enabled",havingValue = "true")
@ControllerAdvice
@Order(1)
public class EncodeResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ApiEncrypt apiEncrypt;

    public EncodeResponseBodyAdvice(ApiEncrypt apiEncrypt) {
        this.apiEncrypt = apiEncrypt;
    }


    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }


    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest srequest, ServerHttpResponse response) {
        ServletServerHttpRequest temp = (ServletServerHttpRequest) srequest;
        HttpServletRequest req = temp.getServletRequest();

        if (selectedContentType.equals(MediaType.APPLICATION_JSON)) {
            Object obj = body;
            if (obj instanceof MappingJacksonValue) {
                obj = ((MappingJacksonValue) obj).getValue();
            }
            log.trace("对方法 api 请求 = {} 返回的数据进行加密", req.getServletPath());

            return apiEncrypt.encryptObj(obj);
        } else {
            return body;
        }
    }
}
