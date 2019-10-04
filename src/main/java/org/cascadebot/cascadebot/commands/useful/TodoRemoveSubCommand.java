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
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.remove.not_number"));
            return;
        }

        TodoList todoList = context.getData().getGuildSettingsUseful().getTodoList(context.getArg(0));

        if (todoList == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.list_does_not_exist", context.getArg(0)));
            return;
        }

        if (todoList.getMessage() == -1) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.list_already_sent"));
            return;
        }

        if (!todoList.canUserEdit(context.getMember().getIdLong())) {
            Member owner = context.getGuild().getMemberById(todoList.getOwnerId());
            if (owner != null) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.todo.cannot_edit", owner.getAsMention()));
            } else {
                context.getTypedMessaging().replyDanger(context.i18n("commands.todo.cannot_edit_no_owner"));
                context.getData().getGuildSettingsUseful().deleteTodoList(context.getArg(0));
            }
            return;
        }

        int index = context.getArgAsInteger(1) - 1;

        if (index < 0 || index > todoList.getItems().size()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.remove.item_does_not_exist"));
            return;
        }

        TodoList.TodoListItem item = todoList.removeTodoItem(index);
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(context.i18n("commands.todo.remove.embed_title"));
        builder.addField(context.i18n("commands.todo.embed_list_field"), context.getArg(0), false);
        builder.addField(context.i18n("commands.todo.embed_item_field"), item.getText(), false);
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
