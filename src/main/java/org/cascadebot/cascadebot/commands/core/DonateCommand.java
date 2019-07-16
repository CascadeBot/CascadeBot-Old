package org.cascadebot.cascadebot.commands.core;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandCore;
import org.cascadebot.cascadebot.messaging.MessagingObjects;

public class DonateCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();

        builder.setTitle(context.i18n("commands.donate.embed_title"));
        builder.setDescription(context.i18n("commands.donate.embed_description"));
        builder.addField("Patreon", context.i18n("commands.donate.embed_patreon"), true);
        builder.addField("PayPal", context.i18n("commands.donate.embed_paypal"), true);
        builder.setFooter(context.i18n("commands.donate.embed_footer"), context.getSelfUser().getAvatarUrl());
        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public String command() {
        return "donate";
    }
}
