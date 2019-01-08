/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.Constants;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.messaging.Messaging;
import com.cascadebot.cascadebot.utils.buttons.Button;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.cascadebot.cascadebot.utils.pagination.Page;
import com.cascadebot.cascadebot.utils.pagination.PageCache;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.Checks;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CommandContext {


    private final GuildData data;

    private final TextChannel channel;
    private final Message message;
    private final Guild guild;
    private final Member member;

    private final String[] args;
    private final String trigger;
    private final boolean isMention;

    public CommandContext(TextChannel channel, Message message, Guild guild, GuildData data, String[] args, Member invoker,
                          String trigger, boolean isMention) {
        this.channel = channel;
        this.message = message;
        this.guild = guild;
        this.member = invoker;
        this.data = data;
        this.args = args;
        this.trigger = trigger;
        this.isMention = isMention;
    }


    //region Raw data getters

    public GuildData getData() {
        return data;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public Message getMessage() {
        return message;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getMember() {
        return member;
    }

    public User getUser() {
        return member.getUser();
    }

    public String[] getArgs() {
        return args;
    }

    public String getTrigger() {
        return trigger;
    }

    public boolean isMention() {
        return isMention;
    }

    //endregion

    //region Helper methods for arguments

    public String getMessage(int start) {
        return getMessage(start, args.length);
    }

    public String getMessage(int start, int end) {
        return String.join(" ", ArrayUtils.subarray(args, start, end));
    }

    public boolean isArgInteger(int index) {
        return Constants.INTEGER_REGEX.matcher(this.args[index]).matches();
    }

    public boolean isArgDecimal(int index) {
        return Constants.DECIMAL_REGEX.matcher(this.args[index]).matches();
    }

    public String getArg(int index) {
        return this.args[index];
    }

    public int getArgAsInteger(int index) {
        return Integer.parseInt(this.args[index]);
    }

    public Double getArgAsDouble(int index) {
        return Double.parseDouble(StringUtils.replace(this.args[index], ",", "."));
    }

    //endregion

    //region Message Methods

    /**
     * Replies to the user in this context
     *
     * @param message the message to reply with which cannot be blank
     * @throws IllegalArgumentException if message is blank
     */
    public void reply(String message) {
        Checks.notBlank(message, "message");
        channel.sendMessage(message).queue();
    }

    public void reply(MessageEmbed embed) {
        Checks.notNull(embed, "embed");
        channel.sendMessage(embed).queue();
    }

    public void reply(Message message) {
        Checks.notNull(message, "message");
        channel.sendMessage(message).queue();
    }

    public void replyInfo(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendInfoMessage(channel, message, data.getUseEmbedForMessages());
    }

    public void replyInfo(String message, Object... objects) {
        replyInfo(String.format(message, objects));
    }

    public void replyInfo(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendInfoMessage(channel, builder, data.getUseEmbedForMessages());
    }

    public void replySuccess(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendSuccessMessage(channel, message, data.getUseEmbedForMessages());
    }

    public void replySuccess(String message, Object... objects) {
        replySuccess(String.format(message, objects));
    }

    public void replySuccess(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendSuccessMessage(channel, builder, data.getUseEmbedForMessages());
    }

    public void replyWarning(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendWarningMessage(channel, message, data.getUseEmbedForMessages());
    }

    public void replyWarning(String message, Object... objects) {
        replyWarning(String.format(message, objects));
    }

    public void replyWarning(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendWarningMessage(channel, builder, data.getUseEmbedForMessages());
    }

    public void replyModeration(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendModerationMessage(channel, message, data.getUseEmbedForMessages());
    }

    public void replyModeration(String message, Object... objects) {
        replyModeration(String.format(message, objects));
    }

    public void replyModeration(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendModerationMessage(channel, builder, data.getUseEmbedForMessages());
    }

    public void replyDanger(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendDangerMessage(channel, message, data.getUseEmbedForMessages());
    }

    public void replyDanger(String message, Object... objects) {
        replyDanger(String.format(message, objects));
    }

    public void replyDanger(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendDangerMessage(channel, builder, data.getUseEmbedForMessages());
    }

    /**
     * Sends a message that auto deletes it's self after the specified delay (in mills).
     *
     * @param message The string message to send which cannot be blank.
     * @param delay   The amount of time to wait before it deletes it's self.
     * @throws IllegalArgumentException if message is blank.
     */
    public void sendAutoDeleteMessage(String message, long delay) {
        Checks.notBlank(message, "message");
        Messaging.sendAutoDeleteMessage(this.channel, message, delay);
    }

    /**
     * Sends a message that auto deletes it's self after the specified delay (in mills).
     *
     * @param embed The non-null {@link MessageEmbed} object to send.
     * @param delay The amount of time to wait before it deletes it's self.
     */
    public void sendAutoDeleteMessage(MessageEmbed embed, long delay) {
        Checks.notNull(embed, "embed");
        Messaging.sendAutoDeleteMessage(this.channel, embed, delay);
    }

    /**
     * Sends a message that auto deletes it's self after the specified delay (in mills).
     *
     * @param message The non-null {@link Message} object to send.
     * @param delay   The amount of time to wait before it deletes it's self.
     * @throws IllegalArgumentException if message is null.
     */
    public void sendAutoDeleteMessage(Message message, long delay) {
        Checks.notNull(message, "message");
        Messaging.sendAutoDeleteMessage(this.channel, message, delay);
    }

    /**
     * Checks the permission for the member and channel provided for the context.
     * Usually this is the channel a command was sent in and the member who send the command.
     *
     * @param permissions Non-null and non empty permissions to check.
     * @return true if the member has all of the specified permissions in the channel.
     * @throws IllegalArgumentException if permissions are empty or null.
     */
    public boolean hasPermission(Permission... permissions) {
        Checks.notEmpty(permissions, "Permissions");
        return this.member.hasPermission(this.channel, permissions);
    }

    /**
     * Checks the permissions for the specified member in the channel provided for this context.
     *
     * @param member      the non-null member to check permissions for. The member needs to be in the same guild as the guild in the context.
     * @param permissions permissions Non-null and non empty permissions to check.
     * @return true if the member has all of the specified permissions in the channel.
     * @throws IllegalArgumentException if member is null or not in the same guild.
     * @throws IllegalArgumentException if permissions are empty or null.
     */
    public boolean hasPermission(Member member, Permission... permissions) {
        Checks.notNull(member, "Member");
        Checks.check(member.getGuild().getIdLong() == guild.getIdLong(),
                "Member needs to be in the same guild as this context! Guild ID: " + guild.getId());
        Checks.notEmpty(permissions, "Permissions");
        return this.member.hasPermission(this.channel, permissions);
    }

    /**
     * Gets the bot {@link Member}.
     *
     * @return The bot {@link Member} for this guild.
     */
    public Member getSelfMember() {
        return guild.getMember(CascadeBot.INS.getSelfUser());
    }

    /**
     * Sends a DM to the user in this context.
     *
     * @param message The message to send.
     */
    public void replyDM(String message) {
        replyDM(message, false);
    }

    /**
     * Sends a DM to the user in this context.
     *
     * @param message      The message to send which cannot be blank.
     * @param allowChannel Whether or not we should send to a channel if DMs are closed off.
     * @throws IllegalArgumentException if message is blank.
     */
    public void replyDM(String message, boolean allowChannel) {
        Checks.notBlank(message, "message");
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(), exception -> {
            if (allowChannel) {
                sendAutoDeleteMessage(message, 5000);
            }
        });
    }

    /**
     * Replies to the user in the context with a {@link MessageEmbed} by direct messages.
     *
     * @param embed The non-null {@link MessageEmbed} object to send.
     * @throws IllegalArgumentException if embed is null.
     * @see CommandContext#replyDM(MessageEmbed, boolean)
     */
    public void replyDM(MessageEmbed embed) {
        replyDM(embed, false);
    }

    /**
     * Replies to the user in the context with a {@link MessageEmbed} by direct messages.
     *
     * @param embed        The non-null {@link MessageEmbed} object to send.
     * @param allowChannel Whether or not we should send to a channel if DMs are closed off.
     * @throws IllegalArgumentException if embed is null.
     */
    public void replyDM(MessageEmbed embed, boolean allowChannel) {
        Checks.notNull(embed, "embed");
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(embed).queue(), exception -> {
            if (allowChannel) {
                sendAutoDeleteMessage(embed, 5000);
            }
        });
    }

    /**
     * Replies to the user in the context with a {@link Message} by direct messages.
     *
     * @param message The {@link Message} object to send.
     * @throws IllegalArgumentException if message is null.
     * @see CommandContext#replyDM(Message, boolean).
     */
    public void replyDM(Message message) {
        replyDM(message, false);
    }

    /**
     * Replies to the user in the context with a {@link Message} by direct messages.
     *
     * @param message      The {@link Message} object to send.
     * @param allowChannel Whether or not we should send to a channel if DMs are closed off.
     * @throws IllegalArgumentException if message is null.
     */
    public void replyDM(Message message, boolean allowChannel) {
        Checks.notNull(message, "message");
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(), exception -> {
            if (allowChannel) {
                sendAutoDeleteMessage(message, 5000);
            }
        });
    }

    public void sendButtonedMessage(String message, ButtonGroup group) {
        Checks.notBlank(message, "message");
        channel.sendMessage(message).queue(sentMessage -> {
            addButtons(sentMessage, group);
            group.setMessage(sentMessage.getIdLong());
            GuildDataMapper.getGuildData(guild.getIdLong()).addButtonGroup(channel, sentMessage, group);
        });

    }

    public void sendButtonedMessage(MessageEmbed embed, ButtonGroup group) {
        Checks.notNull(embed, "embed");
        channel.sendMessage(embed).queue(sentMessage -> {
            addButtons(sentMessage, group);
            group.setMessage(sentMessage.getIdLong());
            GuildDataMapper.getGuildData(guild.getIdLong()).addButtonGroup(channel, sentMessage, group);
        });
    }

    public void sendButtonedMessage(Message message, ButtonGroup group) {
        Checks.notNull(message, "message");
        channel.sendMessage(message).queue(sentMessage -> {
            addButtons(sentMessage, group);
            group.setMessage(sentMessage.getIdLong());
            GuildDataMapper.getGuildData(guild.getIdLong()).addButtonGroup(channel, sentMessage, group);
        });
    }

    private void addButtons(Message message, ButtonGroup group) {
        for (Button button : group.getButtons()) {
            button.addReaction(message);
        }
    }

    public void sendPagedMessage(List<Page> pages) {
        ButtonGroup group = new ButtonGroup(member.getUser().getIdLong(), guild.getIdLong());
        group.addButton(new Button.UnicodeButton("\u23EE" /* ⏮ */, (runner, channel, message) -> {
            PageCache.Pages pageGroup = GuildDataMapper.getGuildData(guild.getIdLong()).getPageCache().get(message.getIdLong());
            pageGroup.getPage(1).pageShow(message, 1, pageGroup.getPages());
            pageGroup.setCurrentPage(1);
        }));
        group.addButton(new Button.UnicodeButton("\u25C0" /* ◀ */, (runner, channel, message) -> {
            PageCache.Pages pageGroup = GuildDataMapper.getGuildData(guild.getIdLong()).getPageCache().get(message.getIdLong());
            int newPage = pageGroup.getCurrentPage() - 1;
            if (newPage < 1) {
                return;
            }
            pageGroup.getPage(newPage).pageShow(message, newPage, pageGroup.getPages());
            pageGroup.setCurrentPage(newPage);
        }));
        group.addButton(new Button.UnicodeButton("\u25B6" /* ▶ */, (runner, channel, message) -> {
            PageCache.Pages pageGroup = GuildDataMapper.getGuildData(guild.getIdLong()).getPageCache().get(message.getIdLong());
            int newPage = pageGroup.getCurrentPage() + 1;
            if (newPage > pageGroup.getPages()) {
                return;
            }
            pageGroup.getPage(newPage).pageShow(message, newPage, pageGroup.getPages());
            pageGroup.setCurrentPage(newPage);
        }));
        group.addButton(new Button.UnicodeButton("\u23ED" /* ⏭ */, (runner, channel, message) -> {
            PageCache.Pages pageGroup = GuildDataMapper.getGuildData(guild.getIdLong()).getPageCache().get(message.getIdLong());
            pageGroup.getPage(pageGroup.getPages()).pageShow(message, pageGroup.getPages(), pageGroup.getPages());
            pageGroup.setCurrentPage(pageGroup.getPages());
        }));
        channel.sendMessage("\u00A0").queue(sentMessage -> {
            pages.get(0).pageShow(sentMessage, 1, pages.size());
            addButtons(sentMessage, group);
            group.setMessage(sentMessage.getIdLong());
            GuildDataMapper.getGuildData(guild.getIdLong()).addButtonGroup(channel, sentMessage, group);
            GuildDataMapper.getGuildData(guild.getIdLong()).getPageCache().put(pages, sentMessage.getIdLong());
        });
    }

    //endregion


}
