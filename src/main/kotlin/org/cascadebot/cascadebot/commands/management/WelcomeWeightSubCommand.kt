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
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import kotlin.math.round
import kotlin.math.roundToInt

class WelcomeWeightSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.size == 1 || context.args.size > 2) {
            context.uiMessaging.replyUsage()
            return
        }

        if (context.args.isEmpty()) {
            val welcomeMessages = context.data.management.greetings.welcomeMessages
            val pages = mutableListOf<Page>()
            for (item in welcomeMessages.itemsAndWeighting) {
                pages.add(PageObjects.EmbedPage(embed(MessageType.INFO, context.user) {
                    title { name = context.i18n("commands.welcome.weight.embed_title") }
                    field {
                        name = context.i18n("commands.welcome.weight.embed_message")
                        value = item.first
                    }
                    field {
                        name = context.i18n("commands.welcome.weight.embed_weight")
                        value = item.second.toString()
                        inline = true
                    }
                    field {
                        name = context.i18n("commands.welcome.weight.proportion_title")
                        value = round((item.second.toDouble() / welcomeMessages.totalWeight.toDouble()) * 100).toInt().toString() + "%"
                        inline = true
                    }
                }))
            }
            context.uiMessaging.sendPagedMessage(pages)
        } else {
            val welcomeMessages = context.data.management.greetings.welcomeMessages
            if (welcomeMessages.size == 0) {
                context.typedMessaging.replyDanger(context.i18n("commands.welcome.no_messages"))
                return
            }

            if (!context.isArgInteger(0) || !context.isArgInteger(1)) {
                context.typedMessaging.replyDanger(context.i18n("commands.welcome.weight.input_whole"))
                return
            }

            val index = context.getArgAsInteger(0)!! - 1
            val weight = context.getArgAsInteger(1)!!

            if (index < 0 || index >= welcomeMessages.size) {
                context.typedMessaging.replyDanger(context.i18n("commands.welcome.invalid_message_index", welcomeMessages.size))
                return
            }

            if (weight < 1) {
                context.typedMessaging.replyDanger(context.i18n("commands.welcome.weight.weight_range"))
                return
            }

            val oldWeight = welcomeMessages.getItemWeight(index)

            if (oldWeight == weight) {
                context.typedMessaging.replyInfo(embed(MessageType.INFO, context.user) {
                    title { name = context.i18n("commands.welcome.weight.same_weight_title") }
                    description = context.i18n("commands.welcome.weight.same_weight_text", weight, welcomeMessages[index].first)
                    field {
                        name = context.i18n("commands.welcome.weight.proportion_title")
                        value = context.i18n("commands.welcome.weight.proportion_text", (welcomeMessages.getItemProportion(index) * 100).roundToInt())
                    }
                })
                return
            }

            welcomeMessages.setItemWeight(index, weight)

            context.typedMessaging.replySuccess(embed(MessageType.SUCCESS, context.user) {
                title {
                    name = context.i18n("commands.welcome.weight.set_weight_title")
                }
                description = context.i18n("commands.welcome.weight.set_weight_text", welcomeMessages[index].first)
                field {
                    name = context.i18n("commands.welcome.weight.old_weight")
                    value = oldWeight.toString()
                    inline = true
                }
                field {
                    name = context.i18n("commands.welcome.weight.new_weight")
                    value = weight.toString()
                    inline = true
                }
                field {
                    name = context.i18n("commands.welcome.weight.proportion_title")
                    value = context.i18n("commands.welcome.weight.proportion_text", (welcomeMessages.getItemProportion(index) * 100).roundToInt())
                }
            })

        }
    }

    override fun command(): String = "weight"

    override fun parent(): String = "welcome"

    override fun permission(): CascadePermission? = CascadePermission.of("welcome.add", false)

}