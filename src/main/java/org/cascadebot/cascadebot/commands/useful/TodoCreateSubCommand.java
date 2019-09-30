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

        TodoList todoList = context.getData().getGuildSettingUseful().createTodoList(context.getArg(0));

        if (todoList == null) {
            context.getTypedMessaging().replyDanger("A todo list with that name already exists!");
            return;
        }

        context.getTypedMessaging().replySuccess("Created todo list with name `" + todoList.getName() + "`");
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
