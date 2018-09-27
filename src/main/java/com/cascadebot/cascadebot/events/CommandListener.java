package com.cascadebot.cascadebot.events;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.Config;
import com.cascadebot.cascadebot.commands.Command;
import com.cascadebot.cascadebot.commands.CommandContext;
import com.cascadebot.cascadebot.objects.GuildData;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.regex.Pattern;

public class CommandListener extends ListenerAdapter {

    private final Pattern multiSpace = Pattern.compile(" {2,}");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String message = multiSpace.matcher(event.getMessage().getContentRaw()).replaceAll(" ");
        String prefix = Config.VALUES.defaultPrefix; //TODO: Add guild data prefix here
        if (message.startsWith(prefix)) {
            String command = message.substring(prefix.length()); // Remove prefix from command
            command = command.split(" ")[0]; // Get first string before a space
            String[] args = ArrayUtils.remove(command.split(" "), 0); // Remove the command portion of the string

            CommandContext context = new CommandContext(event.getChannel(), new GuildData(event.getGuild().getIdLong()), args);

            Command c = CascadeBot.instance().getCommandManager().getCommand(command, event.getAuthor());
            c.onCommand(event.getMember(), context);
        }

    }

}
