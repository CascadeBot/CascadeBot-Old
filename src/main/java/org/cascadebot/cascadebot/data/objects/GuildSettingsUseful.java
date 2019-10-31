package org.cascadebot.cascadebot.data.objects;

import org.cascadebot.cascadebot.commandmeta.Module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SettingsContainer(module = Module.USEFUL)
public class GuildSettingsUseful {

    private Map<String, TodoList> todoLists = new ConcurrentHashMap<>();

    //region todo list stuff
    public TodoList getTodoList(String name) {
        return todoLists.get(name.toLowerCase());
    }

    public TodoList createTodoList(String name, long owner) {
        if (todoLists.containsKey(name.toLowerCase())) {
            return null;
        }
        TodoList todoList = new TodoList(owner);
        todoLists.put(name.toLowerCase(), todoList);
        return todoList;
    }

    public void deleteTodoList(String name) {
        todoLists.remove(name.toLowerCase());
    }

    public TodoList getTodoListByMessage(long messageId) {
        TodoList list = null;
        for (Map.Entry<String, TodoList> listEntry : todoLists.entrySet()) {
            if (listEntry.getValue().getMessageId() == messageId) {
                list = listEntry.getValue();
            }
        }

        return list;
    }
    //endregion

}
