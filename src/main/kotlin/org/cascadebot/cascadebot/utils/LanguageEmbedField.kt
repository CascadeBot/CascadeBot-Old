package org.cascadebot.cascadebot.utils

import club.minnced.discord.webhook.send.WebhookEmbed
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale

class LanguageEmbedField {

    var inline: Boolean = false

    var titleLanguagePath: String = ""
    var titleLanguageObjects: MutableList<String> = ArrayList()

    var valueLanguagePath: String = ""
    var valueLanguageObjects: MutableList<String> = ArrayList()

    constructor(inline: Boolean = true,
                titleLanguagePath: String,
                valueLanguagePath: String, vararg valueLanguageObjects: String) {
        this.inline = inline;
        this.titleLanguagePath = titleLanguagePath
        this.valueLanguagePath = valueLanguagePath
        this.valueLanguageObjects.addAll(valueLanguageObjects)
        // TODO add checks for if language string exists
    }

    constructor()

    fun addTitleObjects(vararg titleLanguageObjects: String) {
        this.titleLanguageObjects.addAll(titleLanguageObjects)
        // TODO add checks for if there is room for objects in language string
    }

    fun addValueObjects(vararg valueLanguageObjects: String) {
        this.valueLanguageObjects.addAll(valueLanguageObjects)
    }

    fun getLocalizedEmbedField(locale: Locale): WebhookEmbed.EmbedField {
        return WebhookEmbed.EmbedField(inline,
        Language.i18n(locale, titleLanguagePath, titleLanguageObjects.joinToString(" ") + UnicodeConstants.ZERO_WIDTH_SPACE),
                Language.i18n(locale, valueLanguagePath, valueLanguageObjects.joinToString(" ") + UnicodeConstants.ZERO_WIDTH_SPACE))
    }

}