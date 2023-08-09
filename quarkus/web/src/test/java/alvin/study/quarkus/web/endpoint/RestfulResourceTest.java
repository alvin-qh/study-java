package alvin.study.quarkus.web.endpoint;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class RestfulResourceTest {
    @Test
    void hello_shouldGetHelloResource() {
        given()
            .when().get("restful/hello")
            .then()
            .statusCode(200)
            .body(is("Hello Quarkus"));
    }

    @Test
    void hello_shouldGetHelloResourceWithParameter() {
        given()
            .when().get("restful/hello?name=Alvin")
            .then()
            .statusCode(200)
            .body(is("Hello Alvin"));
    }
}
