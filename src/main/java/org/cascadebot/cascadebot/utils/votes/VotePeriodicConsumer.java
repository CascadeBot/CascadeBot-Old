package org.cascadebot.cascadebot.utils.votes;

import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.function.BiConsumer;

public enum VotePeriodicConsumer {
    SKIP((results, message) -> {
        StringBuilder resultsBuilder = new StringBuilder();
        for (VoteResult result : results) {
            resultsBuilder.append(result.getVote()).append(" (").append(result.getAmount()).append(")\n");
        }
        /*GuildData data = GuildDataManager.getGuildData(message.getGuild().getIdLong());
        VoteGroup group = data.getVoteGroups().get("skip");
        CascadeBot.INS.getShardManager().retrieveUserById(group.getOwnerId()).queue(user -> {
            EmbedBuilder skipVoteEmbed = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, user, data.getLocale())
                    .setTitle(data.getLocale().i18n("commands.skip.skip_vote_title"));
            if (resultsBuilder.length() > 0) {
                skipVoteEmbed.setDescription(resultsBuilder.toString());
            }
            message.editMessage(skipVoteEmbed.build()).override(true).setActionRows(message.getActionRows()).queue();
        });*/
    });

    private final BiConsumer<List<VoteResult>, Message> consumer;
    VotePeriodicConsumer(BiConsumer<List<VoteResult>, Message> consumer) {
        this.consumer = consumer;
    }

    public BiConsumer<List<VoteResult>, Message> getConsumer() {
        return consumer;
    }
}
