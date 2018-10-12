package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.tasks.Task;
import net.dv8tion.jda.core.entities.Member;

public class TestCommand implements ICommandRestricted {
    @Override
    public void onCommand(Member sender, CommandContext context) {

        new Task("Hello", 2000, 5000) {

            private int i = 0;

            @Override
            protected void execute() {
                if (++i == 5) {
                    cancel();
                }
                context.replyDM("hi");
            }
        }.start();
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
