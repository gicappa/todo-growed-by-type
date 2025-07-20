package gk.todo.item;

import java.util.ArrayList;
import java.util.List;

public record TodoBoard(List<Item> items) {

    public static TodoBoard empty() {
        return new TodoBoard(new ArrayList<>());
    }

    public int count() {
        return items.size();
    }
}
