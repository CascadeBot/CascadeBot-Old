package org.cascadebot.cascade.commands.management;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.UnicodeConstants;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandMain;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.data.objects.Flag;
import org.cascadebot.cascade.data.objects.GuildData;
import org.cascadebot.cascade.messaging.MessagingObjects;
import org.cascadebot.cascade.permissions.CascadePermission;
import org.cascadebot.cascade.utils.FormatUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FlagsCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        GuildData dataForList = context.getData();
        Guild guildForList = context.getGuild();

        String flags = Arrays.stream(Flag.values())
                             .map(flag -> FormatUtils.formatEnum(flag) + " - " +
                                     (dataForList.isFlagEnabled(flag) ? UnicodeConstants.TICK : UnicodeConstants.RED_CROSS))
                             .collect(Collectors.joining("\n"));

        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(guildForList.getName());
        builder.setThumbnail(guildForList.getIconUrl());

        builder.addField("Flags", flags, false);

        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public Module getModule() {
        return Module.MANAGEMENT;
    }

    @Override
    public String command() {
        return "flags";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Flag Command", "flags", false);
    }

    @Override
    public String description() {
        return "Command to get a list of guild flags, and see if they're enabled or disabled.";
    }

}
