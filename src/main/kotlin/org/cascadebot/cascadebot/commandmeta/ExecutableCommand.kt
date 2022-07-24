/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.utils.pagination.Page

abstract class ExecutableCommand {

    val commandPath: String
        get() = "commands.$absoluteCommand.command"

    val descriptionPath: String
        get() = "commands.$absoluteCommand.description"

    val absoluteCommand: String
        get() = when (this) {
            is SubCommand -> this.parent() + "." + command()
            else -> command()
        }

    abstract fun onCommand(sender: Member, context: CommandContext)

    abstract fun command(): String

    open fun description(): String? {
        return null
    }

    fun command(locale: Locale): String {
        if (Language.hasLanguageEntry(locale, commandPath)) {
            return Language.i18n(locale, commandPath)
        }
        return command()
    }

    fun fullCommand(locale: Locale): String {
        if (this is SubCommand) {
            return this.getParent().command(locale) + " " + command(locale)
        }
        return command(locale)
    }

    fun description(locale: Locale): String? {
        return if (Language.hasLanguageEntry(locale, descriptionPath) || description() == null) {
            Language.i18n(locale, descriptionPath)
        } else {
            description()
        }
    }

    open fun deleteMessages(): Boolean = true

    open fun additionalUsagePages(locale: Locale): List<Page> = listOf()


}