package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.guild.TodoList;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TodoAddSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        String todoName = context.getArg(0).toLowerCase();
        TodoList todoList = context.getData().getUseful().getTodoList(todoName);

        if (todoList == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.list_does_not_exist", todoName));
            return;
        }

        if (!todoList.canUserEdit(context.getMember().getIdLong())) {
            Member owner = context.getGuild().getMemberById(todoList.getOwnerId());
            if (owner != null) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.todo.cannot_edit", owner.getAsMention()));
            } else {
                context.getTypedMessaging().replyDanger(context.i18n("commands.todo.cannot_edit_no_owner"));
                context.getData().getUseful().deleteTodoList(todoName);
            }
            return;
        }

        int index = todoList.addTodoItem(context.getMessage(1)) + 1;
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(context.i18n("commands.todo.add.embed_title"));
        builder.addField(context.i18n("commands.todo.embed_position_field"), String.valueOf(index), true);
        builder.addField(context.i18n("commands.todo.embed_item_field"), context.getMessage(1), true);
        context.getTypedMessaging().replySuccess(builder);
        todoList.edit(context);
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
