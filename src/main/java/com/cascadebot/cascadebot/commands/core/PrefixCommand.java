package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandCore;
import net.dv8tion.jda.core.entities.Member;

public class PrefixCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String newPrefix = "";

        if (context.getArgs().length > 1) {
            newPrefix = context.getArg(1);
            context.getData().setCommandPrefix(newPrefix);
        } else if (context.getArgs().length > 0) {
            if (context.getArgs().length > 1) {
                newPrefix = context.getArg(1);
                context.getData().setCommandPrefix(newPrefix);
            } else {
                context.reply(context.getData().getCommandPrefix());
            }
        } if (newPrefix.length() > 5) {
            context.reply("Your new prefix must be less than 4 characters!");
            return;
        }
    }

    @Override
    public String command() {
        return "prefix";
    }

}
