package todo.app;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.matchesPattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("REST Calls")
public class TodoBoardTest {

    private static App app;

    @BeforeAll
    static void beforeAll() {
        app = new App();
        app.run();
    }


    @Test
    @DisplayName("POST /board - creates a new board")
    void it_invokes_POST_board() {
        given()
            .port(8080)
            .contentType("application/json")
            .body("""
                {
                  "name": "My Todo Board",
                  "capacity": 10
                }
                """)
            .when()
            .post("/boards")
            .then()
            .statusCode(201)
            .header("Location",
                matchesPattern(
                    ".*/boards/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
    }

}
