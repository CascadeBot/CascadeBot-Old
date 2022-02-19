/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildFilterEntity
import org.cascadebot.cascadebot.data.entities.GuildFilterId
import org.cascadebot.cascadebot.data.objects.CommandFilter
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.language.LanguageUtils
import org.cascadebot.cascadebot.utils.toTitleCase

class FiltersOperatorSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val filterName = context.getArg(0)
        val filter = context.getDataObject(GuildFilterEntity::class.java, GuildFilterId(filterName, context.getGuildId()))

        if (filter == null) {
            context.typedMessaging.replyDanger(context.i18n("commands.filters.doesnt_exist", filterName))
            return
        }

        val operator: CommandFilter.FilterOperator? = LanguageUtils.findEnumByI18n(CommandFilter.FilterOperator::class.java, context.locale, context.getArg(1), true)

        if (operator == null) {
            val validTypes = CommandFilter.FilterOperator.values().joinToString(", ") { "`${it.name.toTitleCase()}`" }
            context.typedMessaging.replyDanger(context.i18n("commands.filters.operator.invalid_operator", validTypes, filterName))
            return
        }

        if (operator == filter.operator) {
            context.typedMessaging.replyInfo(context.i18n("commands.filters.operator.already_set", FormatUtils.formatEnum(operator, context.locale), filterName))
            return
        }

        filter.operator = operator
        context.typedMessaging.replySuccess(context.i18n("commands.filters.operator.success", FormatUtils.formatEnum(operator, context.locale), filterName))

        context.saveDataObject(filter)
    }

    override fun command(): String  = "operator"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.operator", true)

}