/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import org.apache.commons.lang3.EnumUtils
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildFilterEntity
import org.cascadebot.cascadebot.data.entities.GuildFilterId
import org.cascadebot.cascadebot.data.entities.GuildSettingsManagementEntity
import org.cascadebot.cascadebot.data.entities.GuildSettingsModerationEntity
import org.cascadebot.cascadebot.data.objects.CommandFilter
import org.cascadebot.cascadebot.data.objects.CommandFilter.FilterType
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.count

class FiltersCreateSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty() || context.args.size > 2) {
            context.uiMessaging.replyUsage()
            return
        }

        val name = context.getArg(0)

        val filter = context.getDataObject(GuildFilterEntity::class.java, GuildFilterId(name, context.getGuildId()))
        if (filter != null) {
            context.typedMessaging.replyDanger(context.i18n(
                    "commands.filters.create.already_exists",
                    name
            ))
            return
        }

        // Args in the format "<name> [type]"
        if (context.args.size == 1) {
            val filter = GuildFilterEntity(name, context.getGuildId(), FilterType.BLACKLIST, CommandFilter.FilterOperator.OR)
            context.saveDataObject(filter)
            val amount = context.transaction {
                count(GuildFilterEntity::class.java, "guild_id", context.getGuildId())
            }
                ?: throw UnsupportedOperationException("TODO") //TODO message
            val managementSettings = context.getDataObject(GuildSettingsManagementEntity::class.java)
                ?: throw UnsupportedOperationException("TODO") //TODO message

            if (amount >= 10 && managementSettings.warnOver10) {
                context.typedMessaging.replyWarning(context.i18n("commands.filters.create.over10warning"))
            }
            context.typedMessaging.replySuccess(context.i18n(
                    "commands.filters.create.created_filter",
                    FormatUtils.formatEnum(filter.type, context.locale),
                    filter.name
            ))
        } else {
            val type = context.getArg(1)
            if (EnumUtils.isValidEnumIgnoreCase(FilterType::class.java, type)) {
                val filter = GuildFilterEntity(name, context.getGuildId(), FilterType.BLACKLIST, CommandFilter.FilterOperator.OR)
                filter.type = EnumUtils.getEnumIgnoreCase(FilterType::class.java, type)
                val amount = context.transaction {
                    count(GuildFilterEntity::class.java, "guild_id", context.getGuildId())
                }
                    ?: throw UnsupportedOperationException("TODO") //TODO message
                val managementSettings = context.getDataObject(GuildSettingsManagementEntity::class.java)
                    ?: throw UnsupportedOperationException("TODO") //TODO message

                if (amount >= 10 && managementSettings.warnOver10) {
                    context.typedMessaging.replyWarning(context.i18n("commands.filters.create.over10warning"))
                }
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