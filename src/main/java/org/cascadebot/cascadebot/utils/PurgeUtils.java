package org.cascadebot.cascadebot.utils;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import org.cascadebot.cascadebot.commandmeta.CommandContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class PurgeUtils {

    public enum Criteria {
        ATTACHMENT,
        BOT,
        LINK,
        TOKEN,
        USER,
        ALL,
        ID,
    }

    public static void Purge(CommandContext context, Criteria type, int amount, @Nullable String argument) {

        MessageHistory history = new MessageHistory(context.getChannel());
        List<Message> eventMessages = history.retrievePast(amount).complete();
        eventMessages = eventMessages.stream().filter((message) -> {
            var reference = new Object() {
                boolean result;
            };
            switch (type) {
                case ATTACHMENT:
                    reference.result = !message.getAttachments().isEmpty();
                    break;
                case BOT:
                    reference.result = message.getAuthor().isBot();
                    break;
                case LINK:
                    reference.result = message.getContentRaw().toLowerCase().contains("http://") || message.getContentRaw().toLowerCase().contains("https://");
                    break;
                case TOKEN:
                    reference.result = message.getContentRaw().toLowerCase().contains(argument.toLowerCase());
                    break;
                case USER:
                    reference.result = message.getAuthor().getId().equals(argument);
                    break;
                case ALL:
                   reference.result = true;
                   break;
                case ID:
                    reference.result = message.getId().equals(argument);
                    break;
            }
            return reference.result;
        }).collect(Collectors.toList());
        if (eventMessages.size() <= 1) {
            context.getTypedMessaging().replyWarning("No messages were purged with this criteria");
            return;
        }
        context.getChannel().deleteMessages(eventMessages).queue();
        context.getTypedMessaging().replySuccess("Success! Purged " + eventMessages.size() + " messages.");
        }
    }

