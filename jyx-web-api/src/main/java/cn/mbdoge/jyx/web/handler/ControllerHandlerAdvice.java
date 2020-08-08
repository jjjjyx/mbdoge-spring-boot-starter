package cn.mbdoge.jyx.web.handler;

import cn.mbdoge.jyx.exception.LocalServiceException;
import cn.mbdoge.jyx.exception.RequestLimitException;
import cn.mbdoge.jyx.web.model.RespResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.*;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 *  spring.mvc.throw-exception-if-no-handler-found=true
 * 需要配置这个选项
 */
@Slf4j
@ConditionalOnClass(MessageSourceAccessor.class)
@ControllerAdvice
@ResponseBody
public class ControllerHandlerAdvice {

    protected final MessageSourceAccessor messageSourceAccessor;

    public ControllerHandlerAdvice(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    /**
     * 请求缺少必要参数错误
     * @param e MissingServletRequestParameterException
     * @return RespResult.warning 警告
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public RespResult<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.trace("MissingServletRequestParameterException 缺少请求参数 getParameterName = {} type = {}", e.getParameterName(), e.getParameterType());
        return RespResult.warning(this.messageSourceAccessor.getMessage("controller.parameter.MissingServletRequest", new Object[]{e.getParameterName()}));
    }

    /**
     * 请求的参数解析错误
     * 发生在 @RequestBody 注解上， 需要将提交对于的 content-type
     * 字段错误
     * @param e HttpMessageNotReadableException
     * @return RespResult.warning 警告
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public RespResult<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) throws IOException {
        log.trace("HttpMessageNotReadableException 请求体解析失败 = {}", e.getMessage());
        return RespResult.warning(this.messageSourceAccessor.getMessage("controller.parameter.HttpMessageNotReadable"));
    }

    /**
     * 只有@RequestBody 并且请求 contentType = application/json 中验证失败会出现这个异常
     * /@Validated @RequestBody B b 中出现验证错误才会出现
     * 由于开启 快速失败，只有一个错误
     * @param e handleMethodArgumentNotValidException
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public RespResult<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.trace("MethodArgumentNotValidException 参数验证失败 = {}", e.getMessage());
        return parseBindingResult(e.getBindingResult());
    }

    /**
     * 只有没有注解 @RequestBody 的参数，以 x-www-form-urlencoded 提交产生出现验证错误
     * @param e BindException
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class})
    public RespResult<?> handleBindException(BindException e) {
        log.trace("BindException 参数验证失败 = {}", e.getMessage());
        return parseBindingResult(e.getBindingResult());
    }

    /**
     * 解析 BindingResult
     * @param result
     * @return
     */
    private RespResult<?> parseBindingResult (BindingResult result) {
        if (result.hasFieldErrors()) {
            FieldError fieldError = result.getFieldError();
            // Object value = fieldError.getRejectedValue();
            // this.messageSourceAccessor.getMessage("controller.parameter.MethodArgumentNotValid", new Object[]{field, value}), fieldError.getField()
            assert fieldError != null;
            String field = fieldError.getField();
            return RespResult.warning(fieldError.getDefaultMessage(), field);
        } else {
            return RespResult.warning(this.messageSourceAccessor.getMessage("controller.parameter.MethodArgumentNotValid.empty"));
        }
    }


    /**
     * 参数类型出错，如果参数是 @RequestBody，则不会触发
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public RespResult<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.trace("MethodArgumentTypeMismatchException 参数类型出错 = {}", ex.getMessage());
        String type = Objects.requireNonNull(ex.getRequiredType()).getSimpleName();
        Object value = ex.getValue();
        return RespResult.error(this.messageSourceAccessor.getMessage("controller.parameter.ArgumentTypeMismatch", new Object[]{type, value}));
    }

    /**
     * 在 Controller 类上注解 @Validated 并且方法上的注解出现错误, 并且不是类型参数
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    public RespResult<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.trace("ConstraintViolationException 参数验证失败 = {}", e.getMessage());
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        if (message == null || "".equals(message)) {
            return RespResult.warning(this.messageSourceAccessor.getMessage("controller.parameter.ConstraintViolation"));
        }
        return RespResult.warning(message);
    }

    // 这个因该不会触发
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler({ValidationException.class})
//    public RespResult<?> handleValidationException(ValidationException e) {
//        log.trace("ValidationException 参数验证失败", e);
//        return RespResult.warning(this.messageSourceAccessor.getMessage("controller.parameter.Validation"));
//    }

    /**
     * 请求方式错误
     * @param e HttpRequestMethodNotSupportedException
     * @return RespResult.error 错误警告
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public RespResult<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.trace("不支持的请求方法 = {} ", e.getMethod());
        return RespResult.error(this.messageSourceAccessor.getMessage("controller.MethodNotSupported", new Object[] {e.getMethod()}));
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public RespResult<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.trace("不支持当前媒体类型 = {}", e.getContentType());
        return RespResult.info(this.messageSourceAccessor.getMessage("controller.HttpMediaTypeNotSupported", new Object[]{e.getContentType()}));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MultipartException.class})
    public RespResult<?> handleMultipartException(MultipartException e) {
        log.trace("上传文件失败", e);
        String message = "upload.fail";
        if (e instanceof MaxUploadSizeExceededException) {
            message = "upload.fail.exceeded.max.size";
        }

        return RespResult.warning(this.messageSourceAccessor.getMessage(message));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({LocalServiceException.class})
    public RespResult<?> handleServiceException(LocalServiceException e) {
        return RespResult.info(this.messageSourceAccessor.getMessage(e.getMessage(), e.getParams()), e.getData());
    }

    /**
     * 访问限制
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({RequestLimitException.class})
    public RespResult<?> handleRequestLimitException(RequestLimitException e) {
        String message = e.getMessage();
        if (message == null) {
            message = "request.limit";
        }
        return RespResult.warning(this.messageSourceAccessor.getMessage(message));
    }

//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    @ExceptionHandler({NoAccessException.class})
//    public RespResult<?> handleServiceException(NoAccessException e) {
//        return RespResult.error(this.messageSourceAccessor.getMessage("controller.no.access"));
//    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoHandlerFoundException.class})
    public RespResult<?> noHandlerFoundException(NoHandlerFoundException e) {
        log.trace("Not Found = {}", e.getRequestURL());
        return RespResult.info(this.messageSourceAccessor.getMessage("controller.404"));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public RespResult<?> handleException(Exception e) {
        log.error("通用异常 = e.class = {} message = {}", e.getClass(), e.getMessage(), e);
        return RespResult.error(this.messageSourceAccessor.getMessage("controller.500"));
    }


}
