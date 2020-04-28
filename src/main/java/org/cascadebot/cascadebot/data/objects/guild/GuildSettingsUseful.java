package org.cascadebot.cascadebot.data.objects.guild;

import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.SettingsContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SettingsContainer(module = Module.USEFUL)
public class GuildSettingsUseful {

    private Map<String, TodoList> todoLists = new ConcurrentHashMap<>();

    //region todo list stuff
    public TodoList getTodoList(String name) {
        return todoLists.get(name);
    }

    public TodoList createTodoList(String name, long owner) {
        if (todoLists.containsKey(name)) {
            return null;
        }
        TodoList todoList = new TodoList(owner);
        todoLists.put(name, todoList);
        return todoList;
    }

    public void deleteTodoList(String name) {
        todoLists.remove(name);
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
