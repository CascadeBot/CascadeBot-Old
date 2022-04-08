/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildTagEntity;
import org.cascadebot.cascadebot.data.entities.GuildTagId;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects;
import org.cascadebot.cascadebot.utils.placeholders.PlaceholdersKt;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class TagCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        String tagName = context.getArg(0).toLowerCase();

        GuildTagEntity tag = context.getDataObject(GuildTagEntity.class, new GuildTagId(context.getGuildId(), tagName));

        if (tag == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", tagName));
            return;
        }

        context.reply(tag.formatTag(context));
    }

    @Override
    public Module module() {
        return Module.MANAGEMENT;
    }

    @Override
    public String command() {
        return "tag";
    }

    @Override
    public Set<SubCommand> subCommands() {
        return Set.of(new TagCreateSubCommand(), new TagDeleteSubCommand(), new TagListSubCommand(), new TagRawSubCommand(), new TagPlaceholdersSubCommand(),
                new TagCategorySubCommand(), new TagEditSubCommand());
    }

    @NotNull
    @Override
    public List<Page> additionalUsagePages(@NotNull Locale locale) {
        return List.of(
                PlaceholdersKt.getPlaceholderUsagePage(
                        PlaceholderObjects.getTags().getPlaceholders(),
                        Language.i18n(locale, "placeholders.tags.title"),
                        locale
                )
        );
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("tag", false);
    }

}
