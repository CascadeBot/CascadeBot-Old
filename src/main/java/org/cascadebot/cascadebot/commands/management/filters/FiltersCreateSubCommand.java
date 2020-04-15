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

        var name = context.getArg(0);

        if (context.getData().getCoreSettings().hasCommandFilter(name)) {
            context.getTypedMessaging().replyDanger(context.i18n(
                    "commands.filters.create.already_exists",
                    name
            ));
            return;
        }

        // Args in the format "<name> [type]"
        if (context.getArgs().length == 1) {
            var filter = new CommandFilter(name);
            context.getData().getCoreSettings().addCommandFilter(filter);
            context.getTypedMessaging().replySuccess(context.i18n(
                    "commands.filters.create.created_filter",
                    FormatUtils.formatEnum(filter.getType(), context.getLocale()),
                    filter.getName()
            ));
        } else {
            var type = context.getArg(1);
            if (EnumUtils.isValidEnumIgnoreCase(CommandFilter.FilterType.class, type)) {
                var filter = new CommandFilter(name);
                filter.setType(EnumUtils.getEnumIgnoreCase(CommandFilter.FilterType.class, type));
                context.getData().getCoreSettings().addCommandFilter(filter);
                context.getTypedMessaging().replySuccess(context.i18n(
                        "commands.filters.create.created_filter",
                        FormatUtils.formatEnum(filter.getType(), context.getLocale()),
                        filter.getName()
                ));
            } else {
                context.getTypedMessaging().replyDanger(context.i18n(
                        "commands.filters.create.type_invalid",
                        type,
                        Arrays.stream(CommandFilter.FilterType.values())
                                .map(filterType -> "`" + FormatUtils.formatEnum(filterType, context.getLocale()) + "`")
                                .collect(Collectors.joining(", "))
                ));
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
