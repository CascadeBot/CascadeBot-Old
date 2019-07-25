/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang.math.NumberUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.time.OffsetDateTime;
import java.util.function.BiFunction;

@AllArgsConstructor
@Getter
public enum Placeholder {

    //region Server
    SERVER((context, args) -> {
       Guild guild = context.getGuild();
       if (args.length < 1) return guild.getName();
       switch (args[0].toLowerCase()) {
           case "id": return guild.getId();
           case "region": return guild.getRegion().getName();
           case "owner": return guild.getOwner().getUser().getAsTag();
           case "member_count": return String.valueOf(guild.getMembers().size());
           default: return guild.getName();
       }
    }),
    //endregion

    //region Sender
    SENDER((context, args) -> {
        Member sender = context.getMember();
        if (args.length < 1) return sender.getUser().getAsTag();
        switch (args[0].toLowerCase()) {
            case "id": return sender.getId();
            case "nickname": return sender.getNickname() == null ? "No nickname!" : sender.getNickname();
            case "name": return sender.getUser().getName();
            case "mention": return sender.getAsMention();
            default: return sender.getUser().getAsTag();
        }
    }),
    //endregion

    //region channel
    CHANNEL((context, args) -> {
        TextChannel channel = context.getChannel();
        if (args.length < 1) return channel.getName();
        switch (args[0].toLowerCase()) {
            case "id": return channel.getId();
            case "mention": return channel.getAsMention();
            case "topic": return channel.getTopic();
            case "creation": return FormatUtils.formatDateTime(channel.getTimeCreated(), context.getLocale());
            case "parent": return channel.getParent() == null ? "No channel parent" : channel.getParent().getName();
            default: return channel.getName();
        }
    }),
    //endregion

    //region Other
    TIME((context, args) -> FormatUtils.formatDateTime(OffsetDateTime.now(), context.getLocale())),
    ARGS((context, args) -> {
        if (args.length == 0) return "";
        int arg = NumberUtils.toInt(args[0], -1);
        if (arg == -1 || arg > context.getArgs().length - 1) return "";
        else return context.getArg(arg);
    });
    //endregion

    private BiFunction<CommandContext, String[], String> function;

    public String getDescription(Locale locale) {
        return Language.i18n(locale, "placeholders." + this.name().toLowerCase().replace("_", ".") + ".description");
    }

}
