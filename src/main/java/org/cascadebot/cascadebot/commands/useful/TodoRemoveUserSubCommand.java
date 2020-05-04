package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.guild.TodoList;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class TodoRemoveUserSubCommand implements ISubCommand {

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

        if (todoList.getOwnerId() != context.getMember().getIdLong()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.owner_only"));
            return;
        }

        //TODO allow multiple users to be added/removed at once
        Member target = DiscordUtils.getMember(context.getGuild(), context.getArg(1));

        if (target == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.user_not_found", context.getArg(1)));
            return;
        }

        todoList.removeEditUser(target);

        context.getTypedMessaging().replySuccess(context.i18n("commands.todo.removeuser.removed", target.getUser().getAsTag(), todoName));

    }

    @Override
    public String command() {
        return "removeuser";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("todo.remove.user", true);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
