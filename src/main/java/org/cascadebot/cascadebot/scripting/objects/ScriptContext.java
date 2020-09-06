package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptTextChannel;

public class ScriptContext {

    private String[] args;
    private ScriptTextChannel channel;
    private ScriptUser user;
    private ScriptGuild guild;
    private String message;

    public ScriptContext(String[] args, TextChannel textChannel, Member member, Guild guild, String message) {
        this.args = args;

        this.user = new ScriptUser();
        this.user.setInternalUser(member);

        this.channel = new ScriptTextChannel();
        this.channel.setInternalTextChannel(textChannel);

        this.guild = new ScriptGuild();
        this.guild.setInternalGuild(guild);
        
        this.message = message;
    }

}
