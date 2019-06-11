/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.data.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.math.NumberUtils;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.utils.FormatUtils;

import java.time.OffsetDateTime;
import java.util.function.BiFunction;

@AllArgsConstructor
@Getter
public enum Placeholder {

    //region Server
    SERVER_REGION((context, args) -> context.getGuild().getRegion().getName(), "Gets the Region this server is in"),
    SERVER_NAME((context, args) -> context.getGuild().getName(), "Gets the name of this server"),
    SERVER_OWNER((context, args) -> context.getGuild().getOwner().getEffectiveName(), "Gets the user who owns this server"),
    MEMBER_COUNT((context, args) -> String.valueOf(context.getGuild().getMembers().size()), "Gets the amount of people in this server"),
    //endregion

    //region Sender
    SENDER((context, args) -> context.getMember().getEffectiveName(), "Gets the user who executed the tag"),
    SENDER_ID((context, args) -> context.getUser().getId(), "Gets the id of the user who executed the tag"),
    SENDER_MENTION((context, args) -> context.getMember().getAsMention(), "Gets the user who executed the tag as a mention"),
    //endregion

    //region channel
    CHANNEL_NAME((context, args) -> context.getChannel().getName(), "Gets the current channel"),
    //endregion

    //region Other
    TIME((context, args) -> FormatUtils.formatDateTime(OffsetDateTime.now()), "Gets the current time"),
    ARGS((context, args) -> {
        if (args.length == 0) return "";
        int arg = NumberUtils.toInt(args[0], -1);
        if (arg == -1 || arg > context.getArgs().length - 1) return "";
        else return context.getArg(arg);
    }, "Injects a specified argument (zero indexed)");
    //endregion

    private BiFunction<CommandContext, String[], String> function;
    private String description;

}
