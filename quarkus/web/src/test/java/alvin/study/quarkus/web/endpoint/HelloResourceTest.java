package alvin.study.quarkus.web.endpoint;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import alvin.study.quarkus.web.BaseTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class HelloResourceTest extends BaseTest {
    @Test
    void helloSimple_shouldGetHelloResource() {
        given()
            .when().get("/hello/simple")
            .then()
            .statusCode(200)
            .body(is("Hello Quarkus"));
    }

    @Test
    void helloTemplate_shouldGetHelloResourceByTemplate() {
        given()
            .when().get("/hello/template?name=Alvin")
            .then()
            .statusCode(200)
            .body(is("Hello Alvin\n"));
    }

    @Test
    void helloCheckedTemplate_shouldGetHelloResourceByCheckedTemplate() {
        given()
            .when().get("/hello/checked-template?name=Alvin")
            .then()
            .statusCode(200)
            .body(containsString("<h1>Hello Alvin</h1>"));
    }
}
