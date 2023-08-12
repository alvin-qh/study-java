package alvin.study.quarkus.web.endpoint;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import alvin.study.quarkus.web.endpoint.model.Gender;
import alvin.study.quarkus.web.endpoint.model.UserDto;
import alvin.study.quarkus.web.interceptor.Response;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;

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
                .get("restful/hello?name={name}", "Alvin")
                .then()
                .statusCode(200)
                .body(is("Hello Alvin"));
    }

    /**
     * 测试 {@link RestfulResource#users()} 方法, 返回 {@link UserDto} 对象集合
     *
     * <p>
     * 通过 {@code restful/users} 地址调用, 测试 GET 方法
     * </p>
     */
    @Test
    void users_shouldGetUserList() {
        // 发出请求, 并获取响应结果
        var resp = given().when()
                .get("restful/users")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<Response<List<UserDto>>>() {});

        // 确认响应结果符合预期
        then(resp)
                .extracting(
                    Response::ok,
                    Response::path)
                .contains(true, "/restful/users");

        then(resp.payload()).hasSize(2)
                .contains(
                    new UserDto("Alvin", LocalDate.of(1981, 3, 17), Gender.MALE),
                    new UserDto("Emma", LocalDate.of(1985, 3, 29), Gender.FEMALE));
    }
}
