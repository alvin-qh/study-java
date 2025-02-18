package alvin.study.springboot.mvc.app.api.controller;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import alvin.study.springboot.mvc.app.api.advice.ApiResponseAdvice;
import alvin.study.springboot.mvc.app.api.interceptor.ApiHandlerInterceptor;
import alvin.study.springboot.mvc.app.api.model.HelloDto;
import alvin.study.springboot.mvc.app.api.model.HelloForm;
import alvin.study.springboot.mvc.core.http.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * API 调用
 *
 * <p>
 * {@link Validated} 注解的作用是将验证错误以异常形式进行处理, 如果不加此注解, 则对请求参数验证失败后不会抛出异常, 而是以
 * {@link org.springframework.validation.BindingResult BindingResult} 类型参数传入
 * Controller 方法中
 * </p>
 *
 * <p>
 * 注意, 如果要使用 {@link org.springframework.validation.BindingResult
 * BindingResult} 参数, 则该参数在参数列表中必须紧跟被校验的最后一个参数之后
 * </p>
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
 * {@link RequestMapping @RequestMapping} 注解表示访问该 Controller 的 url 前缀
 * </p>
 *
 * <p>
 * 测试调用: <a href="http://localhost:8080/api/hello?name=Alvin">http://localhost:8080/api/hello?name=Alvin</a>
 * </p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/hello")
public class HelloController {
    /**
     * 处理 GET 请求
     *
     * <p>
     * 该方法从请求的 {@code QueryString} 中获取参数, 并返回一个模型对象
     * </p>
     *
     * <p>
     * {@link GetMapping @GetMapping} 注解表示该方法处理一个 GET 请求, 可以进一步通过注解的 {@code value}
     * 属性设置下一级 url, 和类上 {@code @RequestMapping} 注解中定义的 url 共同组成请求的 url
     * </p>
     *
     * <p>
     * {@link RequestParam @RequestParam} 注解该参数会从请求的 {@code QueryString} 中解析得到
     * </p>
     *
     * <p>
     * {@link ResponseBody @ResponseBody} 注解表示返回的结果会被处理为 JSON 格式
     * </p>
     *
     * <p>
     * 该方法直接返回了 {@link ResponseWrapper} 类型对象, 所以
     * {@link ApiResponseAdvice ApiResponseAdvice}
     * 类不会对该方法的返回值进行处理
     * </p>
     *
     * <p>
     * 在该 Controller 方法执行前,
     * {@link ApiHandlerInterceptor} 拦截器会被执行,
     * 处理请求上下文
     * </p>
     *
     * @param name 姓名参数
     * @return {@link ResponseWrapper} 对象
     */
    @GetMapping
    @ResponseBody
    ResponseWrapper<HelloDto> get(@RequestParam("name") @Length(min = 3, max = 10) String name) {
        log.info("GET method of HelloController was called, name=\"{}\"", name);

        // 返回成功响应
        return ResponseWrapper.success(new HelloDto(name, "Hello"));
    }

    /**
     * 处理 POST 请求
     *
     * <p>
     * 该方法从请求的 Body 中获取 JSON 信息, 转换为 {@link HelloForm} 对象后, 作为参数传入方法
     * </p>
     *
     * <p>
     * {@link PostMapping @PostMapping} 注解表示该方法处理一个 POST 请求, 可以进一步通过注解的
     * {@code value} 属性设置下一级 url, 和类上 {@code @RequestMapping} 注解中定义的 url 共同组成请求的 url
     * </p>
     *
     * <p>
     * {@link RequestBody @RequestBody} 注解表示该参数会从请求 Body 中的 JSON 转换得到
     * </p>
     *
     * <p>
     * {@link ResponseBody @ResponseBody} 注解表示返回的结果会被处理为 JSON 格式
     * </p>
     *
     * <p>
     * 方法执行完毕后, 返回一个模型对象, 经由 {@link ApiResponseAdvice
     * ApiResponseAdvice} 类型方法包装为统一类型后返回客户端
     * </p>
     *
     * @param form 请求 body 对象
     * @return {@link HelloForm} 对象
     */
    @PostMapping
    @ResponseBody
    HelloDto post(@RequestBody @Valid HelloForm form) {
        // 打印日志
        log.info("POST method of HelloController was called, name=\"{}\"", form.name());

        // 返回成功响应
        return new HelloDto(form.name(), "Welcome");
    }
}
