/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class FiltersUsersSubCommand : DeprecatedSubCommand() {

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
        val filter = context.data.management.filters.find { it.name == filterName }

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

                val user: User? = DiscordUtils.getUser(context.guild, args[1], true)

                if (user == null) {
                    context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_user_matching", args[1]))
                    return
                }

                if (filter.userIds.add(user.idLong)) {
                    context.typedMessaging.replySuccess(context.i18n("commands.filters.users.link.success", user.asMention, filterName))
                } else {
                    context.typedMessaging.replyInfo(context.i18n("commands.filters.users.link.already_exists", user.asMention, filterName))
                }
            }
            context.testForArg("unlink") -> {
                if (args.size < 2) {
                    context.uiMessaging.replyUsage()
                    return
                }

                val user: User? = DiscordUtils.getUser(context.guild, args[1], true)

                if (user == null) {
                    context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_user_matching", args[1]))
                    return
                }

                if (filter.userIds.remove(user.idLong)) {
                    context.typedMessaging.replySuccess(context.i18n("commands.filters.users.unlink.success", user.asMention, filterName))
                } else {
                    context.typedMessaging.replyDanger(context.i18n("commands.filters.users.unlink.didnt_exist", user.asMention, filterName))
                }
            }

            context.testForArg("list") -> {
                context.typedMessaging.replyInfo(embed(MessageType.INFO) {
                    title {
                        name = context.i18n("commands.filters.users.list.embed_title", filterName)
                    }
                    description = filter.userIds
                            .mapNotNull { context.guild.getMemberById(it)?.asMention }
                            .joinToString("\n")
                            .ifBlank { context.i18n("commands.filters.users.list.no_filters") }
                })
            }
            else -> {
                context.uiMessaging.replyUsage()
            }
        }
    }

    override fun command(): String = "users"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.users", false)

}