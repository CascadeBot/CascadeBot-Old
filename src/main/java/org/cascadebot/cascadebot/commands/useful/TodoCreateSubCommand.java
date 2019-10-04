package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.TodoList;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TodoCreateSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUIMessaging().replyUsage();
            return;
        }

        TodoList todoList = context.getData().getGuildSettingsUseful().createTodoList(context.getArg(0), context.getMember().getIdLong());

        if (todoList == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.create.list_exists"));
            return;
        }

        context.getTypedMessaging().replySuccess(context.i18n("commands.todo.create.created", context.getArg(0)));
    }

    @Override
    public String command() {
        return "create";
    }

    @Override
    public String parent() {
        return "todo";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("todo.create", true);
    }

}
