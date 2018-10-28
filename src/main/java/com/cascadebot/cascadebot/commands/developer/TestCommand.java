/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.tasks.Task;
import com.cascadebot.cascadebot.utils.buttons.Button;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.cascadebot.cascadebot.utils.buttons.ButtonRunnable;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class TestCommand implements ICommandRestricted {
    @Override
    public void onCommand(Member sender, CommandContext context) {
        ButtonGroup group = new ButtonGroup(sender.getUser().getIdLong(), context.getGuild().getOwnerIdLong());
        group.addButton(new Button.UnicodeButton("\uD83D\uDC40", (runner, channel, message) -> {
            channel.sendMessage("\uD83D\uDC40").queue();
        }));
        group.addButton(new Button.UnicodeButton("\uD83D\uDC4D", (runner, channel, message) -> {
            channel.sendMessage("\uD83D\uDC4D").queue();
        }));
        group.addButton(new Button.EmoteButton(context.getGuild().getEmoteById(502576800086622208L), (runner, channel, message) -> {
            channel.sendMessage("<:cascade:502576800086622208>").queue();
        }));
        context.sendButtonedMessage("test", group);
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
