package org.cascadebot.cascadebot.commands.core;

import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandCore;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.LavaPlayerAudioSendHandler;
import org.cascadebot.cascadebot.music.MusicHandler;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class StatusCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        JDA jda = CascadeBot.INS.getShardManager().getShardById(context.getChannel().getJDA().getShardInfo().getShardId());

        builder.setTitle(context.getSelfUser().getName());
        builder.setThumbnail(context.getSelfUser().getAvatarUrl());

        builder.addField("Cascade Version", CascadeBot.getVersion().toString(), true);
        builder.addField("JDA Version", JDAInfo.VERSION, true);
        builder.addField("Total Shards", String.valueOf(CascadeBot.INS.getShardManager().getShardsTotal()), true);
        builder.addField("Online Shards", String.valueOf(CascadeBot.INS.getShardManager().getShardsRunning()), true);
        builder.addField("Ping", String.valueOf(context.getChannel().getJDA().getPing()), true);
        builder.addField("Shard Status", CascadeBot.INS.getShardManager().getStatus(context.getChannel().getJDA().getShardInfo().getShardId()).toString().toLowerCase(), true);
        builder.addField("Shard ID", String.valueOf(context.getChannel().getJDA().getShardInfo().getShardId() + 1), true);

        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public String command() {
        return "status";
    }

    @Override
    public String description() {
        return "Returns information regarding the Bot.";
    }

}
