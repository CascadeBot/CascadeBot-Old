/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import org.apache.commons.lang.math.NumberUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.time.OffsetDateTime;
import java.util.function.BiFunction;

public enum Placeholder {

    //region Server
    SERVER_REGION(((context, args) -> context.getGuild().getRegion().getName())),
    SERVER_NAME((((context, args) -> context.getGuild().getName()))),
    SERVER_OWNER((((context, args) -> context.getGuild().getOwner().getEffectiveName()))),
    MEMBER_COUNT((((context, args) -> String.valueOf(context.getGuild().getMembers().size())))),
    //endregion

    //region Sender
    SENDER((((context, args) -> context.getMember().getEffectiveName()))),
    SENDER_ID((((context, args) -> context.getUser().getId()))),
    SENDER_MENTION((((context, args) -> context.getMember().getAsMention()))),
    //endregion

    //region channel
    CHANNEL_NAME((((context, args) -> context.getChannel().getName()))),
    //endregion

    //region Other
    TIME((((context, args) -> FormatUtils.formatDateTime(OffsetDateTime.now())))),
    ARGS((((context, args) -> {
        if (args.length == 0) return "";
        int arg = NumberUtils.toInt(args[0], -1);
        if (arg == -1 || arg > context.getArgs().length - 1) return "";
        else return context.getArg(arg);
    })));
    //endregion

    private BiFunction<CommandContext, String[], String> function;

    Placeholder(BiFunction<CommandContext, String[], String> function) {
        this.function = function;
    }

    public BiFunction<CommandContext, String[], String> getFunction() {
        return function;
    }

}
