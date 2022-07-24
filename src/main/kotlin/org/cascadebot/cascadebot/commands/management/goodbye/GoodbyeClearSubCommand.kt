/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.goodbye

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildGreetingEntity
import org.cascadebot.cascadebot.data.objects.GreetingType
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.utils.ConfirmUtils
import org.cascadebot.cascadebot.utils.deleteById

class GoodbyeClearSubCommand : SubCommand() {

    private val actionKey = "goodbye-clear"

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isNotEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        if (ConfirmUtils.hasRegisteredAction(actionKey, context.user.idLong)) {
            ConfirmUtils.confirmAction(actionKey, context.user.idLong)
            return
        }

        ConfirmUtils.registerForConfirmation(context.user.idLong,
                actionKey,
                context.channel,
                MessageType.WARNING,
                context.i18n("commands.goodbye.clear.confirm_warning"),
                isCancellable = true,
                action = {
                    context.transaction {
                        deleteById(GuildGreetingEntity::class.java,
                            mapOf(Pair("guild_id", context.getGuildId()), Pair("type", GreetingType.GOODBYE)))
                    }
                    context.typedMessaging.replySuccess(context.i18n("commands.goodbye.clear.clear_success"))
                }
        )

    }

    override fun command(): String = "clear"

    override fun parent(): String = "goodbye"

}