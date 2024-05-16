package alvin.study.springboot.springdoc.core.http;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.google.common.base.Joiner;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;

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
@Hidden
@RestControllerAdvice(basePackages = "alvin.study.app")
public class RestResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    /**
     * 以警告级别记录异常日志
     *
     * @param e 异常对象
     */
    private static void warnLog(Exception e) {
        log.warn("Some error raised and will return to client", e);
    }

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
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        // 获取 controller 方法的返回值类型
        var retType = Optional.ofNullable(returnType.getMethod())
                .map(Method::getReturnType)
                .orElseThrow();

        // 如果 controller 方法返回类型为 Response 类型, 则返回 false
        return ResponseWrapper.class != retType && ResponseEntity.class != retType && String.class != retType;
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
     * 本例中, 对所有不是 {@link ResponseWrapper} 和 {@link ResponseEntity} 类型的返回值, 均包装为
     * {@link ResponseWrapper} 类型返回, 由此对 Controller 返回客户端的数据格式进行了统一
     * </p>
     *
     * @see ResponseWrapper#success(Object)
     */
    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        return ResponseWrapper.success(body);
    }

    /**
     * 处理其余未处理异常
     *
     * <p>
     * 处理 {@link Exception} 类型异常
     * </p>
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseWrapper<Void> handle(Exception e) {
        warnLog(e);

        return ResponseWrapper.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal_error");
    }

    /**
     * 处理参数绑定异常
     *
     * <p>
     * 处理 {@link BindException} 类型异常
     * </p>
     *
     * <p>
     * 当 Controller 在绑定请求参数时出现错误时抛出的异常
     * </p>
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResponseWrapper<ResponseWrapper.ErrorDetail> handle(BindException e) {
        warnLog(e);

        var fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    r -> new String[] { r.getDefaultMessage() }));

        return ResponseWrapper.error(
            HttpStatus.BAD_REQUEST.value(),
            "missing_request_args",
            ResponseWrapper.ErrorDetail.withErrorFields(fieldErrors));
    }

    /**
     * 处理请求参数校验错误异常
     *
     * <p>
     * 处理 {@link ConstraintViolationException} 类型异常
     * </p>
     *
     * <p>
     * 一般情况下这类异常反馈给客户端为 {@code 400 BAD_REQUEST} 错误响应
     * </p>
     *
     * @param e 异常对象
     * @return 包装为 {@link ResponseWrapper} 类型的响应结果
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseWrapper<ResponseWrapper.ErrorDetail> handle(ConstraintViolationException e) {
        warnLog(e);

        var err = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                    v -> pathToPropertyName(v.getPropertyPath()),
                    v -> new String[] { v.getMessage() }));

        return ResponseWrapper.error(
            HttpStatus.BAD_REQUEST.value(),
            "invalid_request_args",
            ResponseWrapper.ErrorDetail.withErrorParameters(err));
    }

    /**
     * 将 {@link Path} 类型对象转为字符串
     *
     * <p>
     * {@code path} 参数表示导致错误的 Controller 方法的参数的路径, 有两种情况
     * </p>
     *
     * <ul>
     * <li>简单参数: 即 {@code int}, {@code String} 这类, 路径格式为 {@code 方法名.参数名}</li>
     * <li>符合对象参数: 即参数为一个对象, 路径格式为 {@code 方法名.参数名.字段名}</li>
     * </ul>
     *
     * <p>
     * 一般情况下这类异常反馈给客户端为 {@code 400 BAD_REQUEST} 错误响应
     * </p>
     *
     * @param path {@link Path} 类型对象, 表示导致错误的 Controller 参数路径
     * @return 字符串结果
     */
    private String pathToPropertyName(Path path) {
        var result = new ArrayList<String>();

        // 获取 path 对象的迭代器, 遍历所有的路径节点
        var it = path.iterator();
        // 跳过第一个节点, 该节点表示调用的 controller 方法名
        if (it.hasNext()) {
            it.next();
        }

        // 遍历之后的节点, 进行节点名进行保存
        while (it.hasNext()) {
            result.add(it.next().getName());
        }

        // 将节点名集合用 . 符合连接
        return Joiner.on(".").join(result);
    }

    /**
     * 处理 Controller 参数缺失异常
     *
     * <p>
     * 处理 {@link MissingServletRequestParameterException} 类型异常
     * </p>
     *
     * <p>
     * 该异常表示缺失必要的请求参数, 导致对应的 Controller 方法无法被调用
     * </p>
     *
     * <p>
     * 一般情况下这类异常反馈给客户端为 {@code 400 BAD_REQUEST} 错误响应
     * </p>
     *
     * @param e {@link MissingServletRequestParameterException} 类型异常对象
     * @return 包装为 {@link ResponseWrapper} 类型的响应结果
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseWrapper<ResponseWrapper.ErrorDetail> handle(MissingServletRequestParameterException e) {
        warnLog(e);

        return ResponseWrapper.error(
            HttpStatus.BAD_REQUEST.value(), // 为此种错误定义代码和错误信息, 此处暂用 400 类型错误代码和信息
            "missing_request_args",
            ResponseWrapper.ErrorDetail.withErrorParameters(
                Map.of(e.getParameterName(), new String[] { e.getLocalizedMessage() })));
    }

    /**
     * 处理客户端异常
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
    public ResponseEntity<ResponseWrapper<Void>> handle(HttpClientErrorException e) {
        warnLog(e);

        var resp = ResponseWrapper.error(e.getStatusCode().value(), e.getStatusText());
        return new ResponseEntity<>(resp, e.getStatusCode());
    }

    /**
     * 处理客户端异常
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
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseWrapper<Void>> handle(AuthenticationException e) {
        warnLog(e);

        var resp = ResponseWrapper.error(HttpStatus.UNAUTHORIZED.value(), e.getLocalizedMessage());
        return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
    }
}
