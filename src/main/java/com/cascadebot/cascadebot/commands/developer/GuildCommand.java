package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.Argument;
import com.cascadebot.cascadebot.commandmeta.ArgumentType;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.commands.subcommands.guild.GuildSaveSubCommand;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.data.objects.Flag;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import com.cascadebot.shared.Regex;
import com.cascadebot.shared.SecurityLevel;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;

import java.util.Arrays;
import java.util.Set;

public class GuildCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length > 0) {
            Flag flag;
            if (!EnumUtils.isValidEnumIgnoreCase(Flag.class, context.getArg(0))) {
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
                context.replySuccess("Disabled flag `%s` for guild `%s (%s)`", flag.name(), guild.getName(), guild.getId());
                return;
            } else {
                guildData.enableFlag(flag);
                context.replySuccess("Enabled flag `%s` for guild `%s (%s)`", flag.name(), guild.getName(), guild.getId());
                return;
            }

        }
        context.replyUsage(this);
    }

    @Override
    public Module getModule() {
        return Module.DEVELOPER;
    }

    @Override
    public SecurityLevel getCommandLevel() {
        return SecurityLevel.DEVELOPER;
    }

    @Override
    public Set<ICommandExecutable> getSubCommands() {
        return Set.of(new GuildSaveSubCommand());
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("flag", "", ArgumentType.OPTIONAL,
                Set.of(
                        Argument.of("guild_id", "Toggles a flag for this guild [or a different guild].", ArgumentType.OPTIONAL)
                )
        ));
    }

    @Override
    public String command() {
        return "guild";
    }

    @Override
    public String description() {
        return "interact with the guild";
    }

}
