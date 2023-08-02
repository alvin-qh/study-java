package alvin.study.quarkus.web.endpoint;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import alvin.study.quarkus.web.BaseTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class HelloResourceTest extends BaseTest {
    @Test
    void hello_shouldGetHelloResource() {
        given()
            .when().get("/hello")
            .then()
            .statusCode(200)
            .body(is("Hello"));
    }
}
