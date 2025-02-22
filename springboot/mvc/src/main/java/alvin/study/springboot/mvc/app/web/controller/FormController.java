package alvin.study.springboot.mvc.app.web.controller;

import java.nio.charset.StandardCharsets;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriUtils;

import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.mvc.app.web.model.Form;
import alvin.study.springboot.mvc.conf.WebConfig;

/**
 * 页面请求 Controller
 *
 * <p>
 * {@link Controller @Controller} 注解表示当前类型为一个 MVC 控制器类, 类中的方法将接收请求并返回一个 HTML 页面
 * </p>
 *
 * <p>
 * {@link RequestMapping @RequestMapping} 注解表示访问当前控制器的 url 地址
 * </p>
 *
 * <p>
 * 在当前 Controller 方法执行前,
 * {@link org.springframework.web.servlet.i18n.LocaleChangeInterceptor
 * LocaleChangeInterceptor} 拦截器会被执行, 为请求获取 {@link java.util.Locale Locale}
 * 本地化对象
 * </p>
 *
 * <p>
 * 除了通过用请求头的 {@code Accept-Language} 属性创建本地化对象外, 也可以通过请求中的 {@code lang}
 * 参数来直接指定本地化语言代码, 这个操作是由
 * {@link org.springframework.web.servlet.i18n.LocaleChangeInterceptor
 * LocaleChangeInterceptor} 拦截器完成
 * </p>
 *
 * <p>
 * 上述涉及到的拦截器均是通过:
 * {@link WebConfig#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 * WebConfig.addInterceptors(InterceptorRegistry)} 方法进行注册
 * </p>
 *
 * <p>
 * 若某次请求携带了本地化设置参数或信息, 则会通过 {@link WebConfig#localeResolver()
 * WebConfig.localeResolver()} 方法, 设置本地化信息的持久化方法, 本例中是持久化到 Cookie 中
 * </p>
 *
 * <p>
 * Controller 方法返回后, 下一步会通过模板引擎对对应的 HTML 进行渲染, 模板引擎的设置参考:
 * {@link WebConfig#templateResolver()
 * WebConfig.templateResolver()}
 * </p>
 *
 * <p>
 * 对于浏览器的镜头资源请求 (js, css, image 等), 参见
 * {@link WebConfig#addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry)
 * WebConfig.addResourceHandlers(ResourceHandlerRegistry)} 方法的设置
 * </p>
 *
 * <p>
 * 测试调用: <a href="http://localhost:8080/web/form">http://localhost:8080/web/form</a> 访问
 * </p>
 */
@Slf4j
@Controller
@RequestMapping("/web/form")
public class FormController {
    /**
     * GET 请求获取页面
     *
     * <p>
     * 该请求返回 {@code classpath:/templates/form.html} 页面的内容, 在客户端浏览器呈现一个表单页面
     * </p>
     *
     * <p>
     * {@link GetMapping @GetMapping} 注解表示该方法处理一个 GET 请求, 可以进一步通过注解的 {@code value}
     * 属性设置下一级 url, 和类上 {@code @RequestMapping} 注解中定义的 url 共同组成请求的 url
     * </p>
     *
     * <p>
     * Spring 采用了标准的 MVC 模型处理 Controller 方法, 模型可以从参数传入, 存储一些键值对后, 送入模板引擎中使用, 参考:
     * {@link Model} 类型
     * </p>
     *
     * <p>
     * 方法的返回值可以为一个 {@link String} 类型, 表示模板引擎要渲染的页面名称; 也可以返回一个
     * {@link org.springframework.web.servlet.ModelAndView ModelAndView} 类型对象,
     * 可以设置更多对响应的设置 (例如 Http 状态码等)
     * </p>
     *
     * @param model 页面模型对象
     * @return 页面名称, 用于传递给模板引擎进行页面渲染
     */
    @GetMapping
    String get(Model model) {
        log.info("GET method of FormController was called");

        // 设置空表单
        model.addAttribute("form", Form.EMPTY);

        // 返回页面名称, 即 classpath:/templates/form.html
        return "form";
    }

    /**
     * POST 表单
     *
     * <p>
     * {@link PostMapping @PostMapping} 注解表示该方法处理一个 POST 请求, 可以进一步通过注解的
     * {@code value} 属性设置下一级 url, 和类上 {@code @RequestMapping} 注解中定义的 url 共同组成请求的 url
     * </p>
     *
     * <p>
     * 浏览器提交表单的请求 Body 为 {@code application/x-www-form-urlencoded} 或
     * {@code multipart/form-data} 格式, 会被解析为 {@link Form} 类型对象
     * </p>
     *
     * <p>
     * {@link Valid @Valid} 注解用于对参数进行校验, 配合紧随其后的
     * {@link BindingResult} 对象, 用来传递验证后的错误信息. 注意: {@link BindingResult} 参数要紧随
     * {@link Valid @Valid} 注解的参数, 否则无效
     * </p>
     *
     * @param form  请求的 Body ({@code enctype} 为
     *              {@code application/x-www-form-urlencoded} 或
     *              {@code multipart/form-data} 类型) 解析后转换的 {@link Form Form} 对象
     * @param br    表单验证错误结果
     * @param model 页面模型对象
     * @return 如果表单参数验证失败, 则转发到 {@code classpath:/templates/form.html} 页面渲染表单值和错误信息,
     *         否则重定向到 {@code /web/result} 请求
     */
    @PostMapping
    String post(@Valid Form form, BindingResult br, Model model) {
        log.info("POST method of FormController was called");

        if (br.hasFieldErrors()) {
            // 在模型中添加错误的表单信息
            model.addAttribute("form", form);
            // 在模型中添加表单验证错误信息
            model.addAttribute("error", br);
            return "form";
        }

        // 重定向到 /web/result 请求地址, 并将表单内容作为参数发送
        return String.format(
            "redirect:/web/result?name=%s&age=%d&gender=%s",
            UriUtils.encode(form.getName(), StandardCharsets.UTF_8),
            form.getAge(),
            form.getGender());
    }
}
