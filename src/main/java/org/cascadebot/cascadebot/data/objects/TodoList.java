package org.cascadebot.cascadebot.data.objects;

import lombok.Getter;

import java.util.List;

public class TodoList {

    @Getter
    private String name;

    @Getter
    private List<TodoListItem> items;

    @Getter
    private long ownerId;

    //List of users id who are able to access this list
    private List<Long> users;

    private TodoList() {
        //Constructor for mongodb
    }

    public TodoList(String name, long ownerId) {
        this.name = name;
        this.ownerId = ownerId;
    }

    public void addTodoItem(String text) {
        items.add(new TodoListItem(text));
    }

    public void removeTodoItem(int id) {
        items.remove(id);
    }

    public boolean canUserEdit(Long id) {
        return ownerId == id || users.contains(id);
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
