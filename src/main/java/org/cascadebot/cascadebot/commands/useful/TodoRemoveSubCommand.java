package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildTodolistEntity;
import org.cascadebot.cascadebot.data.entities.GuildTodolistId;
import org.cascadebot.cascadebot.data.entities.GuildTodolistItemEntity;
import org.cascadebot.cascadebot.messaging.MessagingObjects;

public class TodoRemoveSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        if (!context.isArgInteger(1)) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.remove.not_number"));
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

        int index = context.getArgAsInteger(1) - 1;

        if (index < 0 || index > todoList.getItems().size()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.remove.item_does_not_exist"));
            return;
        }

        GuildTodolistItemEntity item = todoList.getItems().remove(index);
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(context.i18n("commands.todo.remove.embed_title"));
        builder.addField(context.i18n("commands.todo.embed_list_field"), todoName, false);
        builder.addField(context.i18n("commands.todo.embed_item_field"), item.getText(), false);
        context.getTypedMessaging().replySuccess(builder);

        if (todoList.getItems().size() == 0) {
            context.transactionNoReturn(session -> {
                session.delete(todoList);
            });
            context.reply(context.i18n("commands.todo.remove.deleted"));
        }

        // TODO figure out how we're going to cache sent items
        // todoList.edit(context);
    }

    @Override
    public String command() {
        return "remove";
    }

    @Override
    public String parent() {
        return "todo";
    }

}
