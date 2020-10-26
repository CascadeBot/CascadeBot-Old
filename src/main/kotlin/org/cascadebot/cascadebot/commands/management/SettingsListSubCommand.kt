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
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import java.lang.reflect.Field

class SettingsListSubCommand(private val settingsClasses: List<Class<*>>) : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        val pages = mutableListOf<Page>()
        for (settingsClass in settingsClasses) {
            var descriptionStr = "";
            val entries = SettingsCommand.getSettingsFromClass(settingsClass).entries

            if (entries.isEmpty()) continue

            entries
                    .sortedBy { it.key }
                    .map { it.value }
                    .forEach { f: Field ->
                        val settingsContainer = SettingsCommand.getSettingsContainer(f, context)
                        if (f.canAccess(settingsContainer)) {
                            val settingValue = f.get(settingsContainer);
                            descriptionStr += if (settingValue is Boolean) {
                                if (settingValue) {
                                    "${f.name}: ${context.globalEmote("tick")} \n"
                                } else {
                                    "${f.name}: ${context.globalEmote("cross")} \n"
                                }
                            } else {
                                "${f.name}: `${settingValue}` \n"
                            }
                        }
                    }

            pages.add(PageObjects.EmbedPage(embed(MessageType.INFO) {
                title {
                    name = context.i18n("commands.settings.section_title", StringUtils.capitalize(FormatUtils.formatEnum<Module>(settingsClass.getAnnotation(SettingsContainer::class.java).module, context.locale)))
                }
                description = descriptionStr
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