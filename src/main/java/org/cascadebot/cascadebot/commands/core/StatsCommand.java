package org.cascadebot.cascadebot.commands.core;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.io.FileUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandCore;
import org.cascadebot.cascadebot.messaging.MessagingObjects;

import com.sun.management.OperatingSystemMXBean;
import org.cascadebot.cascadebot.music.MusicHandler;

import java.lang.management.ManagementFactory;
import java.util.logging.Filter;

public class StatsCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        builder.setTitle(context.getSelfUser().getName());
        builder.setThumbnail(context.getSelfUser().getAvatarUrl());

        builder.addField("Total Guilds", String.valueOf(CascadeBot.INS.getShardManager().getGuilds().size()), true);
        builder.addField("Active Guilds", String.valueOf(CascadeBot.INS.getShardManager().getGuildCache().size()), true);
        builder.addField("Active Voice Channels", String.valueOf(MusicHandler.getPlayers().entrySet().stream().filter(entry -> entry.getValue().getConnectedChannel() != null).count()), true);
        builder.addField("RAM Usage", FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory()), true);
        builder.addField("CPU Load", String.valueOf(osBean.getProcessCpuLoad()), true);
        builder.addField("Total Shards", String.valueOf(CascadeBot.INS.getShardManager().getShardsTotal()), true);
        builder.addField("Online Shards", String.valueOf(CascadeBot.INS.getShardManager().getShardsRunning()), true);
        builder.addField("Ping", String.valueOf(context.getChannel().getJDA().getPing()), true);
        builder.addField("Shard Status", CascadeBot.INS.getShardManager().getStatus(context.getChannel().getJDA().getShardInfo().getShardId()).toString(), true);
        builder.addField("Shard ID", String.valueOf(context.getChannel().getJDA().getShardInfo().getShardId() + 1), true);

        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public String command() {
        return "stats";
    }

    @Override
    public String description() {
        return "Returns information regarding the Bot.";
    }

}
