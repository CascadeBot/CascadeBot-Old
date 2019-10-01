package org.cascadebot.cascadebot.data.objects;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class TodoList {

    @Getter
    private List<TodoListItem> items = new ArrayList<>();

    @Getter
    private long ownerId;

    //List of users id who are able to access this list
    private List<Long> users = new ArrayList<>();

    private TodoList() {
        //Constructor for mongodb
    }

    public TodoList(long ownerId) {
        this.ownerId = ownerId;
    }

    public int addTodoItem(String text) {
        TodoListItem item = new TodoListItem(text);
        items.add(item);
        return items.indexOf(item);
    }

    public TodoListItem removeTodoItem(int id) {
        return items.remove(id);
    }

    public void addEditUser(Member member) {
        users.add(member.getIdLong());
    }

    public void removeEditUser(Member member) {
        users.remove(member.getIdLong());
    }

    public boolean canUserEdit(Long id) {
        return ownerId == id || users.contains(id);
    }

    public class TodoListItem {

        boolean done;

        @Getter
        private String text;

        private TodoListItem() {
            //Constructor for mongodb
        }

        TodoListItem(String text) {
            this.text = text;
            done = false;
        }

    }

}
