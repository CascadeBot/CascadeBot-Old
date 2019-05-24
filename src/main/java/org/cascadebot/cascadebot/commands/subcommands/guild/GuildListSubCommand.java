package org.cascadebot.cascadebot.commands.subcommands.guild;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.Table;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

public class GuildListSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Guild guildForInfo = context.getGuild();

        if (context.getArgs().length > 0) {
            guildForInfo = CascadeBot.INS.getShardManager().getGuildById(context.getArg(0));
        }
        if (guildForInfo == null) {
            context.getTypedMessaging().replyDanger("We couldn't find that guild!");
            return;
        }

        Table.TableBuilder builder = new Table.TableBuilder("Flag Name");

        for (Flag flag : Flag.values()) {
            builder.addRow(flag.name());
            if (context.getData().isFlagEnabled(flag)) {
                builder.addRow("Enabled");
            } else {
                builder.addRow("Disabled");
            }
        }

        context.getUIMessaging().sendPagedMessage(PageUtils.splitTableDataToPages(builder.build(), 20));
    }

    @Override
    public String command() {
        return "list";
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
