package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandCore;
import com.cascadebot.cascadebot.data.Config;
import net.dv8tion.jda.core.entities.Member;

public class PrefixCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length > 0) {
            String newPrefix = context.getArg(0);
            if (newPrefix.equals("reset")) {
                context.getData().setCommandPrefix(Config.INS.getDefaultPrefix());
                context.replyInfo("The prefix has been reset to: `%s`", Config.INS.getDefaultPrefix());
                return;
            } else if (newPrefix.length() > 5) {
                context.replyDanger("The requested prefix is too long!");
                return;
            }
            context.getData().setCommandPrefix(newPrefix);
            context.replyInfo("The new prefix is: `%s`", newPrefix);
        } else {
            context.replyInfo("The current server prefix is `%s`", context.getData().getCommandPrefix());
        }
    }

    @Override
    public String command() {
        return "prefix";
    }

}
