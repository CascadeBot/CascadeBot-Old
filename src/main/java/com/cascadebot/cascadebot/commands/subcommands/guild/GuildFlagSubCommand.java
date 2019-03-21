/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.subcommands.guild;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.Argument;
import com.cascadebot.cascadebot.commandmeta.ArgumentType;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.data.objects.Flag;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.FormatUtils;
import com.cascadebot.shared.Regex;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;

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
                    // TODO: Format embed better :( 
                    context.replyInfo(Arrays.stream(Flag.values())
                            .map(e -> String.format("`%s`", FormatUtils.formatEnum(e)))
                            .collect(Collectors.joining("\n"))
                    );
                    return;
                }
                context.replyDanger("Invalid flag! Possible values: %s", Arrays.toString(Flag.values()));
                return;
            } else {
                flag = EnumUtils.getEnum(Flag.class, context.getArg(0).toUpperCase());
            }

            Guild guild = context.getGuild();
            if (context.getArgs().length == 2) {
                if (Regex.ID.matcher(context.getArg(1)).matches()) {
                    guild = CascadeBot.INS.getShardManager().getGuildById(context.getArg(1));
                    if (guild == null) {
                        context.replyDanger("Cannot find that guild!");
                        return;
                    }
                } else {
                    context.replyDanger("Invalid guild ID!");
                    return;
                }
            }

            GuildData guildData = GuildDataMapper.getGuildData(guild.getIdLong());

            if (guildData.isFlagEnabled(flag)) {
                guildData.disableFlag(flag);
                context.replySuccess("Disabled flag `%s` for guild `%s (%s)`", FormatUtils.formatEnum(flag), guild.getName(), guild.getId());
                return;
            } else {
                guildData.enableFlag(flag);
                context.replySuccess("Enabled flag `%s` for guild `%s (%s)`", FormatUtils.formatEnum(flag), guild.getName(), guild.getId());
                return;
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
                Argument.of("flag", "", ArgumentType.OPTIONAL,
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
