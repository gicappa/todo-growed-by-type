package gk.todo.item;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemUseCaseTest {

    private ItemUseCase itemUseCase;

    @BeforeEach
    void beforeEach() {
        itemUseCase = new ItemUseCase();
    }

    @Test
    void it_should_add_a_new_item() {
        var actual = itemUseCase.addItem(TodoBoard.empty(), new Item());

        assertThat(actual.count()).isEqualTo(1);
    }
}
