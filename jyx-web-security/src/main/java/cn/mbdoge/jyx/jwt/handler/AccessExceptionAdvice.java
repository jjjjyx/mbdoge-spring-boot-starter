package cn.mbdoge.jyx.jwt.handler;

import cn.mbdoge.jyx.web.model.RespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author jyx
 */
@ControllerAdvice
@ResponseBody
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessExceptionAdvice {
    public AccessExceptionAdvice() {
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class})
    public RespResult<?> handleAccessDeniedException(AccessDeniedException e) {
//        this.messageSourceAccessor.getMessage("controller.no.access")
        log.trace("AccessDeniedException = {}", e.getMessage());
        return RespResult.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationException.class})
    public RespResult<?> handleAuthenticationException(AuthenticationException e) {
//        this.messageSourceAccessor.getMessage("controller.no.access")
        log.trace("AuthenticationException = {}", e.getMessage());
        return RespResult.error(e.getMessage());
    }
}
