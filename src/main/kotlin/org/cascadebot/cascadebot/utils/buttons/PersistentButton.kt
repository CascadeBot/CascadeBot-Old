package org.cascadebot.cascadebot.utils.buttons

import de.bild.codec.annotations.Transient
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
    TODO_BUTTON_CHECK(Button.UnicodeButton(UnicodeConstants.TICK,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
        if (!todoList.canUserEdit(runner.idLong)) {
            return@IButtonRunnable
        }
        val item = todoList.items[todoList.currentItem]
        item.done = true
        todoList.addUncheckButton(message)
        message.editMessage(todoList.todoListMessage).queue()
    })),
    TODO_BUTTON_UNCHECK(Button.UnicodeButton(UnicodeConstants.WHITE_HALLOW_SQUARE,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
        if (!todoList.canUserEdit(runner.idLong)) {
            return@IButtonRunnable
        }
        val item = todoList.items[todoList.currentItem]
        item.done = false
        todoList.addCheckButton(message)
        message.editMessage(todoList.todoListMessage).queue()
    })),
    TODO_BUTTON_NAVIGATE_LEFT(Button.UnicodeButton(UnicodeConstants.BACKWARD_ARROW, IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
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
        message.editMessage(todoList.todoListMessage).queue()
        todoList.doCheckToggle(message)
    })),
    TODO_BUTTON_NAVIGATE_RIGHT(Button.UnicodeButton(UnicodeConstants.FORWARD_ARROW,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
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
        message.editMessage(todoList.todoListMessage).queue()
        todoList.doCheckToggle(message)
    })),
    TODO_BUTTON_NAVIGATE_UP(Button.UnicodeButton(UnicodeConstants.ARROW_UP,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
        if (!todoList.canUserEdit(runner.idLong)) {
            return@IButtonRunnable
        }
        val newItem = todoList.currentItem - 1
        if (newItem < 0) {
            return@IButtonRunnable
        }
        todoList.currentItem = newItem
        message.editMessage(todoList.todoListMessage).queue()
        todoList.doCheckToggle(message)
    })),
    TODO_BUTTON_NAVIGATE_DOWN(Button.UnicodeButton(UnicodeConstants.ARROW_DOWN,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val todoList = GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
        if (!todoList.canUserEdit(runner.idLong)) {
            return@IButtonRunnable
        }
        val newItem = todoList.currentItem + 1
        if (newItem >= todoList.items.size) {
            return@IButtonRunnable
        }
        todoList.currentItem = newItem
        message.editMessage(todoList.todoListMessage).queue()
        todoList.doCheckToggle(message)
    })),
    VOTE_BUTTON_YES(Button.UnicodeButton(UnicodeConstants.TICK,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, UnicodeConstants.TICK)
    })),
    VOTE_BUTTON_NO(Button.UnicodeButton(UnicodeConstants.RED_CROSS,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, UnicodeConstants.RED_CROSS)
    })),
    VOTE_BUTTON_ONE(Button.UnicodeButton(UnicodeConstants.ONE,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 1)
    })),
    VOTE_BUTTON_TWO(Button.UnicodeButton(UnicodeConstants.TWO,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 2)
    })),
    VOTE_BUTTON_THREE(Button.UnicodeButton(UnicodeConstants.THREE,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 3)
    })),
    VOTE_BUTTON_FOUR(Button.UnicodeButton(UnicodeConstants.FOUR,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 4)
    })),
    VOTE_BUTTON_FIVE(Button.UnicodeButton(UnicodeConstants.FIVE,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 5)
    })),
    VOTE_BUTTON_SIX(Button.UnicodeButton(UnicodeConstants.SIX,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 6)
    })),
    VOTE_BUTTON_SEVEN(Button.UnicodeButton(UnicodeConstants.SEVEN,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 7)
    })),
    VOTE_BUTTON_EIGHT(Button.UnicodeButton(UnicodeConstants.EIGHT,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 8)
    })),
    VOTE_BUTTON_NINE(Button.UnicodeButton(UnicodeConstants.NINE,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 9)
    })),
    VOTE_BUTTON_A(Button.UnicodeButton(UnicodeConstants.A,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 0)
    })),
    VOTE_BUTTON_B(Button.UnicodeButton(UnicodeConstants.B,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 1)
    })),
    VOTE_BUTTON_C(Button.UnicodeButton(UnicodeConstants.C,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 2)
    })),
    VOTE_BUTTON_D(Button.UnicodeButton(UnicodeConstants.D,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 3)
    })),
    VOTE_BUTTON_E(Button.UnicodeButton(UnicodeConstants.E,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 4)
    })),
    VOTE_BUTTON_F(Button.UnicodeButton(UnicodeConstants.F,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 5)
    })),
    VOTE_BUTTON_G(Button.UnicodeButton(UnicodeConstants.G,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 6)
    })),
    VOTE_BUTTON_H(Button.UnicodeButton(UnicodeConstants.H,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 7)
    })),
    VOTE_BUTTON_I(Button.UnicodeButton(UnicodeConstants.I,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 8)
    })),
    VOTE_BUTTON_J(Button.UnicodeButton(UnicodeConstants.J,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 9)
    })),
    VOTE_BUTTON_K(Button.UnicodeButton(UnicodeConstants.K,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 10)
    })),
    VOTE_BUTTON_L(Button.UnicodeButton(UnicodeConstants.L,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 11)
    })),
    VOTE_BUTTON_M(Button.UnicodeButton(UnicodeConstants.M,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 12)
    })),
    VOTE_BUTTON_N(Button.UnicodeButton(UnicodeConstants.N,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 13)
    })),
    VOTE_BUTTON_O(Button.UnicodeButton(UnicodeConstants.O,  IButtonRunnable { runner: Member, channel: TextChannel, message: Message ->
        val voteButtonGroup = GuildDataManager.getGuildData(channel.guild.idLong).persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
            return@IButtonRunnable
        }
        voteButtonGroup.addVote(runner.user, 14)
    })),
    SKIP_BUTTON_FORCE(Button.UnicodeButton(UnicodeConstants.FAST_FORWARD,  IButtonRunnable { runner: Member?, channel: TextChannel, message: Message ->
        val data = GuildDataManager.getGuildData(channel.guild.idLong)
        if (!data.management.permissions.hasPermission(runner, channel, CascadeBot.INS.permissionsManager.getPermission("skip.force"), data.core)) {
            return@IButtonRunnable
        }
        message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
        val voteButtonGroup = data.persistentButtons[channel.idLong]!![message.idLong] as VoteButtonGroup?
        voteButtonGroup!!.stopVote()
        SkipCommand.voteMap.remove(channel.guild.idLong)
        CascadeBot.INS.musicHandler.getPlayer(channel.guild.id, org.cascadebot.orchestra.data.enums.NodeType.GENERAL)!!.skip()
    }));

}
