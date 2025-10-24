package alvin.study.springboot.testing.app.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

/**
 * 通过 {@link WebMvcTest @WebMvcTest} 注解进行 HTTP 测试
 *
 * <p>
 * {@link ActiveProfiles @ActiveProfiles} 注解用于指定活动配置名, 通过指定为 {@code "test"},
 * 令所有注解为 {@link org.springframework.context.annotation.Profile @Profile}
 * 的配置类生效, 且配置文件 {@code classpath:/application-test.yml} 生效
 * </p>
 *
 * <p>
 * {@link WebMvcTest @WebMvcTest} 注解可以为测试对象注入 {@link MockMvc} 对象, 这是一个 http 客户端,
 * 可以访问指定的 Controller 对象对其进行验证, 其 {@code value} 属性指定了要测试的 Controller 类型,
 * 可以同时指定多个
 * </p>
 */
@ActiveProfiles("test") // 定义测试配置文件后缀, 令 application-test.yml 生效
@WebMvcTest({ TestController.class }) // 针对 TestController 进行测试
class MockMvcTest {
    // 用于 mock 当前时间
    private static final String CLOCK = "2022-10-01T08:00:00Z";

    // 注入 MockMvc 测试工具对象
    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试正确的 Web 调用, 返回 200 OK
     *
     * <p>
     * {@link MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
     * MockMvc.perform(RequestBuilder)} 方法用于执行一个 http 请求操作, 该请求操作通过一个
     * {@link org.springframework.test.web.servlet.RequestBuilder RequestBuilder}
     * 对象构造
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.servlet.request.MockMvcRequestBuilders#get(String, Object...)
     * MockMvcRequestBuilders.get(String, Object...)} 方法返回一个 {@code HTTP GET} 的
     * {@link org.springframework.test.web.servlet.RequestBuilder RequestBuilder} 对象
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.servlet.ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
     * ResultActions.andDo(ResultHandler)} 方法将 HTTP 调用结果传递给一个
     * {@link org.springframework.test.web.servlet.ResultHandler ResultHandler} 对象,
     * 进行所定义的操作. 这里使用的
     * {@link org.springframework.test.web.servlet.result.MockMvcResultHandlers#print()
     * MockMvcResultHandlers.print()} 操作用于将 HTTP 响应结果进行日志输出
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.servlet.ResultActions#andExpect(org.springframework.test.web.servlet.ResultMatcher)
     * ResultActions.andExpect(ResultMatcher)} 方法将 HTTP 调用结果传递给一个
     * {@link org.springframework.test.web.servlet.ResultMatcher ResultMatcher} 对象,
     * 对结果进行断言, 以此来验证结果是否正确
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.servlet.result.MockMvcResultMatchers#status()
     * MockMvcResultMatchers.status()} 方法返回一个
     * {@link org.springframework.test.web.servlet.result.StatusResultMatchers
     * StatusResultMatchers} 对象, 可用于对 HTTP 返回状态码的断言
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.servlet.result.MockMvcResultMatchers#jsonPath(String, org.hamcrest.Matcher)
     * MockMvcResultMatchers.jsonPath(String, Matcher)} 方法返回一个
     * {@link org.springframework.test.web.servlet.ResultMatcher ResultMatcher} 对象,
     * 可用于对 HTTP 返回 Body 中的 JSON 内容进行匹配
     * </p>
     */
    @Test
    @SneakyThrows
    void mockMvc_shouldGetResponse() {
        mockMvc.perform(get("/testing?name={name}&clock={clock}", "Alvin", CLOCK))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path", is(equalTo("/testing"))))
                .andExpect(jsonPath("$.retCode", is(equalTo(0))))
                .andExpect(jsonPath("$.retMsg", is(equalTo("success"))))
                .andExpect(jsonPath("$.payload.id", is(notNullValue())))
                .andExpect(jsonPath("$.payload.name", is(equalTo("Alvin"))))
                .andExpect(jsonPath("$.payload.timestamp", is(equalTo("2022-10-01T08:00:00Z"))));
    }

    /**
     * 测试错误的 Web 调用, 返回 400 BAD_REQUEST
     *
     * <p>
     * {@link MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
     * MockMvc.perform(RequestBuilder)} 方法用于执行一个 http 请求操作, 该请求操作通过一个
     * {@link org.springframework.test.web.servlet.RequestBuilder RequestBuilder}
     * 对象构造
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.servlet.request.MockMvcRequestBuilders#get(String, Object...)
     * MockMvcRequestBuilders.get(String, Object...)} 方法返回一个 {@code HTTP GET} 的
     * {@link org.springframework.test.web.servlet.RequestBuilder RequestBuilder} 对象
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.servlet.ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
     * ResultActions.andDo(ResultHandler)} 方法将 HTTP 调用结果传递给一个
     * {@link org.springframework.test.web.servlet.ResultHandler ResultHandler} 对象,
     * 进行所定义的操作. 这里使用的
     * {@link org.springframework.test.web.servlet.result.MockMvcResultHandlers#print()
     * MockMvcResultHandlers.print()} 操作用于将 HTTP 响应结果进行日志输出
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.servlet.ResultActions#andExpect(org.springframework.test.web.servlet.ResultMatcher)
     * ResultActions.andExpect(ResultMatcher)} 方法将 HTTP 调用结果传递给一个
     * {@link org.springframework.test.web.servlet.ResultMatcher ResultMatcher} 对象,
     * 对结果进行断言, 以此来验证结果是否正确
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.servlet.result.MockMvcResultMatchers#status()
     * MockMvcResultMatchers.status()} 方法返回一个
     * {@link org.springframework.test.web.servlet.result.StatusResultMatchers
     * StatusResultMatchers} 对象, 可用于对 HTTP 返回状态码的断言
     * </p>
     *
     * <p>
     * {@link org.springframework.test.web.servlet.result.MockMvcResultMatchers#jsonPath(String, org.hamcrest.Matcher)
     * MockMvcResultMatchers.jsonPath(String, Matcher)} 方法返回一个
     * {@link org.springframework.test.web.servlet.ResultMatcher ResultMatcher} 对象,
     * 可用于对 HTTP 返回 Body 中的 JSON 内容进行匹配
     * </p>
     */
    @Test
    @SneakyThrows
    void mockMvc_shouldGetResponseWithoutQueryParameters() {
        mockMvc.perform(get("/testing"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path", is(equalTo("/testing"))))
                .andExpect(jsonPath("$.retCode", is(equalTo(400))))
                .andExpect(jsonPath("$.retMsg", is(equalTo("missing_request_args"))))
                .andExpect(jsonPath("$.payload.errorParameters['name'][0]",
                    is(equalTo("Required request parameter 'name' for method parameter type String is not present"))));
    }
}
