/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.management;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.data.objects.GuildSettings;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.Table;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;

public class SettingsCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Field field = Arrays.stream(GuildSettings.class.getDeclaredFields())
                .filter(f -> f.getName().equalsIgnoreCase(context.getArg(0)))
                .findFirst()
                .orElse(null);

        if (field != null) {
            field.setAccessible(true);
            try {
                if (field.getType() == boolean.class) {
                    field.setBoolean(context.getSettings(), Boolean.valueOf(context.getArg(1)));
                } else if (field.getType() == String.class) {
                    field.set(context.getSettings(), context.getArg(1));
                } else {
                    return;
                }
                context.replySuccess("Setting `%s` has been set to a value of `%s`", field.getName(), context.getArg(1));
            } catch (IllegalAccessException e) {
                context.replyException("Could not access that setting!", e);
            }
        } else if (context.getArg(0).equalsIgnoreCase("list")) {
            Table.TableBuilder tableBuilder = new Table.TableBuilder("Setting", "Current value");
            Arrays.stream(GuildSettings.class.getDeclaredFields())
                    .sorted(Comparator.comparing(Field::getName))
                    .forEach(f -> {
                        try {
                            f.setAccessible(true);
                            tableBuilder.addRow(f.getName(), String.valueOf(f.get(context.getSettings())));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
            context.reply(tableBuilder.build().toString());
        } else {
            context.replyDanger("Cannot find that field!");
        }
    }

    @Override
    public String command() {
        return "settings";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Settings command", "settings", false, Permission.MANAGE_SERVER);
    }

    @Override
    public Module getModule() {
        return Module.MANAGEMENT;
    }

}
