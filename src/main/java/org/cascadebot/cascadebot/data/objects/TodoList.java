package org.cascadebot.cascadebot.data.objects;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.util.ArrayList;
import java.util.List;

public class TodoList {


    @Getter
    private List<TodoListItem> items = new ArrayList<>();

    @Getter
    @Setter
    private long messageId = -1;

    @Getter
    @Setter
    private long channelId = -1;

    private ButtonGroup buttonGroup;

    @Getter
    @Setter
    private int currentItem;

    @Getter
    private long ownerId;

    //List of users id who are able to access this list
    private List<Long> users = new ArrayList<>();

    private Button checkButton = new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel, message) -> {
        if (!canUserEdit(runner.getIdLong())) {
            return;
        }

        TodoList.TodoListItem item = this.getItems().get(this.getCurrentItem());
        item.setDone(true);
        addUncheckButton(message);
        message.editMessage(getTodoListMessage()).queue();
    });

    private Button uncheckButton = new Button.UnicodeButton(UnicodeConstants.WHITE_HALLOW_SQUARE, (runner, channel, message) -> {
        if (!this.canUserEdit(runner.getIdLong())) {
            return;
        }
        TodoList.TodoListItem item = this.getItems().get(this.getCurrentItem());
        item.setDone(false);
        addCheckButton(message);
        message.editMessage(getTodoListMessage()).queue();
    });

    private TodoList() {
        //Constructor for mongodb
    }

    public TodoList(long ownerId) {
        this.ownerId = ownerId;
    }

    public int addTodoItem(String text) {
        TodoListItem item = new TodoListItem(text);
        items.add(item);
        return items.indexOf(item);
    }

    public TodoListItem removeTodoItem(int id) {
        return items.remove(id);
    }

    public void addEditUser(Member member) {
        users.add(member.getIdLong());
    }

    public void removeEditUser(Member member) {
        users.remove(member.getIdLong());
    }

    public boolean canUserEdit(Long id) {
        return ownerId == id || users.contains(id);
    }

    public static class TodoListItem {

        @Getter
        @Setter
        private boolean done;

        @Getter
        @Setter
        private String text;

        private TodoListItem() {
            //Constructor for mongodb
        }

        TodoListItem(String text) {
            this.text = text;
            done = false;
        }

    }

    public void edit(CommandContext context) {
        if (messageId == -1 || channelId == -1) return;
        TextChannel originalChannel = context.getGuild().getTextChannelById(channelId);
        if (originalChannel != null && originalChannel.getIdLong() == channelId) {
            Message message = originalChannel.retrieveMessageById(messageId).complete();
            if (message != null) {
                message.editMessage(getTodoListMessage()).queue();
                doCheckToggle(message);
            }
        }
    }

    public void send(CommandContext context, TextChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("The channel should exist :(");
        }
        buttonGroup = generateButtons(context.getMember().getIdLong(), channel.getIdLong(), context.getGuild().getIdLong());
        this.setCurrentItem(0);
        Messaging.sendButtonedMessage(channel, getTodoListMessage(), buttonGroup).thenAccept(message -> {
            messageId = message.getIdLong();
            this.channelId = message.getChannel().getIdLong();
        });
    }

    private ButtonGroup generateButtons(long memberId, long channelId, long guildId) {
        ButtonGroup buttonGroup = new ButtonGroup(memberId, channelId, guildId);
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.BACKWARD_ARROW, (runner, channel1, message) -> {
            int currentPage = this.getCurrentItem() / 10 + 1;
            int start = currentPage * 10 - 10;

            if (start == 0) {
                return;
            }

            int newPos = Math.max(start - 10, 0);

            this.setCurrentItem(newPos);

            message.editMessage(getTodoListMessage()).queue();
            doCheckToggle(message);
        }));
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_UP, (runner, channel1, message) -> {
            int newItem = this.getCurrentItem() - 1;

            if (newItem < 0) {
                return;
            }

            this.setCurrentItem(newItem);

            message.editMessage(getTodoListMessage()).queue();
            doCheckToggle(message);
        }));

        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_DOWN, (runner, channel1, message) -> {
            int newItem = this.getCurrentItem() + 1;

            if (newItem >= this.getItems().size()) {
                return;
            }

            this.setCurrentItem(newItem);

            message.editMessage(getTodoListMessage()).queue();
            doCheckToggle(message);
        }));

        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.FORWARD_ARROW, (runner, channel1, message) -> {
            int currentPage = this.getCurrentItem() / 10 + 1;
            int start = currentPage * 10 - 10;
            int end = start + 9;

            if (end + 1 >= this.getItems().size()) {
                return;
            }

            this.setCurrentItem(end + 1);

            message.editMessage(getTodoListMessage()).queue();
            doCheckToggle(message);
        }));
        buttonGroup.addButton(checkButton);
        return buttonGroup;
    }

    public void addUncheckButton(Message message) {
        buttonGroup.addButton(uncheckButton);
        buttonGroup.removeButton(checkButton);
    }

    public void addCheckButton(Message message) {
        buttonGroup.removeButton(uncheckButton);
        buttonGroup.addButton(checkButton);
    }

    public void doCheckToggle(Message message) {
        TodoList.TodoListItem item = items.get(this.getCurrentItem());

        if (item.isDone()) {
            addUncheckButton(message);
        } else {
            addCheckButton(message);
        }
        //TODO check all items check. I'm going to wait for persistent buttons to do this
    }

    public MessageEmbed getTodoListMessage() {
        int pos = this.getCurrentItem();
        int currentPage = pos / 10 + 1;
        int start = currentPage * 10 - 10;
        int end = start + 9;

        StringBuilder pageBuilder = new StringBuilder();

        for (int i = start; i <= end; i++) {
            if (i >= this.getItems().size()) {
                break;
            }
            TodoList.TodoListItem item = this.getItems().get(i);
            if (i == pos) {
                pageBuilder.append(UnicodeConstants.SMALL_ORANGE_DIAMOND).append(" ");
            } else {
                pageBuilder.append(UnicodeConstants.WHITE_SMALL_SQUARE).append(" ");
            }
            pageBuilder.append(i + 1).append(": ");
            if (item.isDone()) {
                pageBuilder.append("~~");
            }
            pageBuilder.append(item.getText()).append('\n');
            if (item.isDone()) {
                pageBuilder.append("~~");
            }
        }
        EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        embedBuilder.setTitle("Todo List");
        embedBuilder.appendDescription(pageBuilder.toString());
        return embedBuilder.build();
    }

}
