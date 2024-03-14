package alvin.study.quarkus.web.endpoint;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import alvin.study.quarkus.web.endpoint.model.ErrorDto;
import alvin.study.quarkus.web.endpoint.model.UserDto;
import alvin.study.quarkus.web.interceptor.Response;
import alvin.study.quarkus.web.persist.DataSource;
import alvin.study.quarkus.web.persist.entity.Gender;
import alvin.study.quarkus.web.persist.entity.User;
import io.quarkus.hibernate.validator.runtime.jaxrs.ViolationReport.Violation;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response.Status;

/**
 * 测试 {@link RestfulResource} 类, RESTful API 调用
 */
@QuarkusTest
class RestfulResourceTest {
    // 注入数据源对象
    @Inject
    DataSource dataSource;

    /**
     * 测试 {@link RestfulResource#hello(String)} 方法, 使用默认语言
     *
     * <p>
     * 通过 {@code restful/hello} 地址调用, 测试 GET 方法
     * </p>
     */
    @Test
    void hello_shouldGetHelloResourceByDefaultLocale() {
        given().when()
                .get("restful/hello")
                .then()
                .statusCode(200)
                .body(is("Hello Quarkus"));
    }

    /**
     * 测试 {@link RestfulResource#hello(String)} 方法, 指定语言
     *
     * <p>
     * 通过 {@code restful/hello} 地址调用, 测试 GET 方法, 设置 {@code Accept-Language} 头属性
     * </p>
     */
    @Test
    void hello_shouldGetHelloResourceByGivenLocale() {
        given().when()
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh;q=0.9,en;q=0.7,*;q=0.5")
                .get("restful/hello")
                .then()
                .statusCode(200)
                .body(is("你好 Quarkus"));
    }

    /**
     * 测试 {@link RestfulResource#numbers(int)} 方法, 使用默认语言
     *
     * <p>
     * 通过 {@code restful/numbers} 地址调用, 测试 GET 方法, 设置 {@code Accept-Language} 头属性
     * </p>
     */
    @Test
    void numbers_shouldGetHelloResourceByDefaultLocale() {
        given().when()
                .get("restful/numbers?digit=5")
                .then()
                .statusCode(200)
                .body(is("five"));
    }

    /**
     * 测试 {@link RestfulResource#numbers(int)} 方法, 使用指定语言
     *
     * <p>
     * 通过 {@code restful/numbers} 地址调用, 测试 GET 方法, 设置 {@code Accept-Language} 头属性
     * </p>
     */
    @Test
    void numbers_shouldGetHelloResourceByGivenLocale() {
        given().when()
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh;q=0.9,en;q=0.7,*;q=0.5")
                .get("restful/numbers?digit=5")
                .then()
                .statusCode(200)
                .body(is("五"));
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
                    new UserDto(
                        "001",
                        "Alvin",
                        LocalDate.of(1981, 3, 17),
                        Gender.MALE),
                    new UserDto(
                        "002",
                        "Emma",
                        LocalDate.of(1985, 3, 29),
                        Gender.FEMALE));
    }

    /**
     * 测试 {@link RestfulResource#createUser(UserDto)} 方法, 通过 RESTful API 创建实体对象
     *
     * <p>
     * 通过 {@code restful/users} 地址调用, 测试 POST 方法, 创建实体对象
     * </p>
     */
    @Test
    void users_shouldCreateUser() {
        // 发送 POST 请求
        var resp = given().when()
                .contentType(ContentType.JSON)
                .body(
                    UserDto.builder()
                            .name("Alvin")
                            .birthday(LocalDate.of(1981, 3, 17))
                            .gender(Gender.MALE)
                            .build())
                .post("restful/users")
                .then()
                .statusCode(Status.CREATED.getStatusCode()) // 确认返回 201 状态码
                .extract()
                .as(new TypeRef<Response<User>>() {});

        // 确认响应结果
        then(resp)
                .extracting(Response::ok, Response::path)
                .contains(true, "/restful/users");

        // 确认返回结果中包含 id 属性
        var id = resp.payload().id();
        then(id).isNotEmpty();

        // 通过返回的 id 属性获取存储的实体对象
        var user = dataSource.<User>get(id);
        then(user).isNotNull();

        // 确认返回的响应和获取的实体对象一致
        then(user)
                .extracting(
                    User::id,
                    User::name,
                    User::birthday,
                    User::gender)
                .contains(
                    resp.payload().id(),
                    resp.payload().name(),
                    resp.payload().birthday(),
                    resp.payload().gender());
    }

    /**
     * 测试 {@link RestfulResource#createUser(UserDto)} 方法, 且验证未通过的情况
     *
     * <p>
     * 通过 {@code restful/users} 地址调用, 测试 POST 方法, 本例中传递错误参数, 测试验证器未通过返回错误的情况
     * </p>
     *
     * <p>
     * 参考 {@link UserDto#name()} 属性上面标记的 {@link NotBlank @NotBlank} 注解
     * </p>
     */
    @Test
    void users_shouldCreateUserButValidatedFailed() {
        // 发送 POST 请求
        var resp = given().when()
                .contentType(ContentType.JSON)
                .body(
                    // 少传递一个 name 参数, 这样会导致 UserDto 的 name 属性 NotBlank 生效, 导致验证失败
                    UserDto.builder()
                            .birthday(LocalDate.of(1981, 3, 17))
                            .gender(Gender.MALE)
                            .build())
                .post("restful/users")
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode()) // 确认返回 201 状态码
                .extract()
                .as(new TypeRef<Response<ErrorDto>>() {});

        // 确认响应结果
        then(resp)
                .extracting(Response::ok, Response::path)
                .contains(false, "/restful/users");

        then(resp.payload())
                .extracting(ErrorDto::status, ErrorDto::message)
                .contains(400, "Constraint Violation");

        then(resp.payload().violations()).hasSize(1)
                .extracting(Violation::getField, Violation::getMessage)
                .contains(tuple("createUser.user.name", "value cannot be blank"));
    }

    /**
     * 测试 {@link RestfulResource#createUser(UserDto)} 方法, 且验证未通过的情况
     *
     * <p>
     * 通过 {@code restful/users} 地址调用, 测试 POST 方法, 本例中传递错误参数, 测试验证器未通过返回错误的情况,
     * 本例设置了 {@code Accept-Language} 请求头, 设置指定语言
     * </p>
     *
     * <p>
     * 参考 {@link UserDto#name()} 属性上面标记的 {@link NotBlank @NotBlank} 注解
     * </p>
     */
    @Test
    void users_shouldCreateUserButValidatedFailedWithGivenLocale() {
        // 发送 POST 请求
        var resp = given().when()
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh;q=0.9,en;q=0.7,*;q=0.5")
                .contentType(ContentType.JSON)
                .body(
                    // 少传递一个 name 参数, 这样会导致 UserDto 的 name 属性 NotBlank 生效, 导致验证失败
                    UserDto.builder()
                            .birthday(LocalDate.of(1981, 3, 17))
                            .gender(Gender.MALE)
                            .build())
                .post("restful/users")
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode()) // 确认返回 201 状态码
                .extract()
                .as(new TypeRef<Response<ErrorDto>>() {});

        // 确认响应结果
        then(resp)
                .extracting(Response::ok, Response::path)
                .contains(false, "/restful/users");

        then(resp.payload())
                .extracting(ErrorDto::status, ErrorDto::message)
                .contains(400, "Constraint Violation");

        then(resp.payload().violations()).hasSize(1)
                .extracting(Violation::getField, Violation::getMessage)
                .contains(tuple("createUser.user.name", "不能为空"));
    }
}
