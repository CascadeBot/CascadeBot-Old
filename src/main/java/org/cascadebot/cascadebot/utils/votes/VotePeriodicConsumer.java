package org.cascadebot.cascadebot.utils.votes;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commands.music.SkipCommand;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;

import java.util.List;
import java.util.function.BiConsumer;

public enum VotePeriodicConsumer {
    SKIP((results, message) -> {
        StringBuilder resultsBuilder = new StringBuilder();
        for (VoteResult result : results) {
            resultsBuilder.append(result.getVote()).append(" (").append(result.getAmount()).append(")\n");
        }
        GuildData data = GuildDataManager.getGuildData(message.getGuild().getIdLong());
        VoteGroup group = data.getVoteGroups().get("skip");
        CascadeBot.INS.getShardManager().retrieveUserById(group.getOwnerId()).queue(user -> {
            EmbedBuilder skipVoteEmbed = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, user, data.getLocale())
                    .setTitle(data.getLocale().i18n("commands.skip.skip_vote_title"));
            if (resultsBuilder.length() > 0) {
                skipVoteEmbed.setDescription(resultsBuilder.toString());
            }
            message.editMessage(skipVoteEmbed.build()).override(true).setActionRows(message.getActionRows()).queue();
        });
    });

    private final BiConsumer<List<VoteResult>, Message> consumer;
    VotePeriodicConsumer(BiConsumer<List<VoteResult>, Message> consumer) {
        this.consumer = consumer;
    }

    public BiConsumer<List<VoteResult>, Message> getConsumer() {
        return consumer;
    }
}
