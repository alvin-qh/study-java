package alvin.study.quarkus.web.endpoint;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * 测试 {@link RestfulResource} 类, RESTful API 调用
 */
@QuarkusTest
public class RestfulResourceTest {
    /**
     * 测试 {@link RestfulResource#hello(String)} 方法
     *
     * <p>
     * 通过 {@code restful/hello} 地址调用, 测试 GET 方法
     * </p>
     */
    @Test
    void hello_shouldGetHelloResource() {
        given().when()
                .get("restful/hello")
                .then()
                .statusCode(200)
                .body(is("Hello Quarkus"));
    }

    /**
     * 测试 {@link RestfulResource#hello(String)} 方法, 并传递请求参数
     *
     * <p>
     * 通过 {@code restful/hello} 地址调用, 测试 GET 方法, 并传递请求参数
     * </p>
     */
    @Test
    void hello_shouldGetHelloResourceWithParameter() {
        given().when()
                .get("restful/hello?name=Alvin")
                .then()
                .statusCode(200)
                .body(is("Hello Alvin"));
    }
}
