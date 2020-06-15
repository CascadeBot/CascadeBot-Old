/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.language.Language.i18n
import org.cascadebot.cascadebot.data.objects.GuildSettingsCore
import org.cascadebot.cascadebot.data.objects.GuildSettingsManagement
import org.cascadebot.cascadebot.data.objects.GuildSettingsModeration
import org.cascadebot.cascadebot.data.objects.GuildSettingsMusic
import org.cascadebot.cascadebot.data.objects.GuildSettingsUseful
import org.cascadebot.cascadebot.data.objects.ModlogEventStore
import org.cascadebot.cascadebot.data.objects.Setting
import org.cascadebot.cascadebot.data.objects.SettingsContainer
import org.cascadebot.cascadebot.moderation.ModlogEmbedField
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.ReflectionUtils
import java.io.IOException
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.stream.Collectors

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

        fun getSettingsContainer(field: Field, context: CommandContext): Any {
            return when (field.declaringClass) {
                GuildSettingsCore::class.java -> context.data.core
                GuildSettingsManagement::class.java -> context.data.management
                GuildSettingsModeration::class.java -> context.data.moderation
                GuildSettingsMusic::class.java -> context.data.music
                GuildSettingsUseful::class.java -> context.data.useful
                else -> context.data.core
            }
        }

        init {
            try {
                ReflectionUtils.getClasses("org.cascadebot.cascadebot.data.objects").stream().filter { classToFilter: Class<*> -> classToFilter.getAnnotation(SettingsContainer::class.java) != null }.forEach { e: Class<*> -> settingsClasses.add(e) }
            } catch (e: ClassNotFoundException) {
                CascadeBot.LOGGER.error("Could not load settings!", e)
            } catch (e: IOException) {
                CascadeBot.LOGGER.error("Could not load settings!", e)
            }
        }
    }

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.size == 2) {
            val field = getAllSettings(settingsClasses)[context.getArg(0).toLowerCase()]

            if (field != null) {
                val settingsContainer: Any = getSettingsContainer(field, context)

                try {
                    val settingAnnotation = field.getAnnotation(Setting::class.java)
                    if (settingAnnotation != null) {
                        if (!context.data.enabledFlags.containsAll(listOf(*settingAnnotation.flagRequired))) {
                            val niceName = i18n(context.guild.idLong, "settings." + field.declaringClass.getAnnotation(SettingsContainer::class.java).module.name.toLowerCase() + "." + field.name + ".nice_name")
                            context.typedMessaging.replyDanger(context.i18n("commands.settings.cannot_edit", niceName))
                            return
                        }
                    }
                    if (context.args.size != 2) {
                        context.uiMessaging.replyUsage()
                        return
                    }
                    val oldValue: String = field.get(settingsContainer).toString()
                    var value = context.getArg(1)
                    when (field.type) {
                        Boolean::class.javaPrimitiveType -> {
                            val booleanValue = java.lang.Boolean.parseBoolean(value)
                            value = booleanValue.toString()
                            field.setBoolean(settingsContainer, booleanValue)
                        }
                        String::class.java -> {
                            field[settingsContainer] = value
                        }
                        else -> {
                            return
                        }
                    }
                    context.typedMessaging.replySuccess(context.i18n("commands.settings.setting_set", field.name, value))
                    val embedFields: MutableList<ModlogEmbedPart> = ArrayList();
                    embedFields.add(ModlogEmbedField(true, "modlog.setting.old", "modlog.general.variable", oldValue))
                    embedFields.add(ModlogEmbedField(true, "modlog.setting.new", "modlog.general.variable", value))
                    context.data.moderation.sendModlogEvent(context.guild.idLong, ModlogEventStore(ModlogEvent.CASCADE_SETTINGS_UPDATED, sender.user, field, ArrayList()))
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
