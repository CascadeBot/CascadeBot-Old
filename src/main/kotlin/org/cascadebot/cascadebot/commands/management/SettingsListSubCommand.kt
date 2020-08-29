/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import org.apache.commons.lang3.StringUtils
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.objects.SettingsContainer
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.PasteUtils
import org.cascadebot.cascadebot.utils.Table.TableBuilder
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import java.lang.reflect.Field

class SettingsListSubCommand(private val settingsClasses: List<Class<*>>) : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        val pages = mutableListOf<Page>()
        for (settingsClass in settingsClasses) {
            val tableBuilder = TableBuilder(context.i18n("commands.settings.setting"), context.i18n("commands.settings.current_value"))
            val entries = SettingsCommand.getSettingsFromClass(settingsClass).entries

            if (entries.isEmpty()) continue

            entries.stream()
                    .sorted(java.util.Map.Entry.comparingByKey())
                    .map { it.value }
                    .forEach { f: Field ->
                        try {
                            tableBuilder.addRow(f.name, f.get(SettingsCommand.getSettingsContainer(f, context)).toString())
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        }
                    }

            pages.add(PageObjects.EmbedPage(embed(MessageType.INFO) {
                title {
                    name = context.i18n("commands.settings.section_title", StringUtils.capitalize(FormatUtils.formatEnum<Module>(settingsClass.getAnnotation(SettingsContainer::class.java).module, context.locale)))
                }
                description = tableBuilder.build().toString()
            }));
        }
        context.uiMessaging.sendPagedMessage(pages)
    }

    override fun command(): String {
        return "list"
    }

    override fun parent(): String {
        return "settings"
    }

    override fun permission(): CascadePermission? {
        return null
    }

}