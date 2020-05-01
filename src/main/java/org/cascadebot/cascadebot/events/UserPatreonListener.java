package org.cascadebot.cascadebot.events;

import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.cascadebot.cascadebot.data.managers.CascadeUserDataManager;

public class UserPatreonListener extends ListenerAdapter {

    //TODO maybe add more checks
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        CascadeUserDataManager.getUser(event.getMember().getIdLong()).update();
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        CascadeUserDataManager.getUser(event.getMember().getIdLong()).update();
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        CascadeUserDataManager.getUser(event.getMember().getIdLong()).update();
    }
}
