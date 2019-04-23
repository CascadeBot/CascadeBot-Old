package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QueueCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadePlayer player = context.getMusicPlayer();
        EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();

        embedBuilder.setTitle("Queue");

        if (player.getTracks().isEmpty() && player.getPlayer().getPlayingTrack() == null) {
            context.getTypedMessaging().replyInfo("There are no tracks playing!");
            return;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("Current song: `").append(player.getPlayer().getPlayingTrack().getInfo().title).append("`\n");

        for (AudioTrack track : player.getTracks()) {
            builder.append("`").append(player.getPlayer().getPlayingTrack().getInfo().title).append("`\n");
        }

        List<Page> pages;

        if (context.getSettings().useEmbedForMessages()) {
            pages = PageUtils.splitStringToEmbedPages(builder.toString(), 1800, '\n');
        } else {
            pages = PageUtils.splitStringToStringPages(builder.toString(), 1800, '\n');
        }
        //builder.append("Requested by").append(sender.getUser().getName()).append("\n");
        context.getUIMessaging().sendPagedMessage(pages);
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }


    @Override
    public String command() {
        return "queue";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Queue", "queue", getModule());
    }

    @Override
    public String description() {
        return "Display the current queue";
    }

}
