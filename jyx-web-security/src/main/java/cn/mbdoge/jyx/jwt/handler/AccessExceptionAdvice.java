package cn.mbdoge.jyx.jwt.handler;

import cn.mbdoge.jyx.web.model.RespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
@Slf4j
public class AccessExceptionAdvice {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class})
    public RespResult<?> handleAccessDeniedException(AccessDeniedException e) {
//        this.messageSourceAccessor.getMessage("controller.no.access")
        e.printStackTrace();
        log.trace("AccessDeniedException = {}", e.getMessage());
        return RespResult.error(e.getMessage());
    }
}
