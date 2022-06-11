package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildTodolistEntity;
import org.cascadebot.cascadebot.data.entities.GuildTodolistId;
import org.cascadebot.cascadebot.data.entities.GuildTodolistItemEntity;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.move.MovableList;

import java.util.Objects;

public class TodoSendSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        TextChannel channel = context.getChannel();
        if (context.getArgs().length > 1) {
            channel = DiscordUtils.getTextChannel(context.getGuild(), context.getMessage(1));
            if (channel == null) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.todo.send.cannot_find_channel", context.getArg(1)));
                return;
            }
        }

        //TODO make sure channel is in guild and that this user can see said channel. probably should do this in the utils

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

        if (todoList.getItems().size() == 0) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.no_items"));
            return;
        }

        MovableList<GuildTodolistItemEntity> movableList = MovableList.wrap(todoList.getItems());
        movableList.setDisplayFunction(GuildTodolistItemEntity::getText);
        movableList.setUsageRestriction(member -> {
            return todoList.getMembers().stream().anyMatch(todoListMember -> todoListMember.getMemberId() == member) || Objects.equals(todoList.getOwnerId(), member);
        });
        movableList.setMovedConsumer(movedInfo -> {
            // TODO this. Currently items don't have a position on them, and I don't know how that works with the one to many relationship. I need to research this more
        });

        // TODO check and uncheck

        movableList.send(channel);
    }

    @Override
    public String command() {
        return "send";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("todo.send", true);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
