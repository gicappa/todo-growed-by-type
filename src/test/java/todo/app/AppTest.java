package todo.app;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import todo.domain.Greeter;

/**
 * Unit test for simple App.
 */
@DisplayName("The App")
public class AppTest {

    private App app;

    @BeforeEach
    void beforeEach() {
        app = new App();
    }

    @Test
    @DisplayName("invokes the greeter message")
    public void it_invoke_greeter() {
//        app.run();
    }
}
