package org.cascadebot.cascadebot.commands.subcommands.guild;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class GuildListSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(context.getGuild().getName());
        builder.setThumbnail(context.getGuild().getIconUrl());

        for (Flag flag : Flag.values()) {
            if (context.getData().isFlagEnabled(flag)) {
                builder.addField(flag.name(), "Enabled " + UnicodeConstants.TICK, false);
            } else if (!context.getData().isFlagEnabled(flag)) {
                builder.addField(flag.name(), "Disabled " + UnicodeConstants.RED_CROSS, false);
            }
        }

        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

}
