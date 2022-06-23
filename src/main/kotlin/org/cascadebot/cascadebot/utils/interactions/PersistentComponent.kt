package org.cascadebot.cascadebot.utils.interactions

import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.data.objects.MoveDirection

enum class PersistentComponent(@field:Transient val component: CascadeComponent) {
    TODO_BUTTON_CHECK(
        CascadeButton.persistent(
            ButtonStyle.PRIMARY,
            "Check",
            Emoji.fromUnicode(UnicodeConstants.TICK),
            todoButtonToggle(true)
        )
    ),
    TODO_BUTTON_UNCHECK(
        CascadeButton.persistent(
            ButtonStyle.PRIMARY,
            "Uncheck",
            Emoji.fromUnicode(UnicodeConstants.WHITE_HALLOW_SQUARE),
            todoButtonToggle(false)
        )
    ),

    TODO_BUTTON_NAVIGATE_LEFT(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY,
            Emoji.fromUnicode(UnicodeConstants.BACKWARD_ARROW),
            todoButtonNavigation(MoveDirection.LEFT)
        )
    ),
    TODO_BUTTON_NAVIGATE_RIGHT(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY,
            Emoji.fromUnicode(UnicodeConstants.FORWARD_ARROW),
            todoButtonNavigation(MoveDirection.RIGHT)
        )
    ),
    TODO_BUTTON_NAVIGATE_UP(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY,
            Emoji.fromUnicode(UnicodeConstants.ARROW_UP),
            todoButtonNavigation(MoveDirection.UP)
        )
    ),
    TODO_BUTTON_NAVIGATE_DOWN(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY,
            Emoji.fromUnicode(UnicodeConstants.ARROW_DOWN),
            todoButtonNavigation(MoveDirection.DOWN)
        )
    ),

    VOTE_BUTTON_YES(
        CascadeButton.persistent(
            ButtonStyle.SUCCESS,
            Emoji.fromUnicode(UnicodeConstants.TICK),
            voteButtonUnicode(UnicodeConstants.TICK)
        )
    ),
    VOTE_BUTTON_NO(
        CascadeButton.persistent(
            ButtonStyle.DANGER,
            Emoji.fromUnicode(UnicodeConstants.RED_CROSS),
            voteButtonUnicode(UnicodeConstants.RED_CROSS)
        )
    ),
    VOTE_BUTTON_ONE(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.ONE), voteButtonAlphaNumeric(1)
        )
    ),
    VOTE_BUTTON_TWO(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.TWO), voteButtonAlphaNumeric(2)
        )
    ),
    VOTE_BUTTON_THREE(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.THREE), voteButtonAlphaNumeric(3)
        )
    ),
    VOTE_BUTTON_FOUR(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.FOUR), voteButtonAlphaNumeric(4)
        )
    ),
    VOTE_BUTTON_FIVE(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.FIVE), voteButtonAlphaNumeric(5)
        )
    ),
    VOTE_BUTTON_SIX(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.SIX), voteButtonAlphaNumeric(6)
        )
    ),
    VOTE_BUTTON_SEVEN(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.SEVEN), voteButtonAlphaNumeric(7)
        )
    ),
    VOTE_BUTTON_EIGHT(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.EIGHT), voteButtonAlphaNumeric(8)
        )
    ),
    VOTE_BUTTON_NINE(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.NINE), voteButtonAlphaNumeric(9)
        )
    ),
    VOTE_BUTTON_A(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.A), voteButtonAlphaNumeric(0)
        )
    ),
    VOTE_BUTTON_B(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.B), voteButtonAlphaNumeric(1)
        )
    ),
    VOTE_BUTTON_C(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.C), voteButtonAlphaNumeric(2)
        )
    ),
    VOTE_BUTTON_D(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.D), voteButtonAlphaNumeric(3)
        )
    ),
    VOTE_BUTTON_E(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.E), voteButtonAlphaNumeric(4)
        )
    ),
    VOTE_BUTTON_F(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.F), voteButtonAlphaNumeric(5)
        )
    ),
    VOTE_BUTTON_G(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.G), voteButtonAlphaNumeric(6)
        )
    ),
    VOTE_BUTTON_H(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.H), voteButtonAlphaNumeric(7)
        )
    ),
    VOTE_BUTTON_I(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.I), voteButtonAlphaNumeric(8)
        )
    ),
    VOTE_BUTTON_J(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.J), voteButtonAlphaNumeric(9)
        )
    ),
    VOTE_BUTTON_K(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.K), voteButtonAlphaNumeric(10)
        )
    ),
    VOTE_BUTTON_L(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.L), voteButtonAlphaNumeric(11)
        )
    ),
    VOTE_BUTTON_M(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.M), voteButtonAlphaNumeric(12)
        )
    ),
    VOTE_BUTTON_N(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.N), voteButtonAlphaNumeric(13)
        )
    ),
    VOTE_BUTTON_O(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.O), voteButtonAlphaNumeric(14)
        )
    ),
    VOTE_BUTTON_P(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.P), voteButtonAlphaNumeric(15)
        )
    ),
    VOTE_BUTTON_Q(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.Q), voteButtonAlphaNumeric(16)
        )
    ),
    VOTE_BUTTON_R(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.R), voteButtonAlphaNumeric(17)
        )
    ),
    VOTE_BUTTON_S(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.S), voteButtonAlphaNumeric(18)
        )
    ),
    VOTE_BUTTON_T(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.T), voteButtonAlphaNumeric(19)
        )
    ),
    VOTE_BUTTON_U(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.U), voteButtonAlphaNumeric(20)
        )
    ),
    VOTE_BUTTON_V(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.V), voteButtonAlphaNumeric(21)
        )
    ),
    VOTE_BUTTON_W(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.W), voteButtonAlphaNumeric(22)
        )
    ),
    VOTE_BUTTON_X(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.X), voteButtonAlphaNumeric(23)
        )
    ),
    VOTE_BUTTON_Y(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.Y), voteButtonAlphaNumeric(24)
        )
    );

}
