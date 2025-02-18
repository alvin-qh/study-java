package alvin.study.springboot.mvc.app.api.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import alvin.study.springboot.mvc.app.api.interceptor.ApiHandlerInterceptor;
import alvin.study.springboot.mvc.app.api.model.I18nDto;
import alvin.study.springboot.mvc.core.i18n.I18n;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 根据请求获取 i18n 信息
 *
 * <p>
 * 可以改变请求头中的 {@code Accept-Language} 属性, 改变返回的文本内容
 * </p>
 *
 * <p>
 * 可以通过请求包含的 {@code lang} 参数, 改变返回的文本内容
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
 * 测试调用: <a href="http://localhost:8080/api/i18n?key=application.name&args=MVC">
 * http://localhost:8080/api/i18n?key=application.name&args=MVC
 * </a>
 * </p>
 *
 * <p>
 * 在该 Controller 方法执行前,
 * {@link ApiHandlerInterceptor} 拦截器会被执行,
 * 处理请求上下文
 * </p>
 *
 * @see I18n
 */
@Slf4j
@Validated // 对参数进行验证
@RestController
@RequestMapping("/api/i18n")
@RequiredArgsConstructor
public class I18nController {
    // Ii8n 对象
    private final I18n i18n;

    /**
     * 处理 GET 请求
     *
     * <p>
     * 根据请求的 Key 获取对应的 Message
     * </p>
     *
     * <p>
     * {@link GetMapping @GetMapping} 注解表示该方法处理一个 GET 请求, 可以进一步通过注解的 {@code value} 属
     * 设置下一级 url, 和类上 {@code @RequestMapping} 注解中定义的 url 共同组成请求的 url
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
     * @param key  {@code /i18n/message.properties} 文件的 Key
     * @param args 格式化 i18n 字符串的参数
     * @return {@link I18nDto} 对象, 包含请求的 Key 和 生成的 Message
     * @see I18n#getMessage(String, Object...)
     */
    @GetMapping
    @ResponseBody
    I18nDto get(
            @RequestParam("key") @NotBlank String key,
            @RequestParam(name = "args", defaultValue = "", required = false) List<String> args) {
        log.info("GET method of I18nController was called, key=\"{}\"", key);

        // 返回成功响应
        return new I18nDto(key, i18n.getMessage(key, args.toArray()));
    }
}
