/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.FormatUtils

class WelcomeWeightSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.size == 1 || context.args.size > 2) {
            context.uiMessaging.replyUsage()
            return
        }

        if (context.args.isEmpty()) {
            TODO("Weight info")
        } else {
            val welcomeMessages = context.data.management.greetings.welcomeMessages
            if (welcomeMessages.size == 0) {
                context.typedMessaging.replyDanger("There are no welcome messages for this server!")
                return
            }

            if (!context.isArgInteger(0) || !context.isArgInteger(1)) {
                context.typedMessaging.replyDanger("The message index and weight both need to be whole numbers!")
                return
            }

            val index = context.getArgAsInteger(0)!!
            val weight = context.getArgAsInteger(1)!!

            if (index < 1 || index > welcomeMessages.size) {
                context.typedMessaging.replyDanger("The message index must be between `1` and `${welcomeMessages.size}`")
                return
            }

            if (weight < 1) {
                context.typedMessaging.replyDanger("The weight must be 1 or more!")
                return
            }

            val oldWeight = welcomeMessages.getItemWeight(index)

            if (oldWeight == weight) {
                context.typedMessaging.replyInfo(embed(MessageType.INFO, context.user) {
                    title { name = "Message weight already the same!"}
                    description = "The message weight for the message below is already set to `$weight`!\n```\n${welcomeMessages[index].first}\n```"
                    field {
                        name = "Proportion"
                        value = "This message will be shown ${FormatUtils.round(welcomeMessages.getItemProportion(index) * 100, 0).toInt()}% of the time!"
                    }
                })
                return
            }

            welcomeMessages.setItemWeight(index, weight)

            context.typedMessaging.replySuccess(embed(MessageType.SUCCESS, context.user) {
                title {
                    name = "Set the message weight!"
                }
                description = "Successfully set the weight of the message: \n```\n${welcomeMessages[index].first}\n```"
                field {
                    name = "Old weight"
                    value = oldWeight.toString()
                    inline = true
                }
                field {
                    name = "New weight"
                    value = weight.toString()
                    inline = true
                }
                field {
                    name = "Proportion"
                    value = "This message will be shown ${FormatUtils.round(welcomeMessages.getItemProportion(index) * 100, 0).toInt()}% of the time!"
                }
            })

        }
    }

    override fun command(): String = "weight"

    override fun parent(): String = "welcome"

    override fun permission(): CascadePermission? = CascadePermission.of("welcome.add", false)

}