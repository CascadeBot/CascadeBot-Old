package org.cascadebot.cascadebot.data.objects;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.guild.GuildData;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.utils.buttons.PersistentButton;
import org.cascadebot.cascadebot.utils.buttons.PersistentButtonGroup;

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

    @Getter
    @Setter
    private int currentItem;

    @Getter
    private long ownerId;

    //List of users id who are able to access this list
    private List<Long> users = new ArrayList<>();

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
        PersistentButtonGroup buttonGroup = generateButtons(context.getMember().getIdLong(), channel.getIdLong(), context.getGuild().getIdLong());
        this.setCurrentItem(0);
        Messaging.sendButtonedMessage(channel, getTodoListMessage(), buttonGroup).thenAccept(message -> {
            messageId = message.getIdLong();
            this.channelId = message.getChannel().getIdLong();
        });
    }

    private PersistentButtonGroup generateButtons(long memberId, long channelId, long guildId) {
        PersistentButtonGroup buttonGroup = new PersistentButtonGroup(memberId, channelId, guildId);
        buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_NAVIGATE_LEFT);
        buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_NAVIGATE_UP);
        buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_NAVIGATE_DOWN);
        buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_NAVIGATE_RIGHT);
        buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_CHECK);
        return buttonGroup;
    }

    public void addUncheckButton(Message message) {
        TextChannel channel = CascadeBot.INS.getClient().getTextChannelById(channelId);
        if (channel != null) {
            GuildData data = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
            PersistentButtonGroup buttonGroup = data.getPersistentButtons().get(channelId).get(messageId);
            buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_UNCHECK);
            buttonGroup.removePersistentButton(PersistentButton.TODO_BUTTON_CHECK);
        }
    }

    public void addCheckButton(Message message) {
        TextChannel channel = CascadeBot.INS.getClient().getTextChannelById(channelId);
        if (channel != null) {
            GuildData data = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
            PersistentButtonGroup buttonGroup = data.getPersistentButtons().get(channelId).get(messageId);
            buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_CHECK);
            buttonGroup.removePersistentButton(PersistentButton.TODO_BUTTON_UNCHECK);
        }
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
