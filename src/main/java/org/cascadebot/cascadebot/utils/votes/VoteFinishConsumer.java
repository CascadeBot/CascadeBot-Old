package org.cascadebot.cascadebot.utils.votes;

import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public enum VoteFinishConsumer {
    SKIP((channel, results) -> {
        GuildData data = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
        if (results.size() != 0 && results.get(0).getVote().equals(UnicodeConstants.TICK)) {
            Messaging.sendMessage(MessageType.INFO, channel, data.getLocale().i18n("commands.skip.skipping"), data.getCore().getUseEmbedForMessages());
            CascadeBot.INS.getMusicHandler().getPlayer(channel.getGuild().getIdLong()).skip();
        } else {
            Messaging.sendMessage(MessageType.INFO, channel, data.getLocale().i18n("commands.skip.not_skipping"), data.getCore().getUseEmbedForMessages());
        }
    });

    private final BiConsumer<TextChannel, List<VoteResult>> consumer;
    VoteFinishConsumer(BiConsumer<TextChannel, List<VoteResult>> consumer) {
        this.consumer = consumer;
    }

    public BiConsumer<TextChannel, List<VoteResult>> getConsumer() {
        return consumer;
    }
}
