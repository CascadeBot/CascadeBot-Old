package org.cascadebot.cascadebot.utils;

import net.dv8tion.jda.api.entities.Message;
import org.cascadebot.cascadebot.commandmeta.CommandContext;

import java.util.ArrayList;
import java.util.List;

public class PurgeUtils {

    public enum Criteria {
        ATTACHMENT,
        BOT,
        LINK,
        TOKEN,
        USER,
        ALL,
    }

    public static void purge(CommandContext context, Criteria type, int amount, String argument) {

        List<Message> messageList = new ArrayList<>();

        for (Message message : context.getChannel().getIterableHistory()) {
            if (messageList.size() == amount) {
                break;
            }
            
            if (!message.getTimeCreated().isBefore(java.time.OffsetDateTime.now().plusWeeks(2))) {
                break;
            }
            
            switch (type) {
                    
                case ATTACHMENT:
                    if (!message.getAttachments().isEmpty()) {
                        messageList.add(message);
                    }
                    break;
                case BOT:
                    if (message.getAuthor().isBot()) {
                        messageList.add(message);
                    }
                    break;
                case LINK:
                    if (message.getContentRaw().toLowerCase().matches("^(?:https?|ftp):\\/\\/[^\\s/$.?#].[^\\s]*$")) {
                        messageList.add(message);
                    }
                    break;
                case TOKEN:
                    if (message.getContentRaw().toLowerCase().contains(argument.toLowerCase())) {
                        messageList.add(message);
                    }
                    break;
                case USER:
                    if (message.getAuthor().getId().equals(argument)) {
                        messageList.add(message);
                }
                    break;
                case ALL:
                    messageList.add(message);
                    break;
            }
        }
        
        if (messageList.size() <= 1) {
            context.getTypedMessaging().replyWarning("No messages were purged with this criteria");
            return;
        }
        
        context.getChannel().deleteMessages(messageList).queue();
        context.getTypedMessaging().replySuccess("Success! Purged " + messageList.size() + " messages.");
        }
    }
