package alvin.study.quarkus.web.endpoint;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.HttpHeaders;

/**
 * 测试 {@link TemplatedResource} 类, 通过模板渲染响应内容
 */
@QuarkusTest
class TemplatedResourceTest {
    /**
     * 测试 {@link TemplatedResource#unchecked(String, String, String)} 方法
     *
     * <p>
     * 测试 unchecked 模板渲染
     * </p>
     */
    @Test
    void unchecked_shouldRenderUncheckedTemplate() {
        given().when()
                .get("template/unchecked?name=Alvin&gender=MALE&birthday=1999-03-17")
                .then()
                .statusCode(200)
                .body(is("""
                    Hello Alvin

                    - Name: Alvin
                    - Gender: MALE
                    - Birthday: 1999-03-17
                    """));
    }

    /**
     * 测试 {@link TemplatedResource#checked(String, String, String)} 方法
     *
     * <p>
     * 测试 checked 模板渲染
     * </p>
     */
    @Test
    void checked_shouldRenderCheckedTemplate() {
        given().when()
                .get("template/checked?name=Alvin&gender=MALE&birthday=1999-03-17")
                .then()
                .statusCode(200)
                .body(allOf(
                    containsString("Hello"),
                    containsString("Mr."),
                    containsString("Alvin"),
                    containsString("<li>Gender: 男</li>"),
                    containsString("<li>Birthday: 1999年03月17日</li>"),
                    containsString("<span>Name: Alvin</span>"),
                    // 验证国际化信息
                    containsString("<div>i18n: five</div>")));
    }

    /**
     * 测试 {@link TemplatedResource#checked(String, String, String)} 方法
     *
     * <p>
     * 测试 checked 模板渲染, 加入 {@code Accept-Language} 头属性, 指定国际化信息
     * </p>
     */
    @Test
    void checked_shouldRenderCheckedTemplateByAcceptLanguage() {
        given().when()
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh;q=0.9, en;q=0.8, *;q=0.5")
                .get("template/checked?name=Alvin&gender=MALE&birthday=1999-03-17")
                .then()
                .statusCode(200)
                .body(allOf(
                    containsString("Hello"),
                    containsString("Mr."),
                    containsString("Alvin"),
                    containsString("<li>Gender: 男</li>"),
                    containsString("<li>Birthday: 1999年03月17日</li>"),
                    containsString("<span>Name: Alvin</span>"),
                    // 验证国际化信息
                    containsString("<div>i18n: 五</div>")));
    }
}
