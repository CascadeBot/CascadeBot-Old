package org.cascadebot.cascadebot.utils.interactions

import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.UnicodeConstants

enum class PersistentComponent(@field:Transient val component: CascadeComponent) {
    TODO_BUTTON_CHECK(CascadeButton.primary("Check", Emoji.fromUnicode(UnicodeConstants.TICK), ::todoButtonCheck)),
    TODO_BUTTON_UNCHECK(CascadeButton.primary("Uncheck", Emoji.fromUnicode(UnicodeConstants.WHITE_HALLOW_SQUARE),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            /*val todoList =
                GuildDataManager.getGuildData(channel.guild.idLong).useful.getTodoListByMessage(message.idLong)
            if (!todoList.canUserEdit(runner.idLong)) {
                return@IButtonRunnable
            }
            val item = todoList.items[todoList.currentItem]
            item.done = false
            todoList.addCheckButton(message.message)
            message.editMessage(todoList.todoListMessage).queue()*/
        })),
    TODO_BUTTON_NAVIGATE_LEFT(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.BACKWARD_ARROW),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            /*val todoList =
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
            message.editMessage(todoList.todoListMessage).queue()*/
        })),
    TODO_BUTTON_NAVIGATE_RIGHT(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.FORWARD_ARROW),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            /*val todoList =
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
            message.editMessage(todoList.todoListMessage).queue()*/
        })),
    TODO_BUTTON_NAVIGATE_UP(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.ARROW_UP),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            /*val todoList =
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
            message.editMessage(todoList.todoListMessage).queue()*/
        })),
    TODO_BUTTON_NAVIGATE_DOWN(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.ARROW_DOWN),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            /*val todoList =
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
            message.editMessage(todoList.todoListMessage).queue()*/
        })),
    VOTE_BUTTON_YES(CascadeButton.success(Emoji.fromUnicode(UnicodeConstants.TICK),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            /*val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, UnicodeConstants.TICK)*/
        })),
    VOTE_BUTTON_NO(CascadeButton.danger(Emoji.fromUnicode(UnicodeConstants.RED_CROSS),
        IButtonRunnable { runner: Member, channel: TextChannel, message: InteractionMessage ->
            /*val voteButtonGroup =
                GuildDataManager.getGuildData(channel.guild.idLong).findVoteGroupByMessageAndChannel(channel.idLong, message.idLong)
            if (!voteButtonGroup!!.isUserAllowed(runner.idLong)) {
                return@IButtonRunnable
            }
            voteButtonGroup.addVote(runner.user, UnicodeConstants.RED_CROSS)*/
        })),
    VOTE_BUTTON_ONE(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.ONE), voteButton(1))),
    VOTE_BUTTON_TWO(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.TWO), voteButton(2))),
    VOTE_BUTTON_THREE(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.THREE), voteButton(3))),
    VOTE_BUTTON_FOUR(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.FOUR), voteButton(4))),
    VOTE_BUTTON_FIVE(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.FIVE), voteButton(5))),
    VOTE_BUTTON_SIX(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.SIX), voteButton(6))),
    VOTE_BUTTON_SEVEN(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.SEVEN), voteButton(7))),
    VOTE_BUTTON_EIGHT(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.EIGHT), voteButton(8))),
    VOTE_BUTTON_NINE(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.NINE), voteButton(9))),
    VOTE_BUTTON_A(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.A), voteButton(0))),
    VOTE_BUTTON_B(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.B), voteButton(1))),
    VOTE_BUTTON_C(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.C), voteButton(2))),
    VOTE_BUTTON_D(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.D), voteButton(3))),
    VOTE_BUTTON_E(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.E), voteButton(4))),
    VOTE_BUTTON_F(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.F), voteButton(5))),
    VOTE_BUTTON_G(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.G), voteButton(6))),
    VOTE_BUTTON_H(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.H), voteButton(7))),
    VOTE_BUTTON_I(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.I), voteButton(8))),
    VOTE_BUTTON_J(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.J), voteButton(9))),
    VOTE_BUTTON_K(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.K), voteButton(10))),
    VOTE_BUTTON_L(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.L), voteButton(11))),
    VOTE_BUTTON_M(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.M), voteButton(12))),
    VOTE_BUTTON_N(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.N), voteButton(13))),
    VOTE_BUTTON_O(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.O), voteButton(14))),
    VOTE_BUTTON_P(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.P), voteButton(15))),
    VOTE_BUTTON_Q(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.Q), voteButton(16))),
    VOTE_BUTTON_R(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.R), voteButton(17))),
    VOTE_BUTTON_S(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.S), voteButton(18))),
    VOTE_BUTTON_T(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.T), voteButton(19))),
    VOTE_BUTTON_U(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.U), voteButton(20))),
    VOTE_BUTTON_V(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.V), voteButton(21))),
    VOTE_BUTTON_W(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.W), voteButton(22))),
    VOTE_BUTTON_X(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.X), voteButton(23))),
    VOTE_BUTTON_Y(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.Y), voteButton(24)));

}
