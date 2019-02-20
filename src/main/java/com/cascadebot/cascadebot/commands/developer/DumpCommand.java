/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.FormatUtils;
import com.cascadebot.cascadebot.utils.PasteUtils;
import com.cascadebot.cascadebot.utils.pagination.PageObjects;
import com.cascadebot.shared.SecurityLevel;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DumpCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.replyDanger("Hmmm either pick: `threads`, `commands` or `permissions`");
            return;
        }
        if (context.getArg(0).equalsIgnoreCase("threads")) {
            String threads = "```\n" + Thread.getAllStackTraces().keySet().stream().map(Thread::getName).sorted().collect(Collectors.joining("\n")) + "```";
            PasteUtils.pasteIfLong(threads, 2048, context::reply);
        } else if (context.getArg(0).equalsIgnoreCase("commands")) {
            List<String> header = List.of("Command", "Module", "Permission", "Subcommands");
            List<List<String>> body = new ArrayList<>();
            for (ICommandMain command : CascadeBot.INS.getCommandManager().getCommands()) {
                body.add(List.of(
                        command.command(),
                        command.getModule().toString(),
                        command.getPermission() == null ? "No permission" : command.getPermission().toString(),
                        command.getSubCommands().stream().map(ICommandExecutable::command).collect(Collectors.toSet()).toString()));
            }
            PasteUtils.pasteIfLong(FormatUtils.makeAsciiTable(header, body, null), 2048, context::reply);
        } else if (context.getArg(0).equalsIgnoreCase("permissions")) {
            List<String> header = List.of("Permission", "Discord Perms");
            List<List<String>> body = new ArrayList<>();
            for (CascadePermission permission : CascadeBot.INS.getPermissionsManager().getPermissions()) {
                body.add(List.of(permission.getPermissionNode(), permission.getDiscordPerm().toString()));
            }
            PasteUtils.pasteIfLong(FormatUtils.makeAsciiTable(header, body, null), 2048, context::reply);
        } else {
            context.replyDanger("I can't seem to find that argument \uD83E\uDD14" /* Thinking emoji 🤔 */);
        }
    }

    @Override
    public String command() {
        return "dump";
    }

    @Override
    public Module getModule() {
        return Module.DEVELOPER;
    }

    @Override
    public SecurityLevel getCommandLevel() {
        return SecurityLevel.DEVELOPER;
    }

}
