package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.scripting.objects.ScriptSnowflake;

public class ScriptChannel extends ScriptSnowflake {

    ScriptChannel() {

    }

    protected GuildChannel internalChannel;

    public static ScriptChannel fromJda(GuildChannel channel) {
        if (channel instanceof Category) {
            ScriptCategory scriptCategory = new ScriptCategory();
            scriptCategory.setInternalCategory((Category) channel);
            return scriptCategory;
        } else if (channel instanceof TextChannel) {
            ScriptTextChannel scriptTextChannel = new ScriptTextChannel();
            scriptTextChannel.setInternalTextChannel((TextChannel) channel);
            return scriptTextChannel;
        } else if (channel instanceof VoiceChannel) {
            ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel();
            scriptVoiceChannel.setInternalVoiceChannel((VoiceChannel) channel);
            return scriptVoiceChannel;
        } else if (channel instanceof StoreChannel) {
            ScriptStoreChannel scriptStoreChannel = new ScriptStoreChannel();
            scriptStoreChannel.setInternalStoreChannel((StoreChannel) channel);
            return scriptStoreChannel;
        } else {
            return null;
        }
    }

}
