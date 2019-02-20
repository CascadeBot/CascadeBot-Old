/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.subcommands.guild;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.FormatUtils;
import net.dv8tion.jda.core.entities.Member;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuildSettingsSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Field field = Arrays.stream(GuildData.class.getDeclaredFields())
                .filter(f -> f.getName().equalsIgnoreCase(context.getArg(0)))
                .filter(f -> f.getType() == boolean.class)
                .findFirst()
                .orElse(null);

        if (field != null) {
            field.setAccessible(true);
            try {
                field.setBoolean(context.getData(), Boolean.valueOf(context.getArg(1)));
                context.replySuccess("Set field `%s` to a value of `%s`", field.getName(), context.getArg(1));
            } catch (IllegalAccessException e) {
                context.replyException("Could not access that setting!", e);
            }
        } else if (context.getArg(0).equalsIgnoreCase("list")) {
            List<String> headings = List.of("Field name", "Current value");
            List<List<String>> body = new ArrayList<>();
            Arrays.stream(GuildData.class.getDeclaredFields())
                    .filter(f -> f.getType() == boolean.class)
                    .forEach(f -> {
                        try {
                            f.setAccessible(true);
                            body.add(List.of(f.getName(), String.valueOf(f.getBoolean(context.getData()))));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
            context.reply(FormatUtils.makeAsciiTable(headings, body, null));
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
        return null;
    }

}
