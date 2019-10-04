package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.TodoList;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TodoAddSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage();
            return;
        }

        TodoList todoList = context.getData().getGuildSettingsUseful().getTodoList(context.getArg(0));

        if (todoList == null) {
            context.getTypedMessaging().replyDanger("Todo list " + context.getArg(0) + " doesn't exist");
            return;
        }

        if (todoList.getMessage() == -1) {
            context.getTypedMessaging().replyDanger("This todo list has already been sent, and therefor is no longer editable");
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

        int index = todoList.addTodoItem(context.getMessage(1)) + 1;
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle("Added item to todo list");
        builder.addField("List", context.getArg(0), true);
        builder.addField("Position", String.valueOf(index), true);
        builder.addField("Item", context.getMessage(1), false);
        context.getTypedMessaging().replySuccess(builder);
    }

    @Override
    public String command() {
        return "add";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("todo.add", false);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
