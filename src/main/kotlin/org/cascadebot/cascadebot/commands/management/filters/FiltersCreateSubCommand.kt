/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import org.apache.commons.lang3.EnumUtils
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.objects.CommandFilter
import org.cascadebot.cascadebot.data.objects.CommandFilter.FilterType
import org.cascadebot.cascadebot.data.objects.ModlogEventData
import org.cascadebot.cascadebot.moderation.ModlogEmbedField
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.FormatUtils
import org.checkerframework.checker.units.qual.s

class FiltersCreateSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty() || context.args.size > 2) {
            context.uiMessaging.replyUsage()
            return
        }

        val name = context.getArg(0)

        if (context.data.management.filters.any { it.name == name }) {
            context.typedMessaging.replyDanger(context.i18n(
                    "commands.filters.create.already_exists",
                    name
            ))
            return
        }

        // A null filter type means that it will not be set on the filter and the default will be used
        var type: FilterType? = null

        // Args in the format "<name> [type]"
        if (context.args.size == 2) {
            val typeInput = context.getArg(1)
            if (EnumUtils.isValidEnumIgnoreCase(FilterType::class.java, typeInput)) {
                type = EnumUtils.getEnumIgnoreCase(FilterType::class.java, typeInput)
            } else {
                context.typedMessaging.replyDanger(context.i18n(
                    "commands.filters.create.type_invalid",
                    typeInput,
                    FilterType.values().joinToString(", ") { filterType -> "`" + FormatUtils.formatEnum(filterType, context.locale) + "`" }
                ))
                return
            }
        }

        val commandFilter: CommandFilter = createFilter(name, type)

        context.data.management.filters.add(commandFilter)
        if (context.data.management.filters.size >= 10 && context.data.management.warnOver10Filters) {
            context.typedMessaging.replyWarning(context.i18n("commands.filters.create.over10warning"))
        }

        context.typedMessaging.replySuccess(context.i18n(
            "commands.filters.create.created_filter",
            FormatUtils.formatEnum(commandFilter.type, context.locale),
            commandFilter.name
        ))

        val embedFieldList = mutableListOf<ModlogEmbedPart>()

        embedFieldList.add(ModlogEmbedField(true, "words.type", null, FormatUtils.formatEnum(commandFilter.type, context.locale, true)))

        val eventStore = ModlogEventData(ModlogEvent.CASCADE_FILTER_CREATE, context.user, commandFilter, embedFieldList)
        context.data.moderation.sendModlogEvent(context.guild.idLong, eventStore)

    }

    private fun createFilter(name: String, type: FilterType?): CommandFilter {
        val filter = CommandFilter(name)
        if (type != null) {
            filter.type = type
        }
        return filter
    }

    override fun command(): String = "create"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.create", false)

}