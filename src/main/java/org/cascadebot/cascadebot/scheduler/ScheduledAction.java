package org.cascadebot.cascadebot.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.utils.FormatUtils;

import javax.swing.Action;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class ScheduledAction {

    private final ActionType type;
    private final ActionData data;
    private final long guildId;
    private final long channelId;
    private final long userId;
    private final OffsetDateTime creationTime;
    private final OffsetDateTime executionTime;

    public ScheduledAction(ActionType type, ActionData data, long guildId, long channelId, long userId, OffsetDateTime creationTime, long delay) {
        this.type = type;
        this.data = data;
        this.guildId = guildId;
        this.channelId = channelId;
        this.userId = userId;
        this.creationTime = creationTime;
        this.executionTime = creationTime.plus(delay, ChronoUnit.MILLIS);
    }

    public ScheduledAction(ActionType type, ActionData data, long guildId, long channelId, long userId, OffsetDateTime creationTime, OffsetDateTime executionTime) {
        this.type = type;
        this.data = data;
        this.guildId = guildId;
        this.channelId = channelId;
        this.userId = userId;
        this.creationTime = creationTime;
        this.executionTime = executionTime;
    }

    @Getter
    @AllArgsConstructor
    public enum ActionType {

        TEMP_MUTE((action, data) -> {

        }),
        TEMP_BAN((action, data) -> {

        }),
        REMINDER((action, data) -> {
            ReminderActionData reminderData = ((ReminderActionData) data);

            User user = CascadeBot.INS.getShardManager().getUserById(action.userId);
            if (user == null) return;

            EmbedBuilder builder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, user);
            builder.setTitle("Reminder!");
            builder.setDescription(user.getAsMention() + " you asked us to remind you of this: ```\n" + reminderData.reminder + "```");
            builder.setFooter("Requested at: A TIME");

            if (reminderData.isDM) {
                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessage(builder.build()).queue();
                });
            } else {
                TextChannel channel = CascadeBot.INS.getShardManager().getTextChannelById(action.channelId);
                if (channel == null) return;
                channel.sendMessage(builder.build()).queue();
            }
        });

        private final BiConsumer<ScheduledAction, ActionData> dataConsumer;

    }

    public interface ActionData {}

    @Getter
    @AllArgsConstructor
    public class ModerationActionData implements ActionData {

        private final long targetId;

    }

    @Getter
    @AllArgsConstructor
    public class ReminderActionData implements ActionData {

        private final String reminder;
        private final boolean isDM;

    }


}
