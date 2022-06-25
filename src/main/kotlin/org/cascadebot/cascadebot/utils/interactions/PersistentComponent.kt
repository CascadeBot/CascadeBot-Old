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
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.ONE), voteButtonUnicode(UnicodeConstants.ONE)
        )
    ),
    VOTE_BUTTON_TWO(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.TWO), voteButtonUnicode(UnicodeConstants.TWO)
        )
    ),
    VOTE_BUTTON_THREE(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.THREE), voteButtonUnicode(UnicodeConstants.THREE)
        )
    ),
    VOTE_BUTTON_FOUR(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.FOUR), voteButtonUnicode(UnicodeConstants.FOUR)
        )
    ),
    VOTE_BUTTON_FIVE(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.FIVE), voteButtonUnicode(UnicodeConstants.FIVE)
        )
    ),
    VOTE_BUTTON_SIX(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.SIX), voteButtonUnicode(UnicodeConstants.SIX)
        )
    ),
    VOTE_BUTTON_SEVEN(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.SEVEN), voteButtonUnicode(UnicodeConstants.SEVEN)
        )
    ),
    VOTE_BUTTON_EIGHT(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.EIGHT), voteButtonUnicode(UnicodeConstants.EIGHT)
        )
    ),
    VOTE_BUTTON_NINE(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.NINE), voteButtonUnicode(UnicodeConstants.NINE)
        )
    ),
    VOTE_BUTTON_A(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.A), voteButtonUnicode(UnicodeConstants.A)
        )
    ),
    VOTE_BUTTON_B(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.B), voteButtonUnicode(UnicodeConstants.B)
        )
    ),
    VOTE_BUTTON_C(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.C), voteButtonUnicode(UnicodeConstants.C)
        )
    ),
    VOTE_BUTTON_D(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.D), voteButtonUnicode(UnicodeConstants.D)
        )
    ),
    VOTE_BUTTON_E(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.E), voteButtonUnicode(UnicodeConstants.E)
        )
    ),
    VOTE_BUTTON_F(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.F), voteButtonUnicode(UnicodeConstants.F)
        )
    ),
    VOTE_BUTTON_G(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.G), voteButtonUnicode(UnicodeConstants.G)
        )
    ),
    VOTE_BUTTON_H(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.H), voteButtonUnicode(UnicodeConstants.H)
        )
    ),
    VOTE_BUTTON_I(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.I), voteButtonUnicode(UnicodeConstants.I)
        )
    ),
    VOTE_BUTTON_J(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.J), voteButtonUnicode(UnicodeConstants.J)
        )
    ),
    VOTE_BUTTON_K(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.K), voteButtonUnicode(UnicodeConstants.K)
        )
    ),
    VOTE_BUTTON_L(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.L), voteButtonUnicode(UnicodeConstants.L)
        )
    ),
    VOTE_BUTTON_M(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.M), voteButtonUnicode(UnicodeConstants.M)
        )
    ),
    VOTE_BUTTON_N(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.N), voteButtonUnicode(UnicodeConstants.N)
        )
    ),
    VOTE_BUTTON_O(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.O), voteButtonUnicode(UnicodeConstants.O)
        )
    ),
    VOTE_BUTTON_P(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.P), voteButtonUnicode(UnicodeConstants.P)
        )
    ),
    VOTE_BUTTON_Q(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.Q), voteButtonUnicode(UnicodeConstants.Q)
        )
    ),
    VOTE_BUTTON_R(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.R), voteButtonUnicode(UnicodeConstants.R)
        )
    ),
    VOTE_BUTTON_S(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.S), voteButtonUnicode(UnicodeConstants.S)
        )
    ),
    VOTE_BUTTON_T(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.T), voteButtonUnicode(UnicodeConstants.T)
        )
    ),
    VOTE_BUTTON_U(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.U), voteButtonUnicode(UnicodeConstants.U)
        )
    ),
    VOTE_BUTTON_V(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.V), voteButtonUnicode(UnicodeConstants.V)
        )
    ),
    VOTE_BUTTON_W(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.W), voteButtonUnicode(UnicodeConstants.W)
        )
    ),
    VOTE_BUTTON_X(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.X), voteButtonUnicode(UnicodeConstants.X)
        )
    ),
    VOTE_BUTTON_Y(
        CascadeButton.persistent(
            ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.Y), voteButtonUnicode(UnicodeConstants.Y)
        )
    );

}
