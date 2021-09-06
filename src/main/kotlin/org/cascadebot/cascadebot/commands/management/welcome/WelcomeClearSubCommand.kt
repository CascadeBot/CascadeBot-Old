/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.welcome

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.ConfirmUtils

class WelcomeClearSubCommand : DeprecatedSubCommand() {

    private val actionKey = "welcome-clear"

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
                context.i18n("commands.welcome.clear.confirm_warning"),
                isCancellable = true,
                action = Runnable {
                    context.data.management.greetings.welcomeMessages.clear()
                    context.data.management.greetings.welcomeChannel = null
                    context.typedMessaging.replySuccess(context.i18n("commands.welcome.clear.clear_success"))
                }
        )

    }

    override fun command(): String = "clear"

    override fun parent(): String = "welcome"

    override fun permission(): CascadePermission? = CascadePermission.of("welcome.clear", false)

}