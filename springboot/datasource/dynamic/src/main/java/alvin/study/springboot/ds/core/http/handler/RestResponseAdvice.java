package alvin.study.springboot.ds.core.http.handler;

import alvin.study.springboot.ds.core.http.common.ClientError;
import alvin.study.springboot.ds.core.http.common.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.AddDefaultCharsetFilter.ResponseWrapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 对 Controller 的返回结果进行处理
 *
 * <p>
 * Controller 返回的结果有两类: 1. 正常执行完毕后的返回值; 2. 出现错误时抛出的异常. 其中:
 * </p>
 *
 * <ul>
 * <li>
 * {@link RestControllerAdvice @RestControllerAdvice} 注解表示当前类用于对 Controller
 * 抛出的异常进行处理, 以取代默认的异常处理后返回的
 * </li>
 * <li>
 * {@link ResponseBodyAdvice#beforeBodyWrite(Object, MethodParameter, MediaType, Class, ServerHttpRequest, ServerHttpResponse)}
 * 接口方法表示当前类会在返回结果写入前进行一次处理, 以取代
 * Controller 返回的结果内容
 * </li>
 * </ul>
 *
 * <p>
 * 上述注解和接口统一完成了一个工作: 对异常或返回值进行统一处理, 以便让客户端接收到格式一致的返回对象
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class RestResponseAdvice implements ResponseBodyAdvice<Object> {
    /**
     * 返回 {@code true} 或 {@code false}, 以决定 {@code beforeBodyWrite} 方法是否需要执行
     *
     * <p>
     * 该方法相当于是一个前置判断, 对于每一个被调用的 Controller 进行判断, 如果返回 {@code true}, 则下一步会执行
     * {@code beforeBodyWrite} 方法对本次的 Controller 的返回值进行处理
     * </p>
     *
     * <p>
     * 判断的依据为所调用 Controller 方法信息 ({@code returnType} 参数) 以及预定义的返回值处理对象
     * ({@code converterType}) 参数
     * </p>
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 获取 controller 方法的返回值类型
        var retType = Optional.ofNullable(returnType.getMethod()).map(Method::getReturnType).orElseThrow();

        // 如果 controller 方法返回类型为 Response 类型, 则返回 false
        return ResponseDto.class != retType && ResponseEntity.class != retType;
    }

    /**
     * 在响应数据写入返回数据流前进行处理
     *
     * <p>
     * 当 {@code supports} 方法返回 {@code true} 后, Controller 的返回值会传递到该方法中进行处理,
     * 以改变预定义的处理行为和结果
     * </p>
     *
     * <p>
     * 本例中, 对所有不是 {@link ResponseDto} 和 {@link ResponseEntity} 类型的返回值, 均包装为
     * {@link ResponseDto} 类型返回, 由此对 Controller 返回客户端的数据格式进行了统一
     * </p>
     */
    @Override
    public ResponseDto<?> beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        return ResponseDto.success(body);
    }

    /**
     * 统一异常处理方法
     *
     * <p>
     * 处理 {@link HttpClientErrorException} 类型异常
     * </p>
     *
     * <p>
     * 该异常表示一个客户端异常, 即客户端发送的请求无效导致的异常
     * </p>
     *
     * <p>
     * 一般情况下这类异常反馈给客户端为 {@code 400 BAD_REQUEST} 错误响应
     * </p>
     *
     * @param e {@link HttpClientErrorException} 类型异常对象
     * @return 包装为 {@link ResponseWrapper} 类型的响应结果
     */
    @ResponseBody
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ResponseDto<ClientError>> handle(HttpClientErrorException e) {
        log.warn("Some error raised and will return to client", e);

        var resp = ResponseDto.error(e.getStatusCode().value(), ClientError.create(e.getStatusText(), e.getMessage()));
        return new ResponseEntity<>(resp, e.getStatusCode());
    }

    /**
     * 统一异常处理方法
     *
     * <p>
     * 处理 {@link Exception} 类型异常, 所有的未处理异常都会由该方法处理
     * </p>
     *
     * <p>
     * 一般情况下这类异常反馈给客户端为 {@code 500 INTERNAL_SERVER_ERROR} 错误响应
     * </p>
     *
     * @param e {@link Exception} 类型异常对象
     * @return {@link ResponseDto ResponseDto&lt; ClientError &gt;} 类型的响应结果
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseDto<ClientError> handle(Exception e) {
        log.warn("Some error raised and will return to client", e);

        return ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ClientError.create(
            HttpStatus.INTERNAL_SERVER_ERROR.name(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }
}
