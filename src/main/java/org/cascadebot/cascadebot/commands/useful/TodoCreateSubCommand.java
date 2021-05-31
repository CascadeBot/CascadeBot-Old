package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.TodoList;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TodoCreateSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUiMessaging().replyUsage();
            return;
        }

        // Warn if the original argument contains uppercase letters
        boolean warnUppercase = !context.getArg(0).equals(context.getArg(0).toLowerCase());
        String todoName = context.getArg(0).toLowerCase();
        context.getData().write(guildData -> {
            TodoList todoList = guildData.getUseful().createTodoList(todoName, context.getMember().getIdLong());

            if (todoList == null) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.todo.create.list_exists"));
                return;
            }

            String message = context.i18n("commands.todo.create.created", todoName);

            if (warnUppercase) {
                message += "\n\n" + context.i18n("commands.todo.create.warn_uppercase", todoName);
            }

            context.getTypedMessaging().replySuccess(message);
        });
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
    public CascadePermission permission() {
        return CascadePermission.of("todo.create", true);
    }

}
