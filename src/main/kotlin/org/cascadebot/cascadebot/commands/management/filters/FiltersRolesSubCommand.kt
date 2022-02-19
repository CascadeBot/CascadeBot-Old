/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildFilterEntity
import org.cascadebot.cascadebot.data.entities.GuildFilterId
import org.cascadebot.cascadebot.data.entities.GuildFilterRoleEntity
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class FiltersRolesSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val args = context.args.copyOfRange(1, context.args.size)

        if (args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val filterName = args[0]
        val filter = context.getDataObject(GuildFilterEntity::class.java, GuildFilterId(filterName, context.getGuildId()))

        if (filter == null) {
            context.typedMessaging.replyDanger(context.i18n("commands.filters.doesnt_exist", filterName))
            return
        }

        when {
            context.testForArg("link") -> {
                if (args.size < 2) {
                    context.uiMessaging.replyUsage()
                    return
                }

                val role: Role? = DiscordUtils.getRole(args[1], context.guild)

                if (role == null) {
                    context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_role_matching", args[1]))
                    return
                }

                if (filter.roles.add(GuildFilterRoleEntity(filter.name, filter.guildId, role.idLong))) {
                    context.typedMessaging.replySuccess(context.i18n("commands.filters.roles.link.success", role.asMention, filterName))
                } else {
                    context.typedMessaging.replyInfo(context.i18n("commands.filters.roles.link.already_exists", role.asMention, filterName))
                }
            }
            context.testForArg("unlink") -> {
                if (args.size < 2) {
                    context.uiMessaging.replyUsage()
                    return
                }

                val role: Role? = DiscordUtils.getRole(args[1], context.guild)

                if (role == null) {
                    context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_role_matching", args[1]))
                    return
                }

                if (filter.roles.remove(GuildFilterRoleEntity(filter.name, filter.guildId, role.idLong))) {
                    context.typedMessaging.replySuccess(context.i18n("commands.filters.roles.unlink.success", role.asMention, filterName))
                } else {
                    context.typedMessaging.replyDanger(context.i18n("commands.filters.roles.unlink.didnt_exist", role.asMention, filterName))
                }
            }

            context.testForArg("list") -> {
                context.typedMessaging.replyInfo(embed(MessageType.INFO) {
                    title {
                        name = context.i18n("commands.filters.roles.list.embed_title", filterName)
                    }
                    description = filter.roles
                            .mapNotNull { context.guild.getRoleById(it.roleId)?.asMention }
                            .joinToString("\n")
                            .ifBlank { context.i18n("commands.filters.roles.list.no_filters") }
                })
            }
            else -> {
                context.uiMessaging.replyUsage()
            }
        }
        context.saveDataObject(filter)
    }

    override fun command(): String = "roles"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.roles", false)

}