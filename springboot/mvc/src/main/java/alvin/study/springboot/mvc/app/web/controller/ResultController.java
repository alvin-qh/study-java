package alvin.study.springboot.mvc.app.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.mvc.app.web.model.Form;

/**
 * 展示表单提交结果的页面请求
 */
@Slf4j
@Controller
@RequestMapping("/web/result")
public class ResultController {
    /**
     * 获取页面
     *
     * <p>
     * {@link RequestParam @RequestParam} 注解该参数会从请求的 {@code QueryString} 中解析得到
     * </p>
     *
     * <p>
     * Spring 采用了标准的 MVC 模型处理 Controller 方法, 模型可以从参数传入, 存储一些键值对后, 送入模板引擎中使用, 参考:
     * {@link Model} 类型
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
     * @return 页面名称
     */
    @GetMapping
    String get(
            @RequestParam("name") String name,
            @RequestParam("age") Integer age,
            @RequestParam("gender") String gender,
            Model model) {
        log.info("GET method of ResultController was called");

        // 设置表单填写结果
        model.addAttribute("form", new Form(name, age, gender));

        // 返回页面名称, 即 classpath:/templates/result.html
        return "result";
    }
}
