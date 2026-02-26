package hk.fish.fishpicturebackend.exception;

import hk.fish.fishpicturebackend.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 全局异常处理(环绕切面)
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException exception){
        log.error("BusinessException:{}",exception.getMessage());
        return BaseResponse.error(exception);
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> businessExceptionHandler(RuntimeException exception){
        log.error("RuntimeException,{}",exception.getMessage());
        return BaseResponse.error(exception);
    }
}
