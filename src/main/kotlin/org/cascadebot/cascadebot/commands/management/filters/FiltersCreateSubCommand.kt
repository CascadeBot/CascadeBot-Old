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
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.FormatUtils

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

        // Args in the format "<name> [type]"

        // Args in the format "<name> [type]"
        if (context.args.size == 1) {
            val filter = CommandFilter(name)
            context.data.management.filters.add(filter)
            context.typedMessaging.replySuccess(context.i18n(
                    "commands.filters.create.created_filter",
                    FormatUtils.formatEnum(filter.type, context.locale),
                    filter.name
            ))
        } else {
            val type = context.getArg(1)
            if (EnumUtils.isValidEnumIgnoreCase(FilterType::class.java, type)) {
                val filter = CommandFilter(name)
                filter.type = EnumUtils.getEnumIgnoreCase(FilterType::class.java, type)
                context.data.management.filters.add(filter)
                context.typedMessaging.replySuccess(context.i18n(
                        "commands.filters.create.created_filter",
                        FormatUtils.formatEnum(filter.type, context.locale),
                        filter.name
                ))
            } else {
                context.typedMessaging.replyDanger(context.i18n(
                        "commands.filters.create.type_invalid",
                        type,
                        FilterType.values().joinToString(", ") { filterType -> "`" + FormatUtils.formatEnum(filterType, context.locale) + "`" }
                ))
            }
        }
    }

    override fun command(): String = "create"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.create", false)

}