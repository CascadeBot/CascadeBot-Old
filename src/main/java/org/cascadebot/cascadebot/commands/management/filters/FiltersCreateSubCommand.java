package org.cascadebot.cascadebot.commands.management.filters;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.CommandFilter;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FiltersCreateSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0 || context.getArgs().length > 2) {
            context.getUIMessaging().replyUsage();
            return;
        }

        // Create only with name
        if (context.getArgs().length == 1) {
            var filter = new CommandFilter(context.getArg(0));
            context.getData().getCoreSettings().getCommandFilters().add(filter);
            context.getTypedMessaging().replySuccess(
                    "Created command filter of type **%s** with the name `%s`",
                    FormatUtils.formatEnum(filter.getType(), context.getLocale()),
                    filter.getName()
            );
        } else {
            var type = context.getArg(1);
            if (EnumUtils.isValidEnumIgnoreCase(CommandFilter.FilterType.class, type)) {
                var filter = new CommandFilter(context.getArg(0));
                context.getData().getCoreSettings().getCommandFilters().add(filter);
                filter.setType(EnumUtils.getEnumIgnoreCase(CommandFilter.FilterType.class, type));
                context.getTypedMessaging().replySuccess(
                        "Created command filter of type **%s** with the name `%s`",
                        FormatUtils.formatEnum(filter.getType(), context.getLocale()),
                        filter.getName()
                );
            } else {
                context.getTypedMessaging().replyDanger(
                        "The filter type `%s` does not exist! Please choose one of: %s",
                        type,
                        Arrays.stream(CommandFilter.FilterType.values())
                                .map(filterType -> "`" + FormatUtils.formatEnum(filterType, context.getLocale()) + "`")
                                .collect(Collectors.joining(", "))
                );
            }
        }
    }

    @Override
    public String command() {
        return "create";
    }

    @Override
    public String parent() {
        return "filters";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

}
