package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TodoCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUIMessaging().replyUsage();
    }

    @Override
    public String command() {
        return "todo";
    }

    @Override
    public Module getModule() {
        return Module.USEFUL;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("todo", true);
    }

}
