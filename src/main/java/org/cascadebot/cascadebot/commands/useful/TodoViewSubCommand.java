package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.TodoList;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;

import java.util.ArrayList;
import java.util.List;

public class TodoViewSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage();
            return;
        }

        TodoList todoList = context.getData().getGuildSettingsUseful().getTodoList(context.getArg(0));

        if (todoList == null) {
            context.getTypedMessaging().replyDanger("Todo list " + context.getArg(0) + " doesn't exist");
            return;
        }

        if (!todoList.canUserEdit(context.getMember().getIdLong())) {
            Member owner = context.getGuild().getMemberById(todoList.getOwnerId());
            if (owner != null) {
                context.getTypedMessaging().replyDanger("You cannot edit this todo list. If you want to edit this contact " + owner.getAsMention());
            } else {
                context.getTypedMessaging().replyDanger("You cannot edit this todo list and the owner has left the guild so the todo list has been deleted");
                context.getData().getGuildSettingsUseful().deleteTodoList(context.getArg(0));
            }
            return;
        }

        List<Page> pages = new ArrayList<>();
        StringBuilder currentPage = new StringBuilder();

        for (int i = 0; i < todoList.getItems().size(); i++) {
            TodoList.TodoListItem item = todoList.getItems().get(i);

            if (i % 20 == 0 && i != 0) {
                pages.add(new PageObjects.StringPage(currentPage.toString()));
                currentPage = new StringBuilder();
            }

            currentPage.append(i + 1).append(": ").append(item.getText()).append("\n");

        }

        if (!currentPage.toString().isEmpty()) {
            pages.add(new PageObjects.StringPage(currentPage.toString()));
        }

        context.getUIMessaging().sendPagedMessage(pages);
    }

    @Override
    public String command() {
        return "view";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("todo.view", true);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
