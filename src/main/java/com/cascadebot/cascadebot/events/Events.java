package com.cascadebot.cascadebot.events;

import com.cascadebot.cascadebot.Config;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Events extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().contains("<@" + Config.VALUES.id + ">")) {
            event.getMessage().getChannel().sendMessage(event.getAuthor().getAsMention() + " hi").queue();
        }
    }
}
