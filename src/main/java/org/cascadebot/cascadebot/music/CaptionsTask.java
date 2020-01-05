package org.cascadebot.cascadebot.music;

import lavalink.client.player.IPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.tasks.Task;

import java.util.List;

public class CaptionsTask extends Task {

    private final long guildId;
    private long channelId;
    private final long messageId;
    private final Captions captions;

    public CaptionsTask(long guildId, long channelId, long messageId, Captions captions) {
        super("captions-" + guildId);
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.captions = captions;
    }

    @Override
    protected void execute() {

        IPlayer player = CascadeBot.INS.getMusicHandler().getPlayer(this.guildId).getPlayer();
        if (player.getPlayingTrack() == null) this.cancel();

        List<String> captionSet = this.captions.getCaptions((player.getPlayingTrack().getPosition() / 1000D));
        EmbedBuilder embedBuilder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO);
        if (!captionSet.isEmpty()) {
            embedBuilder.setDescription(String.join("\n", captionSet));
        } else {
            embedBuilder.setDescription(Language.i18n(guildId, "commands.karaoke.no_lyrics_atm"));
        }

        TextChannel channel = CascadeBot.INS.getShardManager().getTextChannelById(this.channelId);
        if (channel != null) {
            channel.retrieveMessageById(this.messageId).queue(message -> {
                message.editMessage(embedBuilder.build()).override(true).queue();
            }, error -> this.cancel());
        } else {
            this.cancel();
        }

        if (player.getPlayingTrack().getPosition() >= (player.getPlayingTrack().getDuration() - 15)) {
            this.cancel();
        }

    }

}
