package org.cascadebot.cascadebot.utils.buttons;

import de.bild.codec.annotations.Transient;
import lombok.Getter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commands.music.SkipCommand;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.TodoList;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.votes.VoteButtonGroup;

public enum PersistentButton {

    TODO_BUTTON_CHECK(new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if (!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        TodoList.TodoListItem item = todoList.getItems().get(todoList.getCurrentItem());
        item.setDone(true);
        todoList.addUncheckButton(message);
        message.editMessage(todoList.getTodoListMessage()).queue();
    })),

    TODO_BUTTON_UNCHECK(new Button.UnicodeButton(UnicodeConstants.WHITE_HALLOW_SQUARE, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if (!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        TodoList.TodoListItem item = todoList.getItems().get(todoList.getCurrentItem());
        item.setDone(false);
        todoList.addCheckButton(message);
        message.editMessage(todoList.getTodoListMessage()).queue();
    })),

    TODO_BUTTON_NAVIGATE_LEFT(new Button.UnicodeButton(UnicodeConstants.BACKWARD_ARROW, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if (!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        int currentPage = todoList.getCurrentItem() / 10 + 1;
        int start = currentPage * 10 - 10;

        if (start == 0) {
            return;
        }

        int newPos = Math.max(start - 10, 0);

        todoList.setCurrentItem(newPos);

        message.editMessage(todoList.getTodoListMessage()).queue();
        todoList.doCheckToggle(message);
    })),

    TODO_BUTTON_NAVIGATE_RIGHT(new Button.UnicodeButton(UnicodeConstants.FORWARD_ARROW, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if (!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        int currentPage = todoList.getCurrentItem() / 10 + 1;
        int start = currentPage * 10 - 10;
        int end = start + 9;

        if (end + 1 >= todoList.getItems().size()) {
            return;
        }

        todoList.setCurrentItem(end + 1);

        message.editMessage(todoList.getTodoListMessage()).queue();
        todoList.doCheckToggle(message);
    })),

    TODO_BUTTON_NAVIGATE_UP(new Button.UnicodeButton(UnicodeConstants.ARROW_UP, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if (!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        int newItem = todoList.getCurrentItem() - 1;

        if (newItem < 0) {
            return;
        }

        todoList.setCurrentItem(newItem);

        message.editMessage(todoList.getTodoListMessage()).queue();
        todoList.doCheckToggle(message);
    })),

    TODO_BUTTON_NAVIGATE_DOWN(new Button.UnicodeButton(UnicodeConstants.ARROW_DOWN, (runner, channel, message) -> {
        TodoList todoList = GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getUsefulSettings().getTodoListByMessage(message.getIdLong());
        if (!todoList.canUserEdit(runner.getIdLong())) {
            return;
        }

        int newItem = todoList.getCurrentItem() + 1;

        if (newItem >= todoList.getItems().size()) {
            return;
        }

        todoList.setCurrentItem(newItem);

        message.editMessage(todoList.getTodoListMessage()).queue();
        todoList.doCheckToggle(message);
    })),

    VOTE_BUTTON_YES(new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), UnicodeConstants.TICK);
    })),

    VOTE_BUTTON_NO(new Button.UnicodeButton(UnicodeConstants.RED_CROSS, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), UnicodeConstants.RED_CROSS);
    })),

    VOTE_BUTTON_ONE(new Button.UnicodeButton(UnicodeConstants.ONE, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 1);
    })),

    VOTE_BUTTON_TWO(new Button.UnicodeButton(UnicodeConstants.TWO, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 2);
    })),

    VOTE_BUTTON_THREE(new Button.UnicodeButton(UnicodeConstants.THREE, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 3);
    })),

    VOTE_BUTTON_FOUR(new Button.UnicodeButton(UnicodeConstants.FOUR, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 4);
    })),

    VOTE_BUTTON_FIVE(new Button.UnicodeButton(UnicodeConstants.FIVE, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 5);
    })),

    VOTE_BUTTON_SIX(new Button.UnicodeButton(UnicodeConstants.SIX, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 6);
    })),

    VOTE_BUTTON_SEVEN(new Button.UnicodeButton(UnicodeConstants.SEVEN, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 7);
    })),

    VOTE_BUTTON_EIGHT(new Button.UnicodeButton(UnicodeConstants.EIGHT, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 8);
    })),

    VOTE_BUTTON_NINE(new Button.UnicodeButton(UnicodeConstants.NINE, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 9);
    })),

    VOTE_BUTTON_A(new Button.UnicodeButton(UnicodeConstants.A, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 0);
    })),

    VOTE_BUTTON_B(new Button.UnicodeButton(UnicodeConstants.B, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 1);
    })),

    VOTE_BUTTON_C(new Button.UnicodeButton(UnicodeConstants.C, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 2);
    })),

    VOTE_BUTTON_D(new Button.UnicodeButton(UnicodeConstants.D, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 3);
    })),

    VOTE_BUTTON_E(new Button.UnicodeButton(UnicodeConstants.E, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 4);
    })),

    VOTE_BUTTON_F(new Button.UnicodeButton(UnicodeConstants.F, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 5);
    })),

    VOTE_BUTTON_G(new Button.UnicodeButton(UnicodeConstants.G, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 6);
    })),

    VOTE_BUTTON_H(new Button.UnicodeButton(UnicodeConstants.H, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 7);
    })),

    VOTE_BUTTON_I(new Button.UnicodeButton(UnicodeConstants.I, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 8);
    })),

    VOTE_BUTTON_J(new Button.UnicodeButton(UnicodeConstants.J, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 9);
    })),

    VOTE_BUTTON_K(new Button.UnicodeButton(UnicodeConstants.K, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 10);
    })),

    VOTE_BUTTON_L(new Button.UnicodeButton(UnicodeConstants.L, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 11);
    })),

    VOTE_BUTTON_M(new Button.UnicodeButton(UnicodeConstants.M, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 12);
    })),

    VOTE_BUTTON_N(new Button.UnicodeButton(UnicodeConstants.N, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 13);
    })),

    VOTE_BUTTON_O(new Button.UnicodeButton(UnicodeConstants.O, (runner, channel, message) -> {
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) GuildDataManager.getGuildData(channel.getGuild().getIdLong()).getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        if (!voteButtonGroup.isUserAllowed(runner.getIdLong())) {
            return;
        }
        voteButtonGroup.addVote(runner.getUser(), 14);
    })),

    SKIP_BUTTON_FORCE(new Button.UnicodeButton(UnicodeConstants.FAST_FORWARD, (runner, channel, message) -> {
        GuildData data = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
        if (!data.getPermissions().hasPermission(runner, channel, CascadeBot.INS.getPermissionsManager().getPermission("skip.force"), data.getCoreSettings())) {
            return;
        }
        message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
        VoteButtonGroup voteButtonGroup = (VoteButtonGroup) data.getPersistentButtons().get(channel.getIdLong()).get(message.getIdLong());
        voteButtonGroup.stopVote();
        SkipCommand.voteMap.remove(channel.getGuild().getIdLong());
        CascadeBot.INS.getMusicHandler().getPlayer(channel.getGuild().getIdLong()).skip();
    }));

    @Getter
    @Transient
    private Button button;

    PersistentButton(Button button) {
        this.button = button;
    }

}
