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
import com.cascadebot.cascadebot.utils.Table;
import com.cascadebot.cascadebot.utils.pagination.PageObjects;
import com.cascadebot.shared.SecurityLevel;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DumpCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.replyDanger("Hmmm either pick: `threads`, `commands`, `permissions` or `guild`");
            return;
        }
        if (context.getArg(0).equalsIgnoreCase("threads")) {
            String threads = "```\n" + Thread.getAllStackTraces().keySet().stream().map(Thread::getName).sorted().collect(Collectors.joining("\n")) + "```";
            PasteUtils.pasteIfLong(threads, 2048, context::reply);
        } else if (context.getArg(0).equalsIgnoreCase("commands")) {
            Table.TableBuilder builder = new Table.TableBuilder("Command", "Module", "Permission", "Subcommands");
            for (ICommandMain command : CascadeBot.INS.getCommandManager().getCommands()) {
                builder.addRow(
                    command.command(),
                    command.getModule().toString(),
                    command.getPermission() == null ? "No permission" : command.getPermission().toString(),
                    command.getSubCommands().stream().map(ICommandExecutable::command).collect(Collectors.toSet()).toString()
                );
            }
            PasteUtils.pasteIfLong(builder.build().toString(), 2048, context::reply);
        } else if (context.getArg(0).equalsIgnoreCase("permissions")) {
            Table.TableBuilder builder = new Table.TableBuilder("Permission", "Discord Perms");
            for (CascadePermission permission : CascadeBot.INS.getPermissionsManager().getPermissions()) {
                builder.addRow(permission.getPermissionNode(), permission.getDiscordPerm().toString());
            }
            PasteUtils.pasteIfLong(builder.build().toString(), 2048, context::reply);
        } else if (context.getArg(0).equalsIgnoreCase("guild")) {
            PasteUtils.pasteIfLong("```json\n" + new GsonBuilder().setPrettyPrinting().create().toJson(context.getData()) + "```", 2048, context::reply);
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
