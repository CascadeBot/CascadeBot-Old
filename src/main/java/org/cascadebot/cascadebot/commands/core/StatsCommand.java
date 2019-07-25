package org.cascadebot.cascadebot.commands.core;

import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.io.FileUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandCore;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.MusicHandler;

import java.lang.management.ManagementFactory;

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
        builder.addField("RAM Usage", FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()), true);
        builder.addField("CPU Load", Math.round(osBean.getProcessCpuLoad() * 100) + "%", true);
        builder.addField("Total Shards", String.valueOf(CascadeBot.INS.getShardManager().getShardsTotal()), true);
        builder.addField("Online Shards", String.valueOf(CascadeBot.INS.getShardManager().getShardsRunning()), true);
        builder.addField("Gateway Ping", String.valueOf(context.getChannel().getJDA().getGatewayPing()), true);
        builder.addField("REST Ping", String.valueOf(context.getChannel().getJDA().getRestPing().complete()), true);
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
