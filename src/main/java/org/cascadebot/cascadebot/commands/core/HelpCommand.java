package org.cascadebot.cascadebot.commands.core;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.CoreCommand;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.entities.GuildModuleEntity;
import org.cascadebot.cascadebot.data.entities.GuildSettingsCoreEntity;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.language.LanguageUtils;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends CoreCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        GuildSettingsCoreEntity coreSettings = context.getDataObject(GuildSettingsCoreEntity.class);
        if (coreSettings == null) {
            throw new UnsupportedOperationException("This shouldn't happen");
        }
        GuildModuleEntity guildModuleEntity = context.getDataObject(GuildModuleEntity.class);
        if (guildModuleEntity == null) {
            throw new UnsupportedOperationException("This shouldn't happen");
        }
        if (context.getArgs().length == 0) {
            context.getUiMessaging().sendPagedMessage(
                    Arrays.stream(Module.values())
                    /*
                     * Filters based on the two conditions:
                     * 1. The module should *not* be private (i.e. a dev module)
                     * 2. Either: The setting "helpShowAllModules" is set to true *or
                     */
                    .filter(module -> !module.isPrivate() && (coreSettings.getHelpShowAllModules() || guildModuleEntity.getModuleEnabled(module)))
                    .map(module -> getModulePages(module, context))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
            );
        } else if (context.getArgs().length == 1) {
            String input = context.getArg(0);
            if (EnumUtils.isValidEnumIgnoreCase(Module.class, input)) {
                Module module = EnumUtils.getEnumIgnoreCase(Module.class, input);
                if (coreSettings.getHelpShowAllModules() || guildModuleEntity.getModuleEnabled(module)) {
                    context.getUiMessaging().sendPagedMessage(getModulePages(module, context));
                } else {
                    context.getTypedMessaging().replyDanger(context.i18n("commands.help.module_not_enabled"));
                }
            } else {
                context.getTypedMessaging().replyDanger(
                        context.i18n("commands.help.module_not_valid",
                                input,
                                LanguageUtils.getListValidEnum(Module.class, context.getLocale(), module -> module != Module.DEVELOPER)
                        )
                );
            }
        } else {
            context.getUiMessaging().replyUsage();
        }
    }

    @Override
    public String command() {
        return "help";
    }

    private List<Page> getModulePages(Module module, CommandContext context) {
        GuildSettingsCoreEntity coreSettings = context.getDataObject(GuildSettingsCoreEntity.class);
        if (coreSettings == null) {
            throw new UnsupportedOperationException("This shouldn't happen");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (MainCommand commandMain : CascadeBot.INS.getCommandManager().getCommandsByModule(module)) {
            /*
             * Allows a permission to be displayed under one of the three conditions:
             * 1. If there is no permission for the command, it will always be displayed
             * 2. If the setting "helpHideCommandsNoPermission" is set to false, the command will always display regardless of permission
             * 3. If the sender of the help command has the permission for the command
             */
            if (commandMain.permission() == null || !coreSettings.getHelpHideNoPerms() || context.hasPermission(commandMain.permission())) {
                stringBuilder.append("`")
                        .append(";")
                        .append(commandMain.command(context.getLocale()))
                        .append("` - ")
                        .append(commandMain.description(context.getLocale()))
                        .append("\n");
            }
        }
        if (stringBuilder.length() == 0) {
            stringBuilder.append("**")
                    .append(context.i18n("commands.help.no_access_to_module"))
                    .append("**");
        }
        return PageUtils.splitStringToEmbedPages(
                stringBuilder.toString(),
                StringUtils.capitalize(FormatUtils.formatEnum(module, context.getLocale())),
                1800,
                '\n'
        );
    }

}
