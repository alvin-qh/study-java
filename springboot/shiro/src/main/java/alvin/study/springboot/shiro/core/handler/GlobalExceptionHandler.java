package alvin.study.springboot.shiro.core.handler;

import java.io.Serializable;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponseDto handle(Exception e) {
        return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResponseDto handle(AuthorizationException e) {
        return new ErrorResponseDto(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler
    public ErrorResponseDto handle(UnauthorizedException e) {
        return new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponseDto implements Serializable {
        private int status;
        private String message;
    }
}
