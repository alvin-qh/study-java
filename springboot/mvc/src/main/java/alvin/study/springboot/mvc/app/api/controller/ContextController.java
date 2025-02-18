package alvin.study.springboot.mvc.app.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import alvin.study.springboot.mvc.app.api.advice.ApiResponseAdvice;
import alvin.study.springboot.mvc.app.api.interceptor.ApiHandlerInterceptor;
import alvin.study.springboot.mvc.app.api.model.ContextDto;
import alvin.study.springboot.mvc.core.context.Context;
import alvin.study.springboot.mvc.core.context.NoContextAttributeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试 {@link Context} 对象的使用
 *
 * <p>
 * 每个 Controller 方法都直接返回与之相关的 DTO 对象或直接抛出异常, 这些返回信息会通过
 * {@link ApiResponseAdvice ApiResponseAdvice} 类型处理后,
 * 包装为统一的类型返回到客户端
 * </p>
 *
 * <p>
 * {@link RestController @RestController} 注解表示当前 Controller 用于处理 Restful 请求,
 * 即请求和响应的 Body 中都为 JSON 格式字符串
 * </p>
 *
 * <p>
 * {@link RequestMapping @RequestMapping} 表示访问该 Controller 的 url 前缀
 * </p>
 *
 * <p>
 * 在该 Controller 方法执行前,
 * {@link ApiHandlerInterceptor} 拦截器会被执行,
 * 处理请求上下文
 * </p>
 *
 * <p>
 * 测试调用: <a href="http://localhost:8080/api/context">http://localhost:8080/api/context</a>,
 * 并在请求头中设置 {@code X-Consumer-Id} 属性
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/context")
@RequiredArgsConstructor
public class ContextController {
    // 注入 Context 对象用于管理上下文
    private final Context context;

    /**
     * 处理 GET 请求
     *
     * <p>
     * 如果请求 Header 中有 {@code X-Consumer-Id} 参数, 则该请求正确返回
     * </p>
     *
     * <p>
     * {@link GetMapping @GetMapping} 注解表示该方法处理一个 GET 请求, 可以进一步通过注解的 {@code value}
     * 属性设置下一级 url, 和类上 {@code @RequestMapping} 注解中定义的 url 共同组成请求的 url
     * </p>
     *
     * <p>
     * {@link ResponseBody @ResponseBody} 注解表示返回的结果会被处理为 JSON 格式
     * </p>
     *
     * @return {@link ContextDto} 对象, 包含用户 ID
     */
    @GetMapping
    @ResponseBody
    ContextDto get() {
        log.info("GET method of ContextController was called");

        try {
            // 通过 Context 对象获取属性
            String orgCode = context.get(Context.KEY_ORG_CODE);
            Long userId = context.get(Context.KEY_USER_ID);
            return new ContextDto(orgCode, userId);
        } catch (NoContextAttributeException e) {
            throw HttpClientErrorException.create(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                "no_org_code",
                HttpHeaders.EMPTY,
                null,
                null);
        }
    }
}
