package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FlagsCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        GuildData dataForList = context.getData();
        Guild guildForList = context.getGuild();

        String flags = Arrays.stream(Flag.values())
                             .map(flag -> FormatUtils.formatEnum(flag, context.getLocale()) + " - " +
                                     (dataForList.isFlagEnabled(flag) ? UnicodeConstants.TICK : UnicodeConstants.RED_CROSS))
                             .collect(Collectors.joining("\n"));

        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(guildForList.getName());
        builder.setThumbnail(guildForList.getIconUrl());

        builder.addField(context.i18n("words.flags"), flags, false);

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
        return CascadePermission.of("flags", false);
    }

}
