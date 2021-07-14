package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.data.objects.LoopMode;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.music.TrackData;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow;
import org.cascadebot.cascadebot.utils.interactions.CascadeButton;
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer;
import org.cascadebot.cascadebot.utils.interactions.InteractionMessage;

public class PlayingCommand extends MainCommand {

    ComponentContainer container = new ComponentContainer();
    private CascadeActionRow mainRow = new CascadeActionRow();

    private CascadeButton playButton = new CascadeButton(ButtonStyle.PRIMARY, Emoji.fromUnicode(UnicodeConstants.PLAY), (runner, channel, message) -> {
        if (CascadeBot.INS.getPermissionsManager().isAuthorised(CascadeBot.INS.getCommandManager().getCommandByDefault("resume"), GuildDataManager.getGuildData(channel.getGuild().getIdLong()), runner)) {
            handlePlayPause(channel.getGuild().getIdLong(), message);
        }
    });

    private CascadeButton pauseButton = new CascadeButton(ButtonStyle.PRIMARY, Emoji.fromUnicode(UnicodeConstants.PAUSE), (runner, channel, message) -> {
        if (CascadeBot.INS.getPermissionsManager().isAuthorised(CascadeBot.INS.getCommandManager().getCommandByDefault("pause"), GuildDataManager.getGuildData(channel.getGuild().getIdLong()), runner)) {
            handlePlayPause(channel.getGuild().getIdLong(), message);
        }
    });

    private CascadeButton repeat = new CascadeButton(ButtonStyle.PRIMARY, Emoji.fromUnicode(UnicodeConstants.REPEAT), (runner, channel, message) -> {

        if (CascadeBot.INS.getPermissionsManager().isAuthorised(CascadeBot.INS.getCommandManager().getCommandByDefault("loop"), GuildDataManager.getGuildData(channel.getGuild().getIdLong()), runner)) {
            handleRepeat(channel.getGuild().getIdLong(), LoopMode.PLAYLIST, message);
        }
    });

    private CascadeButton repeatOne = new CascadeButton(ButtonStyle.PRIMARY, Emoji.fromUnicode(UnicodeConstants.REPEAT_ONCE), (runner, channel, message) -> {
        if (CascadeBot.INS.getPermissionsManager().isAuthorised(CascadeBot.INS.getCommandManager().getCommandByDefault("loop"), GuildDataManager.getGuildData(channel.getGuild().getIdLong()), runner)) {
            handleRepeat(channel.getGuild().getIdLong(), LoopMode.SONG, message);
        }
    });

    private CascadeButton noRepeat = new CascadeButton(ButtonStyle.PRIMARY, Emoji.fromEmote(CascadeBot.INS.getShardManager().getEmoteById(Config.INS.getGlobalEmotes().get("norepeat"))), (runner, channel, message) -> {

        if (CascadeBot.INS.getPermissionsManager().isAuthorised(CascadeBot.INS.getCommandManager().getCommandByDefault("loop"), GuildDataManager.getGuildData(channel.getGuild().getIdLong()), runner)) {
            handleRepeat(channel.getGuild().getIdLong(), LoopMode.DISABLED, message);
        }
    });

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadePlayer player = context.getMusicPlayer();

        if (player.getPlayingTrack() == null) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.playing.no_music_playing"));
            return;
        }
        if (player.isPaused()) {
            mainRow.addComponent(playButton);
        } else {
            mainRow.addComponent(pauseButton);
        }
        mainRow.addComponent(new CascadeButton(ButtonStyle.PRIMARY, Emoji.fromUnicode(UnicodeConstants.STOP), (runner, channel, message) -> {
            if (context.hasPermission(runner, "stop")) {
                context.getMusicPlayer().stop();
                message.getMessage().delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
            }
        }));
        mainRow.addComponent(new CascadeButton(ButtonStyle.PRIMARY, Emoji.fromUnicode(UnicodeConstants.FAST_FORWARD), (runner, channel, message) -> {
            if (context.hasPermission(runner, "skip")) {
                context.runOtherCommand("skip", runner, context);
                message.editMessage(getSongEmbed(player, context.getGuild().getIdLong())).queue();
                if (player.getPlayingTrack() == null) {
                    message.getMessage().delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                }
            }
        }));
        switch (player.getLoopMode()) {
            case DISABLED:
                mainRow.addComponent(repeat);
                break;
            case PLAYLIST:
                mainRow.addComponent(repeatOne);
                break;
            case SONG:
                mainRow.addComponent(noRepeat);
                break;
        }
        container.addRow(mainRow);
        if (context.getData().isFlagEnabled(Flag.MUSIC_SERVICES)) {
            CascadeActionRow volumeRow = new CascadeActionRow();
            volumeRow.addComponent(new CascadeButton(ButtonStyle.PRIMARY, Emoji.fromUnicode(UnicodeConstants.VOLUME_DOWN), (runner, channel, message) -> {
                if (context.hasPermission(runner, "volume")) {
                    int volume = context.getMusicPlayer().getVolume();
                    volume -= 10;
                    if (volume <= 0) {
                        volume = 0;
                    }
                    context.getMusicPlayer().setVolume(volume);
                    message.editMessage(getSongEmbed(player, context.getGuild().getIdLong())).queue();
                }
            }));
            volumeRow.addComponent(new CascadeButton(ButtonStyle.PRIMARY, Emoji.fromUnicode(UnicodeConstants.VOLUME_UP), (runner, channel, message) -> {
                if (context.hasPermission(runner, "volume")) {
                    int volume = context.getMusicPlayer().getVolume();
                    volume += 10;
                    if (volume >= 100) {
                        volume = 100;
                    }
                    context.getMusicPlayer().setVolume(volume);
                    message.editMessage(getSongEmbed(player, context.getGuild().getIdLong())).queue();
                }
            }));
            volumeRow.addComponent(new CascadeButton(ButtonStyle.PRIMARY, Emoji.fromUnicode(UnicodeConstants.BAR_CHART), (runner, channel, message) -> {
                context.runOtherCommand("equalizer", runner, context);
            }));
            container.addRow(volumeRow);
        }

        context.getUiMessaging().sendComponentMessage(getSongEmbed(context.getMusicPlayer(), context.getGuild().getIdLong()), container);

    }

    private MessageEmbed getSongEmbed(CascadePlayer player, long guildId) {
        AudioTrack track = player.getPlayingTrack();
        EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        if (track == null) {
            embedBuilder.setDescription(Language.i18n(guildId, "commands.playing.no_music_playing"));
            return embedBuilder.build();
        }
        embedBuilder.setAuthor(track.getInfo().author);
        embedBuilder.setTitle(track.getInfo().title, track.getInfo().uri);
        if (player.getArtwork() != null) {
            embedBuilder.setThumbnail(player.getArtwork());
        }
        embedBuilder.addField(Language.i18n(guildId, "words.status"), player.isPaused() ? UnicodeConstants.PAUSE + " " + Language.i18n(guildId, "words.paused") : UnicodeConstants.PLAY + " " + Language.i18n(guildId, "words.playing"), true);

        if (!track.getInfo().isStream) {
            embedBuilder.addField(Language.i18n(guildId, "words.progress"), player.getTrackProgressBar(GuildDataManager.getGuildData(guildId).getCore().getUseEmbedForMessages()), false);
        }

        embedBuilder.addField(Language.i18n(guildId, "commands.playing.amount_played"), FormatUtils.formatLongTimeMills(player.getTrackPosition()) + " / " +
                (!track.getInfo().isStream ? FormatUtils.formatLongTimeMills(track.getDuration()) : UnicodeConstants.INFINITY_SYMBOL), true);
        embedBuilder.addField(Language.i18n(guildId, "words.volume"), player.getVolume() + "%", true);
        embedBuilder.addField(Language.i18n(guildId, "commands.playing.loop_mode"), FormatUtils.formatEnum(player.getLoopMode(), Language.getGuildLocale(guildId)), true);
        if (track.getUserData() instanceof TrackData) { //TODO find out why user data sometimes gets set to null.
            long id = ((TrackData) track.getUserData()).getUserId();
            User user = CascadeBot.INS.getShardManager().getUserById(id);
            if (user != null) {
                embedBuilder.addField(Language.i18n(guildId, "words.requested_by"), user.getAsTag(), true);
            }
        }
        AudioTrack next = player.getQueue().peek();
        if (next != null) {
            StringBuilder nextSongBuilder = new StringBuilder();
            nextSongBuilder.append("**").append(next.getInfo().title).append("**");
            if (next.getUserData() instanceof TrackData) {
                long id = ((TrackData) next.getUserData()).getUserId();
                User user = CascadeBot.INS.getShardManager().getUserById(id);
                if (user != null) {
                    nextSongBuilder.append('\n').append(Language.i18n(guildId, "words.requested_by")).append(user.getAsTag());
                }
            }
            embedBuilder.addField(Language.i18n(guildId, "commands.playing.up_next"), nextSongBuilder.toString(), false);
        }

        return embedBuilder.build();
    }

    public void handlePlayPause(long guildId, InteractionMessage buttonMessage) {
        CascadePlayer player = CascadeBot.INS.getMusicHandler().getPlayer(guildId);
        if (player.isPaused()) {
            player.setPaused(false);
            mainRow.setComponent(0, pauseButton);
        } else {
            player.setPaused(true);
            mainRow.setComponent(0, playButton);
        }
        GuildDataManager.getGuildData(guildId).addComponents(buttonMessage.getMessage().getChannel(), buttonMessage.getMessage(), container);
        buttonMessage.editMessage(getSongEmbed(player, guildId)).queue();
    }

    public void handleRepeat(long guildId, LoopMode mode, InteractionMessage buttonMessage) {
        CascadePlayer player = CascadeBot.INS.getMusicHandler().getPlayer(guildId);
        switch (mode) {
            case DISABLED:
                mainRow.setComponent(3, repeat);
                break;
            case PLAYLIST:
                mainRow.setComponent(3, repeatOne);
                break;
            case SONG:
                mainRow.setComponent(3, noRepeat);
                break;
        }
        player.loopMode(mode);
        GuildDataManager.getGuildData(guildId).addComponents(buttonMessage.getMessage().getChannel(), buttonMessage.getMessage(), container);
        buttonMessage.editMessage(getSongEmbed(player, guildId)).queue();
    }

    @Override
    public Module module() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "playing";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("playing", true);
    }

}
