package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandCore;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.subcommands.module.ModuleDisableSubCommand;
import com.cascadebot.cascadebot.subcommands.module.ModuleEnableSubCommand;
import com.cascadebot.cascadebot.subcommands.module.ModuleListSubCommand;
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
        return Set.of(new ModuleDisableSubCommand(), new ModuleEnableSubCommand(), new ModuleListSubCommand());
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("core.module");
    }

}
