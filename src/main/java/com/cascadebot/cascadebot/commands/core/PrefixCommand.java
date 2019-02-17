package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandCore;
import net.dv8tion.jda.core.entities.Member;

public class PrefixCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String newPrefix = "";

        if (newPrefix.length() > 5) {
            context.replyDanger("Your new prefix must be less than 4 characters!");
            return;
        } else if (context.getArgs().length > 0) {
            newPrefix = context.getArg(0);
        } else if (context.getArg(0) == null) {
            context.replyDanger("Please provide an argument!");
            return;
        }
        context.reply(newPrefix);
    }

    @Override
    public String command() {
        return "prefix";
    }

}
