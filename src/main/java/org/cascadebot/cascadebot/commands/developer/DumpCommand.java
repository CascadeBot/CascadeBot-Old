/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ExecutableCommand;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.RestrictedCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.PasteUtils;
import org.cascadebot.cascadebot.utils.Table;
import org.cascadebot.shared.SecurityLevel;

import java.util.Comparator;
import java.util.stream.Collectors;

public class DumpCommand extends RestrictedCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getTypedMessaging().replyDanger("Hmmm either pick: `threads`, `commands`, `permissions`, `args` or `guild`");
            return;
        }
        if (context.getArg(0).equalsIgnoreCase("threads")) {
            String threads = Thread.getAllStackTraces().keySet().stream().map(Thread::getName).sorted().collect(Collectors.joining("\n"));
            context.getTypedMessaging().replyInfo("**Threads**\n" + PasteUtils.paste(threads));
        } else if (context.getArg(0).equalsIgnoreCase("commands")) {
            Table.TableBuilder builder = new Table.TableBuilder("Command", "Module", "Permission", "Subcommands");
            for (MainCommand command : CascadeBot.INS.getCommandManager().getCommands()) {
                builder.addRow(
                        command.command(),
                        command.module().toString(),
                        command.permission() == null ? "No permission" : command.permission().toString(),
                        command.subCommands().stream().map(ExecutableCommand::command).collect(Collectors.toSet()).toString()
                );
            }
            context.getTypedMessaging().replyInfo("**Commands**\n" + PasteUtils.paste(builder.build().toString()));
        } else if (context.getArg(0).equalsIgnoreCase("permissions")) {
            Table.TableBuilder builder = new Table.TableBuilder("Permission", "Discord permissions", "Default permission");
            context.getData().getPermissionsManager().getPermissions().stream().sorted(Comparator.comparing(CascadePermission::getPermissionRaw)).forEach(permission -> {
                builder.addRow(permission.getPermissionRaw(), permission.getDiscordPerms().toString(), String.valueOf(permission.isDefaultPerm()));
            });
            context.getTypedMessaging().replyInfo("**Permissions**\n" + PasteUtils.paste(builder.build().toString()));
        } else if (context.getArg(0).equalsIgnoreCase("guild")) {
            context.getTypedMessaging().replyInfo("**Guild**\n" + PasteUtils.paste(new GsonBuilder().setPrettyPrinting().create().toJson(context.getData())));
        } else if (context.getArg(0).equalsIgnoreCase("args")) {
            StringBuilder builder = new StringBuilder();
            for (MainCommand command : CascadeBot.INS.getCommandManager().getCommands()) {
                builder.append("\n").append(context.getUsage(command)).append("\n");
            }
            context.getTypedMessaging().replyInfo("**Arguments**\n" + PasteUtils.paste(builder.toString()));
        } else {
            context.getTypedMessaging().replyDanger("I can't find that argument!");
        }
    }

    @Override
    public String command() {
        return "dump";
    }

    @Override
    public String description() {
        return "Dumps various pieces of guild data.";
    }

    @Override
    public Module module() {
        return Module.DEVELOPER;
    }

    @Override
    public SecurityLevel commandLevel() {
        return SecurityLevel.DEVELOPER;
    }

}
