/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.goodbye

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.language.Language.i18n
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects.goodbyes
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects.tags
import org.cascadebot.cascadebot.utils.placeholders.getPlaceholderUsagePage
import org.cascadebot.cascadebot.utils.toPercentage
import org.cascadebot.cascadebot.utils.truncate

class GoodbyeCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        context.uiMessaging.replyUsage()
    }

    override fun command(): String = "goodbye"

    override fun module(): Module = Module.MANAGEMENT

    override fun permission(): CascadePermission = CascadePermission.of("goodbye", false)

    override fun additionalUsagePages(locale: Locale): List<Page> {
        return listOf(
                getPlaceholderUsagePage(
                        goodbyes.placeholders,
                        i18n(locale, "placeholders.goodbyes.title"),
                        locale
                )
        )
    }

    override fun subCommands(): Set<SubCommand> = setOf(GoodbyeAddSubCommand(), GoodbyeClearSubCommand(), GoodbyeRemoveSubCommand(), GoodbyeWeightSubCommand())

}