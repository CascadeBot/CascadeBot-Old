/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.goodbye

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.ConfirmUtils

class GoodbyeClearSubCommand : SubCommand() {

    private val actionKey = "goodbye-clear"

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isNotEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        if (ConfirmUtils.hasConfirmedAction(actionKey, context.user.idLong)) {
            ConfirmUtils.completeAction(actionKey, context.user.idLong)
            return
        }

        ConfirmUtils.confirmAction(context.user.idLong,
                actionKey,
                context.channel,
                MessageType.WARNING,
                context.i18n("commands.goodbye.clear.confirm_warning"),
                true,
                Runnable {
                    context.data.management.greetings.goodbyeMessages.clear()
                    context.typedMessaging.replySuccess(context.i18n("commands.goodbye.clear.clear_success"))
                }
        )

    }

    override fun command(): String = "clear"

    override fun parent(): String = "goodbye"

    override fun permission(): CascadePermission? = CascadePermission.of("goodbye.clear", false)

}