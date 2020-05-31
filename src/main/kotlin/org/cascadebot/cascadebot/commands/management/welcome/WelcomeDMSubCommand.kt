/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.welcome

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.ConfirmUtils
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import kotlin.math.round
import kotlin.math.roundToInt

class WelcomeDMSubCommand : SubCommand() {

    val actionKey = "dm-welcome-clear"

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            TODO("Show welcome commands")
        }

        val subContext = context.copy(args = context.args.copyOfRange(1, context.args.size))
        when {
            context.testForArg("add") -> add(subContext)
            context.testForArg("clear") -> clear(subContext)
            context.testForArg("remove") -> remove(subContext)
            context.testForArg("weight") -> weight(subContext)
            else -> context.uiMessaging.replyUsage()
        }
    }

    override fun command(): String = "dm"

    override fun parent(): String = "welcome"

    override fun permission(): CascadePermission? = CascadePermission.of("welcome.dm", false)

    private fun add(context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val message = context.getMessage(0)

        val welcomeMessages = context.data.management.greetings.welcomeDMMessages
        welcomeMessages.add(message)

        val index = welcomeMessages.indexOf(message)
        val proportion = welcomeMessages.getItemProportion(index)

        context.typedMessaging.replySuccess(embed(MessageType.SUCCESS) {
            title {
                name = context.i18n("commands.welcome.dm.add.success_title")
            }
            description = "${context.i18n("commands.welcome.dm.add.success_text_1")}\n" +
                    "```\n$message\n```\n" +
                    context.i18n("commands.welcome.dm.add.success_text_2", welcomeMessages.size, FormatUtils.round(proportion * 100, 0).toInt())
        })
    }

    private fun weight(context: CommandContext) {
        if (context.args.size == 1 || context.args.size > 2) {
            context.uiMessaging.replyUsage()
            return
        }

        if (context.args.isEmpty()) {
            val welcomeMessages = context.data.management.greetings.welcomeMessages
            val pages = mutableListOf<Page>()
            for (item in welcomeMessages.itemsAndWeighting) {
                pages.add(PageObjects.EmbedPage(embed(MessageType.INFO, context.user) {
                    title { name = context.i18n("commands.welcome.dm.weight.embed_title") }
                    field {
                        name = context.i18n("commands.welcome.embed_message")
                        value = item.item
                    }
                    field {
                        name = context.i18n("commands.welcome.embed_weight")
                        value = item.weight.toString()
                        inline = true
                    }
                    field {
                        name = context.i18n("commands.welcome.proportion_title")
                        value = round((item.weight.toDouble() / welcomeMessages.totalWeight.toDouble()) * 100).toInt().toString() + "%"
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
                context.typedMessaging.replyDanger(context.i18n("commands.welcome.invalid_message_index",  welcomeMessages.size))
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
                    description = context.i18n("commands.welcome.weight.same_weight_text", weight, welcomeMessages[index].item
                            ?: "Message unavailable!")
                    field {
                        name = context.i18n("commands.welcome.proportion_title")
                        value = context.i18n("commands.welcome.weight.proportion_text",  (welcomeMessages.getItemProportion(index) * 100).roundToInt())
                    }
                })
                return
            }

            welcomeMessages.setItemWeight(index, weight)

            context.typedMessaging.replySuccess(embed(MessageType.SUCCESS, context.user) {
                title {
                    name = context.i18n(TODO("commands.welcome.weight.set_weight_title"))
                }
                description = context.i18n(TODO("commands.welcome.weight.set_weight_text"),  welcomeMessages[index].item ?: "Message unavailable!")
                field {
                    name = context.i18n(TODO("commands.welcome.weight.old_weight"))
                    value = oldWeight.toString()
                    inline = true
                }
                field {
                    name = context.i18n(TODO("commands.welcome.weight.new_weight"))
                    value = weight.toString()
                    inline = true
                }
                field {
                    name = context.i18n(TODO("commands.welcome.proportion_title"))
                    value = context.i18n(TODO("commands.welcome.weight.proportion_text"),  (welcomeMessages.getItemProportion(index) * 100).roundToInt())
                }
            })

        }
    }

    private fun remove(context: CommandContext) {
        if (context.args.size != 1) {
            context.uiMessaging.replyUsage()
            return
        }

        if (!context.isArgInteger(0)) {
            context.typedMessaging.replyDanger(context.i18n("commands.welcome.remove.message_index_number"))
            return
        }

        val index = context.getArgAsInteger(0)!! - 1
        val welcomeMessages = context.data.management.greetings.welcomeMessages
        if (index < 0 || index >= welcomeMessages.size) {
            context.typedMessaging.replyDanger(context.i18n("commands.welcome.invalid_message_index", welcomeMessages.size))
            return
        }

        val message = welcomeMessages.remove(index)

        context.typedMessaging.replySuccess(embed(MessageType.INFO, context.user) {
            title {
                name = context.i18n("commands.welcome.dm.remove.success_title")
            }
            description = context.i18n("commands.welcome.remove.success_text", message ?: "Message unavailable!")
        })
    }

    private fun clear(context: CommandContext) {
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
                context.i18n("commands.welcome.dm.clear.confirm_warning"),
                true,
                object : ConfirmUtils.ConfirmRunnable() {
                    override fun execute() {
                        context.data.management.greetings.welcomeMessages.clear()
                        context.data.management.greetings.welcomeChannel = null
                        context.typedMessaging.replySuccess(context.i18n("commands.welcome.dm.clear.clear_success"))
                    }
                }
        )
    }

}
