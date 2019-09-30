package org.cascadebot.cascadebot.commands.useful;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TodoAddSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        
    }

    @Override
    public String command() {
        return "add";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("todo.add", false);
    }

    @Override
    public String parent() {
        return "todo";
    }

}
