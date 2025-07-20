package gk.todo.item;

import java.util.ArrayList;

public class ItemUseCase {

    public TodoBoard addItem(TodoBoard board, Item item) {
        var items = new ArrayList<>(board.items());
        items.add(item);
        return new TodoBoard(items);
    }
}
