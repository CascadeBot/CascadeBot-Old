package org.cascadebot.cascadebot.data.language

import com.ibm.icu.text.MessageFormat
import io.github.binaryoverload.JSONConfig
import net.dv8tion.jda.internal.utils.Checks
import org.apache.commons.lang3.ArrayUtils
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.ShutdownHandler
import org.cascadebot.cascadebot.data.entities.GuildSettingsCoreEntity
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.utils.FormatUtils
import java.util.EnumMap

object Language {

    private val languages: MutableMap<Locale, JSONConfig> = EnumMap(Locale::class.java)

    init {
        try {
            loadLanguage(Locale.ENGLISH_UK)
        } catch (e: Exception) {
            CascadeBot.LOGGER.error("Could not load language!", e)
            ShutdownHandler.exitWithError()
        }
        CascadeBot.LOGGER.info("Loaded {} languages!", languages.size)
    }

    private fun loadLanguage(locale: Locale) {
        val stream = locale.languageFile
        if (stream == null) {
            if (locale == Locale.getDefaultLocale()) {
                CascadeBot.LOGGER.error("I couldn't load the default language file {} from the JAR file, stopping the bot!", locale.languageFileName)
                ShutdownHandler.exitWithError()
            } else {
                CascadeBot.LOGGER.warn("I couldn't load the language file {} from the JAR file!", locale.languageFileName)
            }
        } else {
            val config = JSONConfig(stream)
            config.allowedSpecialCharacters = ArrayUtils.add(config.allowedSpecialCharacters, '#')
            languages[locale] = config
        }
    }

    @JvmStatic
    fun getLanguages(): Map<Locale, JSONConfig> {
        return languages.toMap()
    }

    @JvmStatic
    fun getLanguage(locale: Locale): JSONConfig? {
        return languages[locale]
    }

    @JvmStatic
    fun hasLanguageEntry(locale: Locale, path: String?): Boolean {
        return languages.containsKey(locale) && languages[locale]!!.getString(path).isPresent
    }

    @JvmStatic
    fun i18n(guildId: Long, path: String, vararg args: Any): String {
        val locale = getGuildLocale(guildId)
        return if (languages.containsKey(locale)) {
            if (languages[locale]!!.getString(path).isPresent) {
                val format = MessageFormat(languages[locale]!!.getString(path).get(), locale.uLocale)
                var message = format.format(args)
                message = FormatUtils.formatPrefix(";", message)
                FormatUtils.formatUnicode(message)
            } else {
                CascadeBot.LOGGER.warn("Cannot find a language string matching the path '{}'", path)
                if (languages[Locale.getDefaultLocale()]!!.getString(path).isPresent) i18n(Locale.getDefaultLocale(), path, *args) else "No language string for $path"
            }
        } else {
            throw IllegalStateException("The language file matching locale '" + locale.languageCode
                    + "' does not exist or is not loaded!")
        }
    }

    @JvmStatic
    fun getGuildLocale(guildId: Long): Locale {
        val coreSettings = CascadeBot.INS.postgresManager.transaction {
            return@transaction get(GuildSettingsCoreEntity::class.java, guildId)
        } ?: throw UnsupportedOperationException("This shouldn't happen")
        return coreSettings.locale
    }

    @JvmStatic
    fun i18n(locale: Locale, path: String, vararg args: Any): String {
        Checks.notNull(locale, "locale")
        return if (languages.containsKey(locale)) {
            if (languages[locale]!!.getString(path).isPresent) {
                val format = MessageFormat(languages[locale]!!.getString(path).get(), locale.uLocale)
                FormatUtils.formatUnicode(format.format(args))
            } else {
                CascadeBot.LOGGER.warn("Cannot find a language string matching the path '{}'", path)
                if (languages[Locale.getDefaultLocale()]!!.getString(path).isPresent) i18n(Locale.getDefaultLocale(), path, *args) else "No language string for $path"
            }
        } else {
            throw IllegalStateException("The language file matching locale '" + locale.languageCode
                    + "' does not exist or is not loaded!")
        }
    }
}