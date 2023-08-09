package alvin.study.quarkus.web.endpoint;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TemplatedResourceTest {
    @Test
    void unchecked_shouldRenderUncheckedTemplate() {
        given()
            .when().get("template/unchecked?name=Alvin&gender=FEMALE&birthday=1999-03-17")
            .then()
            .statusCode(200)
            .body(is("""
                Hello Alvin

                - Name: Alvin
                - Gender: FEMALE
                - Birthday: 1999-03-17
                """));
    }

    @Test
    void checked_shouldRenderCheckedTemplate() {
        given()
            .when().get("template/checked?name=Alvin&gender=MALE&birthday=1999-03-17")
            .then()
            .statusCode(200)
            .body(allOf(
                containsString("Hello"),
                containsString("Mr."),
                containsString("Alvin"),
                containsString("<li>Gender: 男</li>"),
                containsString("<li>Birthday: 1999年03月17日</li>"),
                containsString("<span>Name: Alvin</span>")
            ));
    }
}
