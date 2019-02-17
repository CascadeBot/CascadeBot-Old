package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.entities.Member;

public class ModuleDisableSubcommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String selectedModule = context.getArg(0);

        if (selectedModule == "fun") {
            context.getData().disableModule(Module.FUN);
            context.replySuccess("We have disabled the `%s` module!", selectedModule);
        } else if (selectedModule == "informational") {
            context.getData().disableModule(Module.INFORMATIONAL);
            context.replySuccess("We have disabled the `%s` module!", selectedModule);
        } else {
            context.replyDanger("We couldn't find that module. Use `" + context.getData().getCommandPrefix() + "module list` for a list of modules.");
        }

    }

    @Override
    public String command() {
        return "disable";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

}
