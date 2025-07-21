package todo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
@DisplayName("The App")
public class AppTest {

    private App app;
    private Greeter greeter;

    @BeforeEach
    void beforeEach() {
        greeter = mock(Greeter.class);
        app = new App(greeter);
    }

    @Test
    @DisplayName("invokes the greeter message")
    public void it_invoke_greeter() {
        app.run();

        verify(greeter).message();
    }
}
