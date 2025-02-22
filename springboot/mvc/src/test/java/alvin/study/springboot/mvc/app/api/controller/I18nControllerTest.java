package alvin.study.springboot.mvc.app.api.controller;

import static org.assertj.core.api.BDDAssertions.then;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.mvc.WebTest;
import alvin.study.springboot.mvc.app.api.model.I18nDto;
import alvin.study.springboot.mvc.core.http.ResponseWrapper;

/**
 * 测试 {@link I18nController}, 国际化语言
 */
class I18nControllerTest extends WebTest {
    // 定义正确的响应类型
    private static final ParameterizedTypeReference<ResponseWrapper<I18nDto>> SUCCESS_TYPE
        = new ParameterizedTypeReference<>() {};

    /**
     * 测试 {@link I18nController#get(String, java.util.List)} 方法
     *
     * <p>
     * 按照系统默认的语言代码, 获取指定的文本信息
     * </p>
     */
    @Test
    void get_shouldGetMessageByDefaultLanguage() {
        // 发起 GET 测试请求/Applications/Visual Studio
        // Code.app/Contents/Resources/app/out/vs/code/electron-sandbox/workbench/workbench.html
        var resp = getJson(
            "/api/i18n?key={key}&args={args}",
            "application.name", "MVC")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(SUCCESS_TYPE).returnResult() // 获取响应结果
                    .getResponseBody(); // 获取响应结果的 body

        then(resp).isNotNull();

        // 确认返回的 key 正确
        then(resp.payload().key()).isEqualTo("application.name");

        // 确认返回的 message 正确
        then(resp.payload().message()).isEqualTo("Study Spring Boot MVC");
    }

    /**
     * 测试 {@link I18nController#get(String, java.util.List)} 方法
     *
     * <p>
     * 通过在请求 Header 中加入 {@code Accept-Language: zh-CN} 情况下获取的文本信息
     * </p>
     */
    @Test
    void get_shouldGetMessageByAcceptHeader() {
        // 发起 GET 测试请求
        var resp = getJson("/api/i18n?key={key}&args={args}", "application.name", "MVC")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN")
                .exchange()
                .expectStatus().isOk()
                .expectBody(SUCCESS_TYPE).returnResult() // 获取响应结果
                .getResponseBody(); // 获取响应结果的
        // body

        then(resp).isNotNull();

        // 确认返回的 key 正确
        then(resp.payload().key()).isEqualTo("application.name");

        // 确认返回的 message 正确
        then(resp.payload().message()).isEqualTo("Spring Boot MVC 学习");
    }

    /**
     * 测试 {@link I18nController#get(String, java.util.List)} 方法
     *
     * <p>
     * 通过在请求参数中加入 {@code lang=zh-CN} 情况下获取的文本信息
     * </p>
     */
    @Test
    void get_shouldGetMessageByLangParameter() {
        // 发起 GET 测试请求, 传递 lang 参数
        var resp = getJson("/api/i18n?key={key}&args={args}&lang={lang}", "application.name", "MVC", "zh-CN")
                .exchange()
                .expectStatus().isOk()
                .expectBody(SUCCESS_TYPE).returnResult() // 获取响应结果
                .getResponseBody(); // 获取响应结果的 body

        then(resp).isNotNull();

        // 确认返回的 key 正确
        then(resp.payload().key()).isEqualTo("application.name");

        // 确认返回的 message 正确
        then(resp.payload().message()).isEqualTo("Spring Boot MVC 学习");
    }
}
