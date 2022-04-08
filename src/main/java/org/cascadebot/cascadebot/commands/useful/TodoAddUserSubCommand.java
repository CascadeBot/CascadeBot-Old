package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildTodolistEntity;
import org.cascadebot.cascadebot.data.entities.GuildTodolistId;
import org.cascadebot.cascadebot.data.entities.GuildTodolistMemberEntity;
import org.cascadebot.cascadebot.data.objects.TodoList;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class TodoAddUserSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        String todoName = context.getArg(0).toLowerCase();
        GuildTodolistEntity todoList = context.transaction(session -> {
            return session.get(GuildTodolistEntity.class, new GuildTodolistId(todoName, context.getGuildId()));
        });

        if (todoList == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.list_does_not_exist", todoName));
            return;
        }

        if (todoList.getOwnerId() == null || todoList.getOwnerId() != context.getMember().getIdLong()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.owner_only"));
            return;
        }

        //TODO allow multiple users to be added/removed at once
        Member target = DiscordUtils.getMember(context.getGuild(), context.getArg(1));

        if (target == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.user_not_found", context.getArg(1)));
            return;
        }

        todoList.getMembers().add(new GuildTodolistMemberEntity(todoList.getName(), context.getGuildId(), target.getIdLong()));

        context.transactionNoReturn(session -> {
            session.save(todoList);
        });

        context.getTypedMessaging().replySuccess(context.i18n("commands.todo.adduser.added", target.getUser().getAsTag(), todoName));

    }

    @Override
    public String command() {
        return "adduser";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("todo.add.user", true);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
