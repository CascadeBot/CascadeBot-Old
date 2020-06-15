package org.cascadebot.cascadebot.moderation

import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale

sealed class ModlogEmbedPart {

    abstract fun build(locale: Locale, embedBuilder: WebhookEmbedBuilder)

}

class ModlogEmbedField() : ModlogEmbedPart() {

    var inline: Boolean = false

    var titleLanguagePath: String = ""
    var titleLanguageObjects: MutableList<String> = ArrayList()

    var valueLanguagePath: String = ""
    var valueLanguageObjects: MutableList<String> = ArrayList()

    constructor(inline: Boolean = true,
                titleLanguagePath: String,
                valueLanguagePath: String, vararg valueLanguageObjects: String): this() {
        this.inline = inline;
        this.titleLanguagePath = titleLanguagePath
        this.valueLanguagePath = valueLanguagePath
        this.valueLanguageObjects.addAll(valueLanguageObjects)
    }

    fun addTitleObjects(vararg titleLanguageObjects: String) {
        this.titleLanguageObjects.addAll(titleLanguageObjects)
    }

    fun addValueObjects(vararg valueLanguageObjects: String) {
        this.valueLanguageObjects.addAll(valueLanguageObjects)
    }

    override fun build(locale: Locale, embedBuilder: WebhookEmbedBuilder) {
        embedBuilder.addField(WebhookEmbed.EmbedField(inline,
                Language.i18n(locale, titleLanguagePath, *titleLanguageObjects.toTypedArray()),
                Language.i18n(locale, valueLanguagePath, *valueLanguageObjects.toTypedArray())))
    }

}

class ModlogEmbedDescription() : ModlogEmbedPart() {

    var languagePath: String = ""
    var languageObjects: MutableList<String> = ArrayList()

    constructor(languagePath: String, vararg languageObjects: String): this() {
        this.languagePath = languagePath
        this.languageObjects.addAll(languageObjects)
    }

    override fun build(locale: Locale, embedBuilder: WebhookEmbedBuilder) {
        embedBuilder.setDescription(Language.i18n(locale, languagePath, *languageObjects.toTypedArray()))
    }

}

class ModlogEmbedFooter() : ModlogEmbedPart() {

    var languagePath: String = ""
    var languageObjects: MutableList<String> = ArrayList()

    var icon: String? = null

    constructor(languagePath: String, icon: String? = null, vararg languageObjects: String): this() {
        this.languagePath = languagePath
        this.languageObjects.addAll(languageObjects)
    }

    override fun build(locale: Locale, embedBuilder: WebhookEmbedBuilder) {
        embedBuilder.setFooter(WebhookEmbed.EmbedFooter(Language.i18n(locale, languagePath, *languageObjects.toTypedArray()), icon));
    }

}