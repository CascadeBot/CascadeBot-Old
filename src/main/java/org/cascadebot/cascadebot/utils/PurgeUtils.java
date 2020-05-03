/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.utils;

import net.dv8tion.jda.api.entities.Message;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.data.objects.PurgeCriteria;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PurgeUtils {

    private static final Pattern linkCheck = Pattern.compile("^(?:https?|ftp):\\/\\/[^\\s/$.?#].[^\\s]*$");

    /**
     * Purge method that cleans messages based on the criteria received,
     * and the amount of messages to clean.
     *
     * @param context {@link CommandContext} of the command
     * @param type {@link PurgeCriteria} to filter for
     * @param amount Amount of messages to clear
     * @param argument Optional argument, made for {@code TOKEN and USER}
     * @return {@link CommandContext#getTypedMessaging}
     */

    public static void purge(CommandContext context, PurgeCriteria type, int amount, String argument) {

        List<Message> messageList = new ArrayList<>();

        for (Message message : context.getChannel().getIterableHistory()) {
            if (messageList.size() == amount) {
                break;
            }
            
            if (message.getTimeCreated().isBefore(OffsetDateTime.now().minusWeeks(2))) {
                context.getTypedMessaging().replyWarning(context.i18n("commands.purge.restriction_time"));
                break;
            }
            
            if (!context.getData().getModerationSettings().isPurgePinnedMessages() && message.isPinned()) {
                    continue;
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
                    if (linkCheck.matcher(message.getContentRaw()).matches()) {
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
            context.getTypedMessaging().replyWarning(context.i18n("commands.purge.failed_clear"));
            return;
        }
        context.getChannel().deleteMessages(messageList).queue($void -> {
            context.getTypedMessaging().replySuccess(context.i18n("commands.purge.successfully_done", messageList.size()));
        }, e -> context.getTypedMessaging().replyException(context.i18n("responses.failed_to_run_command"), e));
        }

    }
