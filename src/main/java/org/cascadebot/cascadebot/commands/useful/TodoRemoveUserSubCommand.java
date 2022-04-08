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

import java.util.Optional;

public class TodoRemoveUserSubCommand extends SubCommand {

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

        Optional<GuildTodolistMemberEntity> memberEntity = todoList.getMembers().stream().filter(todoListMember -> todoListMember.getMemberId() == target.getIdLong()).findFirst();
        if (memberEntity.isEmpty()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.user_not_found", context.getArg(1))); // TODO different message
            return;
        }
        todoList.getMembers().remove(memberEntity.get());

        context.transactionNoReturn(session -> {
            session.save(todoList);
        });

        context.getTypedMessaging().replySuccess(context.i18n("commands.todo.removeuser.removed", target.getUser().getAsTag(), todoName));
    }

    @Override
    public String command() {
        return "removeuser";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("todo.remove.user", true);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
