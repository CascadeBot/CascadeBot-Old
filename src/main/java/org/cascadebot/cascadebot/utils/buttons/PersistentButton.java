package org.cascadebot.cascadebot.utils.buttons;

import de.bild.codec.annotations.Transient;
import lombok.Getter;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.TodoList;

public enum PersistentButton {

    TODO_BUTTON_CHECK(new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if(!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        TodoList.TodoListItem item = todoList.getItems().get(todoList.getCurrentItem());
        item.setDone(true);
        todoList.addUncheckButton(message);
        message.editMessage(todoList.getTodoListMessage()).queue();
    })),

    TODO_BUTTON_UNCHECK(new Button.UnicodeButton(UnicodeConstants.WHITE_HALLOW_SQUARE, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if(!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        TodoList.TodoListItem item = todoList.getItems().get(todoList.getCurrentItem());
        item.setDone(false);
        todoList.addCheckButton(message);
        message.editMessage(todoList.getTodoListMessage()).queue();
    })),

    TODO_BUTTON_NAVIGATE_LEFT(new Button.UnicodeButton(UnicodeConstants.BACKWARD_ARROW, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if(!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        int currentPage = todoList.getCurrentItem() / 10 + 1;
        int start = currentPage * 10 - 10;

        if (start == 0) {
            return;
        }

        int newPos = Math.max(start - 10, 0);

        todoList.setCurrentItem(newPos);

        message.editMessage(todoList.getTodoListMessage()).queue();
        todoList.doCheckToggle(message);
    })),

    TODO_BUTTON_NAVIGATE_RIGHT(new Button.UnicodeButton(UnicodeConstants.FORWARD_ARROW, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if(!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        int currentPage = todoList.getCurrentItem() / 10 + 1;
        int start = currentPage * 10 - 10;
        int end = start + 9;

        if (end + 1 >= todoList.getItems().size()) {
            return;
        }

        todoList.setCurrentItem(end + 1);

        message.editMessage(todoList.getTodoListMessage()).queue();
        todoList.doCheckToggle(message);
    })),

    TODO_BUTTON_NAVIGATE_UP(new Button.UnicodeButton(UnicodeConstants.ARROW_UP, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if(!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        int newItem = todoList.getCurrentItem() - 1;

        if (newItem < 0) {
            return;
        }

        todoList.setCurrentItem(newItem);

        message.editMessage(todoList.getTodoListMessage()).queue();
        todoList.doCheckToggle(message);
    })),

    TODO_BUTTON_NAVIGATE_DOWN(new Button.UnicodeButton(UnicodeConstants.ARROW_DOWN, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if(!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        int newItem = todoList.getCurrentItem() + 1;

        if (newItem >= todoList.getItems().size()) {
            return;
        }

        todoList.setCurrentItem(newItem);

        message.editMessage(todoList.getTodoListMessage()).queue();
        todoList.doCheckToggle(message);
    }));

    @Getter
    @Transient
    private Button button;

    PersistentButton(Button button) {
        this.button = button;
    }
    
}
