package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildTodolistEntity;
import org.cascadebot.cascadebot.data.entities.GuildTodolistId;
import org.cascadebot.cascadebot.data.entities.GuildTodolistItemEntity;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;

import java.util.ArrayList;
import java.util.List;

public class TodoViewSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
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

        List<Page> pages = new ArrayList<>();
        StringBuilder currentPage = new StringBuilder();

        if (todoList.getItems().size() == 0) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.no_items"));
            return;
        }

        for (int i = 0; i < todoList.getItems().size(); i++) {
            GuildTodolistItemEntity item = todoList.getItems().get(i);

            if (i % 20 == 0 && i != 0) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Todo list items");
                builder.appendDescription(currentPage.toString());
                pages.add(new PageObjects.EmbedPage(builder));
                currentPage = new StringBuilder();
            }

            currentPage.append(i + 1).append(": ").append(item.getText()).append("\n");

        }

        if (!currentPage.toString().isEmpty()) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Todo list items");
            builder.appendDescription(currentPage.toString());
            pages.add(new PageObjects.EmbedPage(builder));
        }

        context.getUiMessaging().sendPagedMessage(pages);
    }

    @Override
    public String command() {
        return "view";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("todo.view", true);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
