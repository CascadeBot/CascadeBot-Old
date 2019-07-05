/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
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
            case "id": return sender.getUser().getId();
            case "nickname": return sender.getNickname() == null ? "No nickname!" : sender.getNickname();
            case "name": return sender.getUser().getName();
            default: return sender.getUser().getAsTag();
        }
    }),
    //endregion

    //region channel
    CHANNEL_NAME((context, args) -> context.getChannel().getName()),
    //endregion

    //region Other
    TIME((context, args) -> FormatUtils.formatDateTime(OffsetDateTime.now())),
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
