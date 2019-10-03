package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.TodoList;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.util.HashMap;
import java.util.Map;

public class TodoSendSubCommand implements ISubCommand {

    private Map<Long, ButtonGroup> buttonGroupMap = new HashMap<>();

    private Button check_button = new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel, message) -> {
        GuildData data = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
        TodoList list = data.getGuildSettingsUseful().getTodoListByMessage(message.getIdLong());
        if (!list.canUserEdit(runner.getIdLong())) {
            return;
        }

        TodoList.TodoListItem item = list.getItems().get(list.getCurrentItem());
        item.setDone(true);
        addCheckButton(message);
        message.editMessage(getTodoListMessage(list)).queue();
    });

    private Button uncheck_button = new Button.UnicodeButton(UnicodeConstants.WHITE_HALLOW_SQUARE, (runner, channel, message) -> {
        GuildData data = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
        TodoList list = data.getGuildSettingsUseful().getTodoListByMessage(message.getIdLong());
        if (!list.canUserEdit(runner.getIdLong())) {
            return;
        }
        TodoList.TodoListItem item = list.getItems().get(list.getCurrentItem());
        item.setDone(false);
        addUncheckButton(message);
        message.editMessage(getTodoListMessage(list)).queue();
    });

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage();
            return;
        }

        TextChannel channel = context.getChannel();
        if (context.getArgs().length > 1) {
            channel = DiscordUtils.getTextChannel(context.getGuild(), context.getMessage(1));
            if (channel == null) {
                context.getTypedMessaging().replyDanger("Couldn't find channel `" + context.getMessage(1) + "`");
                return;
            }
        }

        //TODO make sure channel is in guild and that this user can see said channel. probably should do this in the utils

        TodoList todoList = context.getData().getGuildSettingsUseful().getTodoList(context.getArg(0));

        if (todoList == null) {
            context.getTypedMessaging().replyDanger("Todo list " + context.getArg(0) + " doesn't exist");
            return;
        }

        if (!todoList.canUserEdit(context.getMember().getIdLong())) {
            Member owner = context.getGuild().getMemberById(todoList.getOwnerId());
            if (owner != null) {
                context.getTypedMessaging().replyDanger("You cannot edit this todo list. If you want to edit this contact " + owner.getAsMention());
            } else {
                context.getTypedMessaging().replyDanger("You cannot edit this todo list and the owner has left the guild so the todo list as been deleted");
                context.getData().getGuildSettingsUseful().deleteTodoList(context.getArg(0));
            }
            return;
        }

        ButtonGroup buttonGroup = new ButtonGroup(context.getMember().getIdLong(), channel.getIdLong(), channel.getGuild().getIdLong());
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.BACKWARD_ARROW, (runner, channel1, message) -> {
            int currentPage = todoList.getCurrentItem()/10 + 1;
            int start = currentPage * 10 - 10;

            if (start == 0) {
                return;
            }

            int newPos = Math.max(start - 10, 0);

            todoList.setCurrentItem(newPos);

            message.editMessage(getTodoListMessage(todoList)).queue();
            //TODO implement check for done items when switching items
        }));
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_UP, (runner, channel1, message) -> {
            int newItem = todoList.getCurrentItem() - 1;

            if (newItem < 0) {
                return;
            }

            todoList.setCurrentItem(newItem);

            message.editMessage(getTodoListMessage(todoList)).queue();
            //TODO implement check for done items when switching items
        }));
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_DOWN, (runner, channel1, message) -> {
            int newItem = todoList.getCurrentItem() + 1;

            if (newItem >= todoList.getItems().size()) {
                return;
            }

            todoList.setCurrentItem(newItem);

            message.editMessage(getTodoListMessage(todoList)).queue();
            //TODO implement check for done items when switching items
        }));
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.FORWARD_ARROW, (runner, channel1, message) -> {
            int currentPage = todoList.getCurrentItem()/10 + 1;
            int start = currentPage * 10 - 10;
            int end = start + 9;

            if (end + 1 >= todoList.getItems().size()) {
                return;
            }

            todoList.setCurrentItem(end + 1);

            message.editMessage(getTodoListMessage(todoList)).queue();
            //TODO implement check for done items when switching items
        }));

        buttonGroup.addButton(check_button);

        todoList.setCurrentItem(0);
        Messaging.sendButtonedMessage(channel, getTodoListMessage(todoList), buttonGroup).thenAccept(message -> {
            buttonGroupMap.put(message.getIdLong(), buttonGroup);
            todoList.setMessage(message.getIdLong());
        });
    }

    private void addCheckButton(Message message) {
        ButtonGroup buttonGroup = buttonGroupMap.get(message.getIdLong());
        buttonGroup.addButton(uncheck_button);
        buttonGroup.removeButton(check_button);
    }

    private void addUncheckButton(Message message) {
        ButtonGroup buttonGroup = buttonGroupMap.get(message.getIdLong());
        buttonGroup.removeButton(uncheck_button);
        buttonGroup.addButton(check_button);
    }

    public String getTodoListMessage(TodoList list) {
        int pos = list.getCurrentItem();
        int currentPage = pos/10 + 1;
        int start = currentPage * 10 - 10;
        int end = start + 9;

        StringBuilder pageBuilder = new StringBuilder();

        for (int i = start; i <= end; i++) {
            if( i >= list.getItems().size()) {
                break;
            }
            TodoList.TodoListItem item = list.getItems().get(i);
            if (i == pos) {
                pageBuilder.append(UnicodeConstants.WHITE_CIRCLE).append(" ");
            }
            pageBuilder.append(i+1).append(": ");
            if (item.isDone()) {
                pageBuilder.append("~~");
            }
            pageBuilder.append(item.getText()).append('\n');
            if (item.isDone()) {
                pageBuilder.append("~~");
            }
        }
        return pageBuilder.toString();
    }

    @Override
    public String command() {
        return "send";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("todo.send", true);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
