package org.cascadebot.cascadebot.moderation

import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale

sealed class ModlogEmbedPart {

    abstract fun build(locale: Locale, embedBuilder: WebhookEmbedBuilder)

}

class ModlogEmbedField(val inline: Boolean = true,
                       val titleLanguagePath: String,
                       var valueLanguagePath: String? = null, vararg valueLanguageObjects: Any) : ModlogEmbedPart() {

    constructor() : this(true, "", "")

    val titleLanguageObjects: MutableList<Any> = mutableListOf()
    val valueLanguageObjects: MutableList<Any> = mutableListOf(*valueLanguageObjects)

    fun addTitleObjects(vararg titleLanguageObjects: Any) {
        this.titleLanguageObjects.addAll(titleLanguageObjects)
    }

    fun addValueObjects(vararg valueLanguageObjects: Any) {
        this.valueLanguageObjects.addAll(valueLanguageObjects)
    }

    override fun build(locale: Locale, embedBuilder: WebhookEmbedBuilder) {
        embedBuilder.addField(WebhookEmbed.EmbedField(inline,
                Language.i18n(locale, titleLanguagePath, *titleLanguageObjects.toTypedArray()),
                Language.i18n(locale, valueLanguagePath ?: "modlog.general.variable", *valueLanguageObjects.toTypedArray())))
    }

}

class ModlogEmbedDescription(val languagePath: String, vararg languageObjects: String) : ModlogEmbedPart() {

    constructor() : this("")

    var languageObjects: MutableList<String> = mutableListOf(*languageObjects)

    override fun build(locale: Locale, embedBuilder: WebhookEmbedBuilder) {
        embedBuilder.setDescription(Language.i18n(locale, languagePath, *languageObjects.toTypedArray()))
    }

}

class ModlogEmbedFooter(val languagePath: String, val icon: String? = null, vararg languageObjects: String) : ModlogEmbedPart() {

    constructor() : this("", "")

    var languageObjects: MutableList<String> = mutableListOf(*languageObjects)

    override fun build(locale: Locale, embedBuilder: WebhookEmbedBuilder) {
        embedBuilder.setFooter(WebhookEmbed.EmbedFooter(Language.i18n(locale, languagePath, *languageObjects.toTypedArray()), icon));
    }

}