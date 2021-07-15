package org.cascadebot.cascadebot.utils.interactions

import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.requests.ErrorResponse
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.commands.music.SkipCommand
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.interactions.CascadeButton
import org.cascadebot.cascadebot.utils.interactions.CascadeComponent
import org.cascadebot.cascadebot.utils.interactions.IButtonRunnable
import org.cascadebot.cascadebot.utils.interactions.InteractionMessage
import org.cascadebot.cascadebot.utils.votes.VoteGroup

enum class PersistentComponent(@field:Transient val component: CascadeComponent) {
    TODO_BUTTON_CHECK(CascadeButton.primary("Check", Emoji.fromUnicode(UnicodeConstants.TICK),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val todoList =
                GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
            if (!todoList.canUserEdit(runner.idLong)) {
                return@IButtonRunnable
            }
            val item = todoList.items[todoList.currentItem]
            item.done = true
            todoList.addUncheckButton(message.message)
            message.editMessage(todoList.todoListMessage).queue()
        })),
    TODO_BUTTON_UNCHECK(CascadeButton.primary("Uncheck", Emoji.fromUnicode(UnicodeConstants.WHITE_HALLOW_SQUARE),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val todoList =
                GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
            if (!todoList.canUserEdit(runner.idLong)) {
                return@IButtonRunnable
            }
            val item = todoList.items[todoList.currentItem]
            item.done = false
            todoList.addCheckButton(message.message)
            message.editMessage(todoList.todoListMessage).queue()
        })),
    TODO_BUTTON_NAVIGATE_LEFT(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.BACKWARD_ARROW),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val todoList =
                GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
            if (!todoList.canUserEdit(runner.idLong)) {
                return@IButtonRunnable
            }
            val currentPage = todoList.currentItem / 10 + 1
            val start = currentPage * 10 - 10
            if (start == 0) {
                return@IButtonRunnable
            }
            val newPos = Math.max(start - 10, 0)
            todoList.currentItem = newPos
            todoList.doCheckToggle(message.message)
            message.editMessage(todoList.todoListMessage).queue()
        })),
    TODO_BUTTON_NAVIGATE_RIGHT(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.FORWARD_ARROW),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val todoList =
                GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
            if (!todoList.canUserEdit(runner.idLong)) {
                return@IButtonRunnable
            }
            val currentPage = todoList.currentItem / 10 + 1
            val start = currentPage * 10 - 10
            val end = start + 9
            if (end + 1 >= todoList.items.size) {
                return@IButtonRunnable
            }
            todoList.currentItem = end + 1
            todoList.doCheckToggle(message.message)
            message.editMessage(todoList.todoListMessage).queue()
        })),
    TODO_BUTTON_NAVIGATE_UP(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.ARROW_UP),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val todoList =
                GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
            if (!todoList.canUserEdit(runner.idLong)) {
                return@IButtonRunnable
            }
            val newItem = todoList.currentItem - 1
            if (newItem < 0) {
                return@IButtonRunnable
            }
            todoList.currentItem = newItem
            todoList.doCheckToggle(message.message)
            message.editMessage(todoList.todoListMessage).queue()
        })),
    TODO_BUTTON_NAVIGATE_DOWN(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.ARROW_DOWN),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val todoList =
                GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
            if (!todoList.canUserEdit(runner.idLong)) {
                return@IButtonRunnable
            }
            val newItem = todoList.currentItem + 1
            if (newItem >= todoList.items.size) {
                return@IButtonRunnable
            }
            todoList.currentItem = newItem
            todoList.doCheckToggle(message.message)
            message.editMessage(todoList.todoListMessage).queue()
        })),
    VOTE_BUTTON_YES(CascadeButton.success(Emoji.fromUnicode(UnicodeConstants.TICK),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, UnicodeConstants.TICK)
        })),
    VOTE_BUTTON_NO(CascadeButton.danger(Emoji.fromUnicode(UnicodeConstants.RED_CROSS),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, UnicodeConstants.RED_CROSS)
        })),
    VOTE_BUTTON_ONE(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.ONE),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 1)
        })),
    VOTE_BUTTON_TWO(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.TWO),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 2)
        })),
    VOTE_BUTTON_THREE(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.THREE),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 3)
        })),
    VOTE_BUTTON_FOUR(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.FOUR),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 4)
        })),
    VOTE_BUTTON_FIVE(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.FIVE),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 5)
        })),
    VOTE_BUTTON_SIX(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.SIX),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 6)
        })),
    VOTE_BUTTON_SEVEN(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.SEVEN),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 7)
        })),
    VOTE_BUTTON_EIGHT(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.EIGHT),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 8)
        })),
    VOTE_BUTTON_NINE(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.NINE),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 9)
        })),
    VOTE_BUTTON_A(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.A),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 0)
        })),
    VOTE_BUTTON_B(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.B),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 1)
        })),
    VOTE_BUTTON_C(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.C),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 2)
        })),
    VOTE_BUTTON_D(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.D),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 3)
        })),
    VOTE_BUTTON_E(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.E),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 4)
        })),
    VOTE_BUTTON_F(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.F),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 5)
        })),
    VOTE_BUTTON_G(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.G),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 6)
        })),
    VOTE_BUTTON_H(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.H),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 7)
        })),
    VOTE_BUTTON_I(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.I),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 8)
        })),
    VOTE_BUTTON_J(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.J),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 9)
        })),
    VOTE_BUTTON_K(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.K),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 10)
        })),
    VOTE_BUTTON_L(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.L),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 11)
        })),
    VOTE_BUTTON_M(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.M),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 12)
        })),
    VOTE_BUTTON_N(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.N),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 13)
        })),
    VOTE_BUTTON_O(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.O),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, 14)
        })),
    SKIP_BUTTON_FORCE(CascadeButton.secondary("Force", Emoji.fromUnicode(UnicodeConstants.FAST_FORWARD),
        IButtonRunnable { runner: Member?, channel: TextChannel, message: InteractionMessage ->
            val data = GuildDataManager.getGuildData(channel.guild.idLong)
            if (!data.management.permissions.hasPermission(
                    runner,
                    channel,
                    CascadeBot.INS.permissionsManager.getPermission("skip.force"),
                    data.core
                )
            ) {
                return@IButtonRunnable
            }
            message.message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
            val voteButtonGroup = data.findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            voteButtonGroup!!.stopVote()
            CascadeBot.INS.musicHandler.getPlayer(channel.guild.idLong)!!.skip()
        }));

}
