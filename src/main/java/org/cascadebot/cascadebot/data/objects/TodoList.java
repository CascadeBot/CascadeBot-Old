package org.cascadebot.cascadebot.data.objects;

import lombok.Getter;

import java.util.List;

public class TodoList {

    @Getter
    private String name;

    @Getter
    private List<TodoListItem> items;

    private TodoList() {
        //Constructor for mongodb
    }

    public TodoList(String name) {
        this.name = name;
    }

    public void addTodoItem(String text) {
        items.add(new TodoListItem(text));
    }

    public void removeTodoItem(int id) {
        items.remove(id);
    }

    public class TodoListItem {

        boolean done;

        String text;

        private TodoListItem() {
            //Constructor for mongodb
        }

        TodoListItem(String text) {
            this.text = text;
            done = false;
        }

    }

}
