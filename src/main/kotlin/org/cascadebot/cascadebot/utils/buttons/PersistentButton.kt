package org.cascadebot.cascadebot.utils.buttons

import de.bild.codec.annotations.Transient
import lombok.Getter
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.requests.ErrorResponse
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.commands.music.SkipCommand
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.votes.VoteButtonGroup

enum class PersistentButton(@field:Transient val button: Button) {
    TODO_BUTTON_CHECK(Button.UnicodeButton(UnicodeConstants.TICK, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).usefulSettings.getTodoListByMessage(message.idLong)
        if (!todoList.canUserEdit(runner.idLong)) {
            return@label
        }
        val item = todoList.items[todoList.currentItem]
        item.isDone = true
        todoList.addUncheckButton(message)
        message.editMessage(todoList.todoListMessage).queue()
    })),
    TODO_BUTTON_UNCHECK(Button.UnicodeButton(UnicodeConstants.WHITE_HALLOW_SQUARE, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).usefulSettings.getTodoListByMessage(message.idLong)
        if (!todoList.canUserEdit(runner.idLong)) {
            return@label
        }
        val item = todoList.items[todoList.currentItem]
        item.isDone = false
        todoList.addCheckButton(message)
        message.editMessage(todoList.todoListMessage).queue()
    })),
    TODO_BUTTON_NAVIGATE_LEFT(Button.UnicodeButton(UnicodeConstants.BACKWARD_ARROW, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).usefulSettings.getTodoListByMessage(message.idLong)
        if (!todoList.canUserEdit(runner.idLong)) {
            return@label
        }
        val currentPage = todoList.currentItem / 10 + 1
        val start = currentPage * 10 - 10
        if (start == 0) {
            return@label
        }
        val newPos = Math.max(start - 10, 0)
        todoList.currentItem = newPos
        message.editMessage(todoList.todoListMessage).queue()
        todoList.doCheckToggle(message)
    })),
    TODO_BUTTON_NAVIGATE_RIGHT(Button.UnicodeButton(UnicodeConstants.FORWARD_ARROW, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).usefulSettings.getTodoListByMessage(message.idLong)
        if (!todoList.canUserEdit(runner.idLong)) {
            return@label
        }
        val currentPage = todoList.currentItem / 10 + 1
        val start = currentPage * 10 - 10
        val end = start + 9
        if (end + 1 >= todoList.items.size) {
            return@label
        }
        todoList.currentItem = end + 1
        message.editMessage(todoList.todoListMessage).queue()
        todoList.doCheckToggle(message)
    })),
    TODO_BUTTON_NAVIGATE_UP(Button.UnicodeButton(UnicodeConstants.ARROW_UP, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).usefulSettings.getTodoListByMessage(message.idLong)
        if (!todoList.canUserEdit(runner.idLong)) {
            return@label
        }
        val newItem = todoList.currentItem - 1
        if (newItem < 0) {
            return@label
        }
        todoList.currentItem = newItem
        message.editMessage(todoList.todoListMessage).queue()
        todoList.doCheckToggle(message)
    })),
    TODO_BUTTON_NAVIGATE_DOWN(Button.UnicodeButton(UnicodeConstants.ARROW_DOWN, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).usefulSettings.getTodoListByMessage(message.idLong)
        if (!todoList.canUserEdit(runner.idLong)) {
            return@label
        }
        val newItem = todoList.currentItem + 1
        if (newItem >= todoList.items.size) {
            return@label
        }
        todoList.currentItem = newItem
        message.editMessage(todoList.todoListMessage).queue()
        todoList.doCheckToggle(message)
    })),
    VOTE_BUTTON_YES(Button.UnicodeButton(UnicodeConstants.TICK, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, UnicodeConstants.TICK)
    })),
    VOTE_BUTTON_NO(Button.UnicodeButton(UnicodeConstants.RED_CROSS, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, UnicodeConstants.RED_CROSS)
    })),
    VOTE_BUTTON_ONE(Button.UnicodeButton(UnicodeConstants.ONE, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 1)
    })),
    VOTE_BUTTON_TWO(Button.UnicodeButton(UnicodeConstants.TWO, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 2)
    })),
    VOTE_BUTTON_THREE(Button.UnicodeButton(UnicodeConstants.THREE, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 3)
    })),
    VOTE_BUTTON_FOUR(Button.UnicodeButton(UnicodeConstants.FOUR, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 4)
    })),
    VOTE_BUTTON_FIVE(Button.UnicodeButton(UnicodeConstants.FIVE, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 5)
    })),
    VOTE_BUTTON_SIX(Button.UnicodeButton(UnicodeConstants.SIX, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 6)
    })),
    VOTE_BUTTON_SEVEN(Button.UnicodeButton(UnicodeConstants.SEVEN, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 7)
    })),
    VOTE_BUTTON_EIGHT(Button.UnicodeButton(UnicodeConstants.EIGHT, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 8)
    })),
    VOTE_BUTTON_NINE(Button.UnicodeButton(UnicodeConstants.NINE, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 9)
    })),
    VOTE_BUTTON_A(Button.UnicodeButton(UnicodeConstants.A, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 0)
    })),
    VOTE_BUTTON_B(Button.UnicodeButton(UnicodeConstants.B, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 1)
    })),
    VOTE_BUTTON_C(Button.UnicodeButton(UnicodeConstants.C, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 2)
    })),
    VOTE_BUTTON_D(Button.UnicodeButton(UnicodeConstants.D, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 3)
    })),
    VOTE_BUTTON_E(Button.UnicodeButton(UnicodeConstants.E, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 4)
    })),
    VOTE_BUTTON_F(Button.UnicodeButton(UnicodeConstants.F, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 5)
    })),
    VOTE_BUTTON_G(Button.UnicodeButton(UnicodeConstants.G, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 6)
    })),
    VOTE_BUTTON_H(Button.UnicodeButton(UnicodeConstants.H, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 7)
    })),
    VOTE_BUTTON_I(Button.UnicodeButton(UnicodeConstants.I, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 8)
    })),
    VOTE_BUTTON_J(Button.UnicodeButton(UnicodeConstants.J, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 9)
    })),
    VOTE_BUTTON_K(Button.UnicodeButton(UnicodeConstants.K, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 10)
    })),
    VOTE_BUTTON_L(Button.UnicodeButton(UnicodeConstants.L, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 11)
    })),
    VOTE_BUTTON_M(Button.UnicodeButton(UnicodeConstants.M, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 12)
    })),
    VOTE_BUTTON_N(Button.UnicodeButton(UnicodeConstants.N, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 13)
    })),
    VOTE_BUTTON_O(Button.UnicodeButton(UnicodeConstants.O, label@ IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@label
        }
        voteButtonGroup.addVote(runner.user, 14)
    })),
    SKIP_BUTTON_FORCE(Button.UnicodeButton(UnicodeConstants.FAST_FORWARD, label@ IButtonRunnable { runner: Member?, channel: TextChannel, message: Message ->
        val data = GuildDataManager.getGuildData(channel.guild.idLong)
        if (!data.permissions.hasPermission(runner, channel, CascadeBot.INS.permissionsManager.getPermission("skip.force"), data.coreSettings)) {
            return@label
        }
        message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
        val voteButtonGroup = data.persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        voteButtonGroup!!.stopVote()
        SkipCommand.voteMap.remove(channel.guild.idLong)
        CascadeBot.INS.musicHandler.getPlayer(channel.guild.idLong)!!.skip()
    }));

}
