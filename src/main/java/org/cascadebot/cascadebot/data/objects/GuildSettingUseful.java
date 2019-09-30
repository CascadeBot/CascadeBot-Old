package org.cascadebot.cascadebot.data.objects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuildSettingUseful {

    public GuildSettingUseful() {

    }

    private Map<String, TodoList> todoLists = new ConcurrentHashMap<>();

    //region todo list stuff
    public TodoList getTodoList(String name) {
        return todoLists.get(name);
    }

    public TodoList createTodoList(String name) {
        if (todoLists.containsKey(name)) {
            return null;
        }
        TodoList todoList = new TodoList(name);
        todoLists.put(name, todoList);
        return todoList;
    }

    public void deleteTodoList(String name) {
        todoLists.remove(name);
    }
    //endregion

}
