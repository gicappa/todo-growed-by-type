package todo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Greeter")
public class GreeterTest {

    private Greeter greeter;

    @BeforeEach
    void beforeEach() {
        greeter = new Greeter();
    }

    @Test
    @DisplayName("returns a message to greet people")
    void it_return_a_message() {
        assertThat(greeter.message()).isNotEmpty();
    }
}
