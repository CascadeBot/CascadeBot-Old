/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.objects.pagination.Page;
import com.cascadebot.cascadebot.objects.pagination.PageObjects;
import com.cascadebot.cascadebot.tasks.Task;
import com.cascadebot.cascadebot.utils.buttons.Button;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.cascadebot.cascadebot.utils.buttons.ButtonRunnable;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCommand implements ICommandRestricted {
    @Override
    public void onCommand(Member sender, CommandContext context) {
        List<Page> pageList = new ArrayList<>();
        pageList.add(new PageObjects.EmbedPage(new EmbedBuilder().setTitle("Test").appendDescription("I'm testing pages woo!")));
        pageList.add(new PageObjects.StringPage("This is a string page"));

        List<String> header = Arrays.asList("1", "2", "3", "4");
        List<List<String>> body = new ArrayList<>();
        List<String> row = new ArrayList<>();
        for(int i = 1; i <= 60; i++) {
            row.add(String.valueOf(i));
            if(i % 4 == 0) {
                body.add(row);
                row = new ArrayList<>();
            }
        }

        pageList.add(new PageObjects.TablePage(header, body));

        context.sendPagedMessage(pageList);
    }

    @Override
    public String defaultCommand() {
        return "test";
    }

    @Override
    public CommandType getType() {
        return CommandType.DEVELOPER;
    }

    @Override
    public boolean forceDefault() {
        return true;
    }
}
