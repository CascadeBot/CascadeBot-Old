package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import net.dv8tion.jda.core.entities.Member;

public class TestCommand implements ICommandRestricted {
    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getMessageContext().sendAutoDeleteMessage("Test 1");
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
