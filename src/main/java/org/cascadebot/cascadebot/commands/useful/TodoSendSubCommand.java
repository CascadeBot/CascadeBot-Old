package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand;
import org.cascadebot.cascadebot.data.objects.TodoList;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class TodoSendSubCommand extends DeprecatedSubCommand {

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
        TodoList todoList = context.getData().getUseful().getTodoList(todoName);

        if (todoList == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.list_does_not_exist", todoName));
            return;
        }

        if (!todoList.canUserEdit(context.getMember().getIdLong())) {
            Member owner = context.getGuild().getMemberById(todoList.getOwnerId());
            if (owner != null) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.todo.cannot_edit", owner.getAsMention()));
            } else {
                context.getTypedMessaging().replyDanger(context.i18n("commands.todo.cannot_edit_no_owner"));
                context.getData().getUseful().deleteTodoList(todoName);
            }
            return;
        }

        if (todoList.getItems().size() == 0) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.todo.no_items"));
            return;
        }

        todoList.send(context, channel);
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
