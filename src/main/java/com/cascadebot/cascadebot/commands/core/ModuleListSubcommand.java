package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModuleListSubcommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply("**Modules**\n" + Arrays.stream(Module.values())
                .map(module -> module.toString().toLowerCase() +
                        " - " +
                        (context.getData().isModuleEnabled(module) ? "Enabled" : "Disabled"))
                .collect(Collectors.joining("\n")));
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

}
