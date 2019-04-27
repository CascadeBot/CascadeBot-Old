/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import com.google.gson.GsonBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.PasteUtils;
import org.cascadebot.cascadebot.utils.Table;
import org.cascadebot.shared.SecurityLevel;

import java.util.stream.Collectors;

public class DumpCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.developer.dump.not_enough_arguments"));
            return;
        }
        if (context.getArg(0).equalsIgnoreCase("threads")) {
            String threads = "```\n" + Thread.getAllStackTraces().keySet().stream().map(Thread::getName).sorted().collect(Collectors.joining("\n")) + "```";
            PasteUtils.pasteIfLong(threads, 2048, context::reply);
        } else if (context.getArg(0).equalsIgnoreCase("commands")) {
            Table.TableBuilder builder = new Table.TableBuilder(context.i18n("words.command"), context.i18n("words.module"), context.i18n("words.permission"), context.i18n("words.subcommand"));
            for (ICommandMain command : CascadeBot.INS.getCommandManager().getCommands()) {
                builder.addRow(
                    command.command(),
                    command.getModule().toString(),
                    command.getPermission() == null ? context.i18n("words.no_permission") : command.getPermission().toString(),
                    command.getSubCommands().stream().map(ICommandExecutable::command).collect(Collectors.toSet()).toString()
                );
            }
            PasteUtils.pasteIfLong(builder.build().toString(), 2048, context::reply);
        } else if (context.getArg(0).equalsIgnoreCase("permissions")) {
            Table.TableBuilder builder = new Table.TableBuilder(context.i18n("words.permission"), context.i18n("words.discord_perms"), context.i18n("words.default_permission"));
            for (CascadePermission permission : CascadeBot.INS.getPermissionsManager().getPermissions()) {
                builder.addRow(permission.getPermissionNode(), permission.getDiscordPerms().toString(), String.valueOf(permission.isDefaultPerm()));
            }
            PasteUtils.pasteIfLong(builder.build().toString(), 2048, context::reply);
        } else if (context.getArg(0).equalsIgnoreCase("guild")) {
            PasteUtils.pasteIfLong("```json\n" + new GsonBuilder().setPrettyPrinting().create().toJson(context.getData()) + "```", 2048, context::reply);
        } else {
            context.getTypedMessaging().replyDanger(context.i18n("responses.cannot_find_argument"));
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
