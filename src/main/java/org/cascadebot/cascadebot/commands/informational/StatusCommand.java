package org.cascadebot.cascadebot.commands.informational;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class StatusCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();

        builder.setTitle(context.getGuild().getName());
        builder.setThumbnail(context.getGuild().getIconUrl());

        builder.addField("Cascade Version", CascadeBot.getVersion().toString(), true);
        builder.addField("Total Shards", String.valueOf(CascadeBot.INS.getShardManager().getShardsTotal()), true);
        builder.addField("Online Shards", String.valueOf(CascadeBot.INS.getShardManager().getShardsRunning()), true);
        builder.addField("Average Ping", String.valueOf(CascadeBot.INS.getShardManager().getAveragePing()), true);
        builder.addField("Shard Status", CascadeBot.INS.getShardManager().getStatus(context.getChannel().getJDA().getShardInfo().getShardId()).toString().toLowerCase(), true);
        builder.addField("Shard ID", String.valueOf(context.getChannel().getJDA().getShardInfo().getShardId()), true);

        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public String command() {
        return "status";
    }

    @Override
    public Module getModule() {
        return Module.INFORMATIONAL;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Status Command", "status", true);
    }

    @Override
    public String description() {
        return null;
    }

}
