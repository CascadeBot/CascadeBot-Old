package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.TodoList;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TodoRemoveSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage();
            return;
        }

        if (!context.isArgInteger(1)) {
            context.getTypedMessaging().replyDanger("You can only remove an item by number!");
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
                context.getTypedMessaging().replyDanger("You cannot edit this todo list and the owner has left the guild so the todo list has been deleted");
                context.getData().getGuildSettingsUseful().deleteTodoList(context.getArg(0));
            }
            return;
        }

        int index = context.getArgAsInteger(1) - 1;

        if (index < 0 || index > todoList.getItems().size()) {
            context.getTypedMessaging().replyDanger("Cannot remove an item that doesn't exist!");
            return;
        }

        TodoList.TodoListItem item = todoList.removeTodoItem(index);
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle("Removed item to todo list");
        builder.addField("List", context.getArg(0), false);
        builder.addField("Item", item.getText(), false);
        context.getTypedMessaging().replySuccess(builder);
    }

    @Override
    public String command() {
        return "remove";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("todo.remove", false);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
