/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import com.google.gson.JsonArray
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildSettingsCoreEntity
import org.cascadebot.cascadebot.data.entities.GuildSettingsManagementEntity
import org.cascadebot.cascadebot.data.entities.GuildSettingsModerationEntity
import org.cascadebot.cascadebot.data.language.Language.getLanguage
import org.cascadebot.cascadebot.data.objects.Setting
import org.cascadebot.cascadebot.data.objects.SettingsContainer
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.ParserUtils
import org.cascadebot.cascadebot.utils.ReflectionUtils
import java.io.IOException
import java.lang.reflect.Field
import java.util.Arrays
import java.util.stream.Collectors
import kotlin.math.min

class SettingsCommand : MainCommand() {
    companion object {
        private val settingsClasses: MutableList<Class<*>> = ArrayList()

        // This is theoretically safe because we will always create the values field to match this
        fun getSettingsFromClass(classForScanning: Class<*>): Map<String, Field> {
            try {
                val values = classForScanning.getField("VALUES")
                values.isAccessible = true
                if (values.type.isAssignableFrom(Map::class.java)) {
                    return values[null] as Map<String, Field>
                }
            } catch (ignored: NoSuchFieldException) {
                // If we can't get the values field, we'll loop through manually
            } catch (ignored: IllegalAccessException) {
            }
            val settings: MutableMap<String, Field> = HashMap()
            Arrays.stream(classForScanning.declaredFields)
                    .filter { field: Field -> field.getAnnotation(Setting::class.java) != null }
                    .forEach { setting: Field ->
                        setting.isAccessible = true
                        settings[setting.name.toLowerCase()] = setting
                    }
            return settings
        }

        private fun getAllSettings(settingClasses: List<Class<*>>): Map<String, Field> {
            return settingClasses.stream()
                    .map { classForScanning: Class<*> -> getSettingsFromClass(classForScanning) }
                    .flatMap { map: Map<String, Field> -> map.entries.stream() }
                    .collect(Collectors.toMap({ it.key }, { it.value }))
        }

        fun getSettingsContainer(field: Field, context: CommandContext): Any? {
            return when (field.declaringClass) {
                GuildSettingsCoreEntity::class.java -> context.getDataObject(GuildSettingsCoreEntity::class.java)
                GuildSettingsManagementEntity::class.java -> context.getDataObject(GuildSettingsManagementEntity::class.java)
                GuildSettingsModerationEntity::class.java -> context.getDataObject(GuildSettingsModerationEntity::class.java)
                else -> null
            }
        }

        init {
            try {
                ReflectionUtils.getClasses("org.cascadebot.cascadebot.data.entities").stream()
                    .filter { classToFilter: Class<*> -> classToFilter.getAnnotation(SettingsContainer::class.java) != null }
                    .forEach { e: Class<*> -> settingsClasses.add(e) }
            } catch (e: ClassNotFoundException) {
                CascadeBot.LOGGER.error("Could not load settings!", e)
            } catch (e: IOException) {
                CascadeBot.LOGGER.error("Could not load settings!", e)
            }
        }
    }

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            this.subCommands().find { it.command() == "list" }?.onCommand(sender, context)
        } else if (context.args.size == 2) {
            val field = getAllSettings(settingsClasses)[context.getArg(0).toLowerCase()]

            if (field != null) {
                val settingsContainer: Any = getSettingsContainer(field, context)
                    ?: throw UnsupportedOperationException("TODO") // TODO message

                try {
                    val settingAnnotation = field.getAnnotation(Setting::class.java)
                    if (settingAnnotation != null) {
                        /*if (!context.data.enabledFlags.containsAll(listOf(*settingAnnotation.flagRequired))) { TODO flags?
                            val niceName = i18n(context.guild.idLong, "settings." + field.declaringClass.getAnnotation(SettingsContainer::class.java).module.name.toLowerCase() + "." + field.name + ".nice_name")
                            context.typedMessaging.replyDanger(context.i18n("commands.settings.cannot_edit", niceName))
                            return
                        }*/
                    }
                    if (context.args.size != 2) {
                        context.uiMessaging.replyUsage()
                        return
                    }
                    var value = context.getArg(1)
                    when (field.type) {
                        Boolean::class.javaPrimitiveType -> {
                            val booleanValue = try {
                                ParserUtils.parseYesNo(context.locale, value)
                            } catch (e: IllegalArgumentException) {
                                val language = getLanguage(context.locale)!!
                                val yesWords: JsonArray = language.getArray("words.yes_words").orElse(JsonArray())
                                val noWords: JsonArray = language.getArray("words.no_words").orElse(JsonArray())
                                var validValues = ""
                                val loopLength = min(yesWords.size(), noWords.size());
                                for (i in 0 until loopLength) {
                                    validValues += "`${yesWords[i].asString}`/`${noWords[i].asString}`"
                                    // If not the end element, add a comma
                                    if (i < loopLength - 1) {
                                        validValues += ", "
                                    }
                                }
                                context.typedMessaging.replyDanger(context.i18n("commands.settings.invalid_boolean", value, validValues))
                                return
                            }
                            value = (if (booleanValue) context.globalEmote("tick") else context.globalEmote("cross")) ?: booleanValue.toString()
                            field.setBoolean(settingsContainer, booleanValue)
                        }
                        String::class.java -> {
                            field[settingsContainer] = value
                        }
                        else -> {
                            return
                        }
                    }
                    context.saveDataObject(settingsContainer)
                    context.typedMessaging.replySuccess(context.i18n("commands.settings.setting_set", field.name, value))
                } catch (e: IllegalAccessException) {
                    context.typedMessaging.replyException(context.i18n("commands.settings.cannot_access"), e)
                }
            } else {
                context.typedMessaging.replyDanger(context.i18n("commands.settings.cannot_find_field"))
            }
        } else {
            context.uiMessaging.replyUsage()
        }
    }

    override fun command(): String {
        return "settings"
    }

    override fun permission(): CascadePermission? {
        return CascadePermission.of("settings", false, Permission.MANAGE_SERVER)
    }

    override fun subCommands(): Set<SubCommand> {
        return java.util.Set.of(SettingsListSubCommand(settingsClasses))
    }

    override fun module(): Module {
        return Module.MANAGEMENT
    }
}
