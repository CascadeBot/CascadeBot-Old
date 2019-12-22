package org.cascadebot.cascadebot.commands.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandCore;
import org.cascadebot.cascadebot.data.objects.donation.Tier;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DonateCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        List<Page> pages = new ArrayList<>();

        builder.setTitle(context.i18n("commands.donate.embed_title"));
        builder.setDescription(context.i18n("commands.donate.embed_description"));
        builder.setFooter(context.i18n("commands.donate.embed_footer"), context.getSelfUser().getAvatarUrl());
        builder.addField("Patreon", context.i18n("commands.donate.embed_patreon"), true);

        pages.add(new PageObjects.EmbedPage(builder));
        for (Map.Entry<String, Tier> tier : Tier.getTiers().entrySet()) {
            if (tier.getKey().equals("default") || tier.getKey().equals("nitro")) {
                continue;
            }
            String title = tier.getKey();
            pages.add(new PageObjects.EmbedPage(new EmbedBuilder()
                    .setDescription((tier.getValue().getGuildTierString(context.getLocale(), null)))
                    .setTitle(StringUtils.capitalize(title))
            ));
        }

        context.getUIMessaging().sendPagedMessage(pages);
    }

    @Override
    public String command() {
        return "donate";
    }
}
