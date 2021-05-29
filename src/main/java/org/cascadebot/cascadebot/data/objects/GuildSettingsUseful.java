package org.cascadebot.cascadebot.data.objects;

import org.cascadebot.cascadebot.commandmeta.Module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SettingsContainer(module = Module.USEFUL)
public class GuildSettingsUseful {

    private Map<String, TodoList> todoLists = new ConcurrentHashMap<>();

    boolean writeMode = false;

    //region todo list stuff
    public TodoList getTodoList(String name) {
        return todoLists.get(name);
    }

    public TodoList createTodoList(String name, long owner) {
        if (!writeMode) throw new UnsupportedOperationException("Cannot modify Guild data if not in write mode!");
        if (todoLists.containsKey(name)) {
            return null;
        }
        TodoList todoList = new TodoList(owner);
        todoLists.put(name, todoList);
        return todoList;
    }

    public void deleteTodoList(String name) {
        if (!writeMode) throw new UnsupportedOperationException("Cannot modify Guild data if not in write mode!");
        todoLists.remove(name);
    }

    public TodoList getTodoListByMessage(long messageId) {
        if (!writeMode) throw new UnsupportedOperationException("Cannot modify Guild data if not in write mode!");
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
