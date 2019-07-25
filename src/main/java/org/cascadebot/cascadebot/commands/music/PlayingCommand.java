package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

public class PlayingCommand implements ICommandMain {

    private Button.UnicodeButton playButton = new Button.UnicodeButton(UnicodeConstants.PLAY, (runner, channel, message) -> {
        if (CascadeBot.INS.getPermissionsManager().isAuthorised(CascadeBot.INS.getCommandManager().getCommandByDefault("resume"), GuildDataManager.getGuildData(channel.getGuild().getIdLong()), runner)) {
            handlePlayPause(GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getButtonsCache().get(channel.getIdLong()).get(message.getIdLong()), message);
        }
    });

    private Button.UnicodeButton pauseButton = new Button.UnicodeButton(UnicodeConstants.PAUSE, (runner, channel, message) -> {
        if (CascadeBot.INS.getPermissionsManager().isAuthorised(CascadeBot.INS.getCommandManager().getCommandByDefault("pause"), GuildDataManager.getGuildData(channel.getGuild().getIdLong()), runner)) {
            handlePlayPause(GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getButtonsCache().get(channel.getIdLong()).get(message.getIdLong()), message);
        }
    });

    private Button.UnicodeButton repeat = new Button.UnicodeButton(UnicodeConstants.REPEAT, (runner, channel, message) -> {

        if (CascadeBot.INS.getPermissionsManager().isAuthorised(CascadeBot.INS.getCommandManager().getCommandByDefault("loop"), GuildDataManager.getGuildData(channel.getGuild().getIdLong()), runner)) {
            ButtonGroup buttonGroup = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getButtonsCache().get(channel.getIdLong()).get(message.getIdLong());
            handleRepeat(buttonGroup, CascadePlayer.LoopMode.PLAYLIST, message);
        }
    });

    private Button.UnicodeButton repeatOne = new Button.UnicodeButton(UnicodeConstants.REPEAT_ONCE, (runner, channel, message) -> {
        if (CascadeBot.INS.getPermissionsManager().isAuthorised(CascadeBot.INS.getCommandManager().getCommandByDefault("loop"), GuildDataManager.getGuildData(channel.getGuild().getIdLong()), runner)) {
            ButtonGroup buttonGroup = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getButtonsCache().get(channel.getIdLong()).get(message.getIdLong());
            handleRepeat(buttonGroup, CascadePlayer.LoopMode.SONG, message);
        }
    });

    private Button.EmoteButton noRepeat = new Button.EmoteButton(Config.INS.getGlobalEmotes().get("norepeat"), (runner, channel, message) -> {

        if (CascadeBot.INS.getPermissionsManager().isAuthorised(CascadeBot.INS.getCommandManager().getCommandByDefault("loop"), GuildDataManager.getGuildData(channel.getGuild().getIdLong()), runner)) {
            ButtonGroup buttonGroup = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getButtonsCache().get(channel.getIdLong()).get(message.getIdLong());
            handleRepeat(buttonGroup, CascadePlayer.LoopMode.DISABLED, message);
        }
    });

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadePlayer player = context.getMusicPlayer();

        if (player.getPlayer().getPlayingTrack() == null) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.playing.no_music_playing"));
        } else {
            ButtonGroup buttonGroup = new ButtonGroup(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
            if (context.getData().isFlagEnabled(Flag.MUSIC_SERVICES)) {
                buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.VOLUME_DOWN, (runner, channel, message) -> {
                    if (context.hasPermission(runner, "volume")) {
                        int volume = context.getMusicPlayer().getPlayer().getVolume();
                        volume -= 10;
                        if (volume <= 0) {
                            volume = 0;
                        }
                        context.getMusicPlayer().getPlayer().setVolume(volume);
                        message.editMessage(getSongEmbed(player, context.getGuild().getIdLong())).queue();
                    }
                }));
                buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.VOLUME_UP, (runner, channel, message) -> {
                    if (context.hasPermission(runner, "volume")) {
                        int volume = context.getMusicPlayer().getPlayer().getVolume();
                        volume += 10;
                        if (volume >= 100) {
                            volume = 100;
                        }
                        context.getMusicPlayer().getPlayer().setVolume(volume);
                        message.editMessage(getSongEmbed(player, context.getGuild().getIdLong())).queue();
                    }
                }));
            }

            buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.STOP, (runner, channel, message) -> {
                if (context.hasPermission(runner, "stop")) {
                    context.getMusicPlayer().stop();
                    message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                }
            }));
            buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.FAST_FORWARD, (runner, channel, message) -> {
                if (context.hasPermission(runner, "skip")) {
                    ICommandMain skip = CascadeBot.INS.getCommandManager().getCommandByDefault("skip");
                    skip.onCommand(runner, new CommandContext(
                            skip,
                            CascadeBot.INS.getClient(),
                            context.getChannel(),
                            message,
                            context.getGuild(),
                            context.getData(),
                            new String[0],
                            runner,
                            "skip",
                            false
                    ));
                    message.editMessage(getSongEmbed(player, context.getGuild().getIdLong())).queue();
                    if (player.getPlayer().getPlayingTrack() == null) {
                        message.clearReactions().queue();
                    }
                }
            }));

            switch (player.getLoopMode()) {
                case DISABLED:
                    buttonGroup.addButton(repeat);
                    break;
                case PLAYLIST:
                    buttonGroup.addButton(repeatOne);
                    break;
                case SONG:
                    buttonGroup.addButton(noRepeat);
                    break;
            }

            buttonGroup.addButton(player.getPlayer().isPaused() ? playButton : pauseButton);

            context.getUIMessaging().sendButtonedMessage(getSongEmbed(context.getMusicPlayer(), context.getGuild().getIdLong()), buttonGroup);
        }

    }

    private MessageEmbed getSongEmbed(CascadePlayer player, long guildId) {
        AudioTrack track = player.getPlayer().getPlayingTrack();
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
        embedBuilder.addField(Language.i18n(guildId, "words.status"), player.getPlayer().isPaused() ? UnicodeConstants.PAUSE + " " + Language.i18n(guildId, "words.paused") : UnicodeConstants.PLAY + " " + Language.i18n(guildId, "words.playing"), true);

        if (!track.getInfo().isStream) {
            embedBuilder.addField(Language.i18n(guildId, "words.progress"), player.getTrackProgressBar(GuildDataManager.getGuildData(guildId).getCoreSettings().isUseEmbedForMessages()), false);
        }

        embedBuilder.addField("Amount played", FormatUtils.formatLongTimeMills(player.getPlayer().getTrackPosition()) + " / " +
                (!track.getInfo().isStream ? FormatUtils.formatLongTimeMills(track.getDuration()) : UnicodeConstants.INFINITY_SYMBOL), true);
        embedBuilder.addField(Language.i18n(guildId, "words.volume"), player.getPlayer().getVolume() + "%", true);
        embedBuilder.addField(Language.i18n(guildId, "commands.playing.loop_mode"), FormatUtils.formatEnum(player.getLoopMode(), Language.getGuildLocale(guildId)), true);
        if (track.getUserData() instanceof Long) { //TODO find out why user data sometimes gets set to null.
            embedBuilder.addField(Language.i18n(guildId, "words.requested_by"), CascadeBot.INS.getShardManager().getUserById((Long) track.getUserData()).getAsTag(), true);
        }
        AudioTrack next = player.getQueue().peek();
        if (next != null) {
            embedBuilder.addField(Language.i18n(guildId, "commands.playing.up_next"), "**" + next.getInfo().title + "**\n" + Language.i18n(guildId, "words.requested_by") +
                    CascadeBot.INS.getShardManager().getUserById((Long) next.getUserData()).getAsTag(), false);
        }

        return embedBuilder.build();
    }

    public void handlePlayPause(ButtonGroup buttonGroup, Message buttonMessage) {
        CascadePlayer player = CascadeBot.INS.getMusicHandler().getPlayer(buttonGroup.getGuildId());
        if (player.getPlayer().isPaused()) {
            player.getPlayer().setPaused(false);
            buttonGroup.removeButton(playButton);
            buttonGroup.addButton(pauseButton);
        } else {
            player.getPlayer().setPaused(true);
            buttonGroup.removeButton(pauseButton);
            buttonGroup.addButton(playButton);
        }
        buttonMessage.editMessage(getSongEmbed(player, buttonGroup.getGuildId())).queue();
    }

    public void handleRepeat(ButtonGroup buttonGroup, CascadePlayer.LoopMode mode, Message buttonMessage) {
        CascadePlayer player = CascadeBot.INS.getMusicHandler().getPlayer(buttonGroup.getGuildId());
        switch (mode) {
            case DISABLED:
                buttonGroup.removeButton(noRepeat);
                buttonGroup.addButton(repeat);
                break;
            case PLAYLIST:
                buttonGroup.removeButton(repeat);
                buttonGroup.addButton(repeatOne);
                break;
            case SONG:
                buttonGroup.removeButton(repeatOne);
                buttonGroup.addButton(noRepeat);
                break;
        }
        player.loopMode(mode);
        buttonMessage.editMessage(getSongEmbed(player, buttonGroup.getGuildId())).queue();
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "playing";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("playing", true);
    }

}
