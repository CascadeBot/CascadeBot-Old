/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.shared.Regex;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GuildFlagSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length > 0) {
            Flag flag;
            if (EnumUtils.isValidEnumIgnoreCase(Flag.class, context.getArg(0))) {
                flag = EnumUtils.getEnum(Flag.class, context.getArg(0).toUpperCase());
            } else {
                if (context.getArg(0).equalsIgnoreCase("list")) {
                    EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
                    embedBuilder.setTitle("Flags");
                    embedBuilder.setDescription(Arrays.stream(Flag.values())
                            .map(e -> String.format("- `%s`", FormatUtils.formatEnum(e, context.getLocale())))
                            .collect(Collectors.joining("\n")));
                    context.getTypedMessaging().replyInfo(embedBuilder);
                    return;
                }
                context.getTypedMessaging().replyDanger("Invalid flag! Possible values: %s", Arrays.toString(Flag.values()));
                return;
            }

            Guild guild = context.getGuild();
            if (context.getArgs().length == 2) {
                if (Regex.ID.matcher(context.getArg(1)).matches()) {
                    guild = CascadeBot.INS.getShardManager().getGuildById(context.getArg(1));
                    if (guild == null) {
                        context.getTypedMessaging().replyDanger("Cannot find that guild!");
                        return;
                    }
                } else {
                    context.getTypedMessaging().replyDanger("Invalid guild ID!");
                    return;
                }
            }

            Guild finalGuild = guild;
            GuildDataManager.getGuildData(guild.getIdLong()).write(guildData -> {
                if (guildData.isFlagEnabled(flag)) {
                    guildData.disableFlag(flag);
                    context.getTypedMessaging().replySuccess("Disabled flag `%s` for guild `%s (%s)`", FormatUtils.formatEnum(flag, guildData.getLocale()), finalGuild.getName(), finalGuild.getId());
                } else {
                    guildData.enableFlag(flag);
                    context.getTypedMessaging().replySuccess("Enabled flag `%s` for guild `%s (%s)`", FormatUtils.formatEnum(flag, guildData.getLocale()), finalGuild.getName(), finalGuild.getId());
                }
            });

        }
    }

    @Override
    public String command() {
        return "flag";
    }

    @Override
    public String parent() {
        return "guild";
    }

    @Override
    public String description() {
        return "Toggles a flag for this guild [or a different guild].";
    }

    @Override
    public CascadePermission permission() {
        return null;
    }

}
