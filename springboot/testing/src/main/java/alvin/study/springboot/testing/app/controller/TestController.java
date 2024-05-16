package alvin.study.springboot.testing.app.controller;

import alvin.study.springboot.testing.model.TestModel;
import com.google.common.base.Strings;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Random;

/**
 * 用于演示 Controller 测试方法的类型
 *
 * <p>
 * 对于 Controller, Spring Boot 框架提供了 3 种测试方法
 * <ul>
 * <li>
 * 通过 {@link org.springframework.boot.test.web.client.TestRestTemplate TestRestTemplate} 作为 Http 客户端调用服务
 * </li>
 * <li>
 * 通过 {@link org.springframework.test.web.reactive.server.WebTestClient WebTestClient} 作为 Http 客户端调用服务
 * </li>
 * <li>
 * 通过 {@link org.springframework.test.web.servlet.MockMvc MockMvc} 作为 Http 客户端调用服务
 * </li>
 * </ul>
 * </p>
 */
@Validated
@RestController
@RequestMapping("/testing")
public class TestController {
    private static final Random RANDOM = new Random();

    /**
     * 供测试使用的 {@code get} 方法
     *
     * @param name  非空字符串参数
     * @param clock 一个时间字符串参数, 用于 mock {@link Instant#now(Clock)} 方法
     * @return {@link TestModel} 对象
     */
    @GetMapping
    @ResponseBody
    TestModel get(
        @RequestParam("name") String name,
        @RequestParam(name = "clock", required = false) String clock) {

        final Clock c;
        if (Strings.isNullOrEmpty(clock)) {
            // 如果不存在 clock 参数, 则使用系统定义的 Clock 对象, 此时 Instant.now(c) 返回系统当前时间
            c = Clock.systemUTC();
        } else {
            // 如果存在 clock 参数, 则根据 clock 参数的值构建 Clock 对象, 此时 Instant.now(c) 返回 clock
            // 参数指定的时间
            c = Clock.fixed(Instant.parse(clock), ZoneOffset.UTC);
        }
        // 返回结果对象
        return new TestModel((long) RANDOM.nextInt(10000), name, Instant.now(c));
    }
}
