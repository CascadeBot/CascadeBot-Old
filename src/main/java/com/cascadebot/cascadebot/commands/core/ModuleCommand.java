package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandCore;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;

public class ModuleCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.replyInfo("Use subcommands disable, enable and list!");
    }

    @Override
    public String command() {
        return "module";
    }

    @Override
    public Set<ICommandExecutable> getSubCommands() {
        return Set.of(new ModuleDisableSubcommand(), new ModuleEnableSubcommand(), new ModuleListSubcommand());
    }

}
