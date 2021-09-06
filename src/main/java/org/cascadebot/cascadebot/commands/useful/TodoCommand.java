package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class TodoCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUiMessaging().replyUsage();
    }

    @Override
    public String command() {
        return "todo";
    }

    @Override
    public Module module() {
        return Module.USEFUL;
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("todo", true);
    }

    @Override
    public Set<DeprecatedSubCommand> subCommands() {
        return Set.of(new TodoCreateSubCommand(), new TodoAddSubCommand(), new TodoRemoveSubCommand(), new TodoAddUserSubCommand(), new TodoRemoveUserSubCommand(), new TodoViewSubCommand(), new TodoSendSubCommand());
    }
}
