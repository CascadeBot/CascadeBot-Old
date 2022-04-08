package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildTodolistEntity;
import org.cascadebot.cascadebot.data.entities.GuildTodolistId;
import org.cascadebot.cascadebot.data.entities.GuildTodolistItemEntity;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TodoAddSubCommand extends SubCommand {

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

        if (todoList.getMembers().stream().noneMatch(todoListMember -> todoListMember.getMemberId() == context.getMember().getIdLong()) && (todoList.getOwnerId() == null || context.getMember().getIdLong() != todoList.getOwnerId())) {
            Member owner = context.getGuild().getMemberById(todoList.getOwnerId());
            if (owner != null) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.todo.cannot_edit", owner.getAsMention()));
            } else {
                context.getTypedMessaging().replyDanger(context.i18n("commands.todo.cannot_edit_no_owner"));
                context.transactionNoReturn(session -> {
                    session.delete(todoList);
                });
            }
            return;
        }
        GuildTodolistItemEntity item = new GuildTodolistItemEntity(todoList.getName(), todoList.getGuildId(), context.getMessage(1));
        todoList.getTodolistItems().add(item);
        int index = todoList.getTodolistItems().indexOf(item) + 1;
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(context.i18n("commands.todo.add.embed_title"));
        builder.addField(context.i18n("commands.todo.embed_position_field"), String.valueOf(index), true);
        builder.addField(context.i18n("commands.todo.embed_item_field"), context.getMessage(1), true);
        context.getTypedMessaging().replySuccess(builder);
        // TODO figure out how we're going to cache sent items
        // todoList.edit(context);
    }

    @Override
    public String command() {
        return "add";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("todo.add", false);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
