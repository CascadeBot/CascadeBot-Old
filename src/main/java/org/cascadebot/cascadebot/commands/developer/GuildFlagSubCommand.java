/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.Cascade;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.shared.Regex;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class GuildFlagSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length > 0) {
            Flag flag;
            if (!EnumUtils.isValidEnumIgnoreCase(Flag.class, context.getArg(0))) {
                if (context.getArg(0).equalsIgnoreCase("list")) {
                    EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
                    embedBuilder.setTitle("Flags");
                    embedBuilder.setDescription(Arrays.stream(Flag.values())
                            .map(e -> String.format("- `%s`", FormatUtils.formatEnum(e)))
                            .collect(Collectors.joining("\n")));
                    context.getTypedMessaging().replyInfo(embedBuilder);
                    return;
                }
                context.getTypedMessaging().replyDanger("Invalid flag! Possible values: %s", Arrays.toString(Flag.values()));
                return;
            } else {
                flag = EnumUtils.getEnum(Flag.class, context.getArg(0).toUpperCase());
            }

            Guild guild = context.getGuild();
            if (context.getArgs().length == 2) {
                if (Regex.ID.matcher(context.getArg(1)).matches()) {
                    guild = Cascade.INS.getShardManager().getGuildById(context.getArg(1));
                    if (guild == null) {
                        context.getTypedMessaging().replyDanger("Cannot find that guild!");
                        return;
                    }
                } else {
                    context.getTypedMessaging().replyDanger("Invalid guild ID!");
                    return;
                }
            }

            GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());

            if (guildData.isFlagEnabled(flag)) {
                guildData.disableFlag(flag);
                context.getTypedMessaging().replySuccess("Disabled flag `%s` for guild `%s (%s)`", FormatUtils.formatEnum(flag), guild.getName(), guild.getId());
            } else {
                guildData.enableFlag(flag);
                context.getTypedMessaging().replySuccess("Enabled flag `%s` for guild `%s (%s)`", FormatUtils.formatEnum(flag), guild.getName(), guild.getId());
            }

        }
    }

    @Override
    public String command() {
        return "flag";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(
                Argument.of("flag", "", ArgumentType.REQUIRED,
                        Set.of(
                                Argument.of("guild_id", "Toggles a flag for this guild [or a different guild].", ArgumentType.OPTIONAL)
                        )
                ),
                Argument.of("list", "Lists the flags available to toggle.")
        );
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

}
