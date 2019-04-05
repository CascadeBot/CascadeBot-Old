package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import org.cascadebot.cascadebot.utils.buttons.ButtonsCache;
import org.cascadebot.cascadebot.utils.buttons.IButtonRunnable;

import java.util.Set;

public class PlayingCommand implements ICommandMain {

    private Button.UnicodeButton playButton = new Button.UnicodeButton("\u25B6" /* ▶ Play */, (runner, channel, message) -> {
        handlePlayPause(GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getButtonsCache().get(channel.getIdLong()).get(message.getIdLong()), message);
    });

    private Button.UnicodeButton pauseButton = new Button.UnicodeButton("\u23F8" /* ⏸ Pause */, (runner, channel, message) -> {
        handlePlayPause(GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getButtonsCache().get(channel.getIdLong()).get(message.getIdLong()), message);
    });

    private Button.UnicodeButton repeat = new Button.UnicodeButton("\uD83D\uDD01" /* 🔁 Repeat */, (runner, channel, message) -> {
        ButtonGroup buttonGroup = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getButtonsCache().get(channel.getIdLong()).get(message.getIdLong());
        handleRepeat(buttonGroup, CascadePlayer.LoopMode.PLAYLIST, message);
    });

    private Button.UnicodeButton repeatOne = new Button.UnicodeButton("\uD83D\uDD02" /* 🔂 Repeat Once */, (runner, channel, message) -> {
        ButtonGroup buttonGroup = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getButtonsCache().get(channel.getIdLong()).get(message.getIdLong());
        handleRepeat(buttonGroup, CascadePlayer.LoopMode.SONG, message);
    });

    private Button.EmoteButton noRepeat = new Button.EmoteButton(CascadeBot.INS.getShardManager().getEmoteById(Config.INS.getGlobalEmotes().get("norepeat")), (runner, channel, message) -> {
        ButtonGroup buttonGroup = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getButtonsCache().get(channel.getIdLong()).get(message.getIdLong());
        handleRepeat(buttonGroup, CascadePlayer.LoopMode.DISABLED, message);
    });

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadePlayer player = context.getData().getMusicPlayer();

        if (player.getPlayer().getPlayingTrack() == null) {
            context.getTypedMessaging().replyWarning("No music playing!");
        } else {
            ButtonGroup buttonGroup = new ButtonGroup(sender.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
            if (context.getData().isFlagEnabled(Flag.MUSIC_SERVICES)) {
                buttonGroup.addButton(new Button.UnicodeButton("\uD83D\uDD09" /* 🔉 Volume down */, (runner, channel, message) -> {
                    int volume = context.getData().getMusicPlayer().getPlayer().getVolume();
                    volume -= 10;
                    if (volume <= 0) {
                        volume = 0;
                    }
                    context.getData().getMusicPlayer().getPlayer().setVolume(volume);
                    message.editMessage(getSongEmbed(player, context.getGuild().getIdLong())).queue();
                }));
                buttonGroup.addButton(new Button.UnicodeButton("\uD83D\uDD0A" /* 🔊 Volume up */, (runner, channel, message) -> {
                    int volume = context.getData().getMusicPlayer().getPlayer().getVolume();
                    volume += 10;
                    if(volume >= 100) {
                        volume = 100;
                    }
                    context.getData().getMusicPlayer().getPlayer().setVolume(volume);
                    message.editMessage(getSongEmbed(player, context.getGuild().getIdLong())).queue();
                }));
            }

            buttonGroup.addButton(new Button.UnicodeButton("\u23F9" /* ⏹ Stop */, (runner, channel, message) -> {
                context.getData().getMusicPlayer().stop();
                message.delete().queue();
            }));
            buttonGroup.addButton(new Button.UnicodeButton("\u23ED" /* ⏭ Skip */, (runner, channel, message) -> {
                context.getData().getMusicPlayer().skip(); //TODO make this run skip command
                message.editMessage(getSongEmbed(player, context.getGuild().getIdLong())).queue();
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

            context.getUIMessaging().sendButtonedMessage(getSongEmbed(context.getData().getMusicPlayer(), context.getGuild().getIdLong()), buttonGroup);
        }

    }

    private MessageEmbed getSongEmbed(CascadePlayer player, long guildID) {
        AudioTrack track = player.getPlayer().getPlayingTrack();
        EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        if(track == null) {
            embedBuilder.setDescription("No song playing");
            return embedBuilder.build();
        }
        embedBuilder.setAuthor(track.getInfo().author);
        embedBuilder.setTitle(track.getInfo().title, track.getInfo().uri);
        if (player.getArtwork() != null) {
            embedBuilder.setThumbnail(player.getArtwork());
        }
        embedBuilder.addField("Status", player.getPlayer().isPaused() ? "\u23F8 Paused" /* ⏸ Paused */ : "\u25B6 Playing" /* ▶ Playing */, true);

        if (!track.getInfo().isStream) {
            embedBuilder.addField("Progress", player.getTrackProgressBar(GuildDataManager.getGuildData(guildID).getSettings().useEmbedForMessages()), false);
        }

        embedBuilder.addField("Amount played", FormatUtils.formatLongTimeMills(track.getPosition()) + "/" +
                (!track.getInfo().isStream ? FormatUtils.formatLongTimeMills(track.getDuration()) : "\u221e" /* Infinity Symbol */), true);
        embedBuilder.addField("Volume", player.getPlayer().getVolume() + "%", true);

        return embedBuilder.build();
    }

    public void handlePlayPause(ButtonGroup buttonGroup, Message buttonMessage) {
        CascadePlayer player = GuildDataManager.getGuildData(buttonGroup.getGuildId()).getMusicPlayer();
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
        CascadePlayer player = GuildDataManager.getGuildData(buttonGroup.getGuildId()).getMusicPlayer();
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
    public Set<String> getGlobalAliases() {
        return Set.of("song");
    }

    @Override
    public String command() {
        return "playing";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Playing Command", "playing", true);
    }

    @Override
    public String description() {
        return "get playing music";
    }

}
