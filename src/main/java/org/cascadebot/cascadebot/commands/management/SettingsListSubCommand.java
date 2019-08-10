package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.SettingsContainer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.PasteUtils;
import org.cascadebot.cascadebot.utils.Table;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SettingsListSubCommand implements ISubCommand {

    private List<Class<?>> settingsClasses;

    public SettingsListSubCommand(List<Class<?>> settingsClasses) {
        this.settingsClasses = settingsClasses;
    }

    @Override
    public void onCommand(Member sender, CommandContext context) {
        StringBuilder messageBuilder = new StringBuilder();
        for (Class<?> settingsClass : settingsClasses) {
            Table.TableBuilder tableBuilder = new Table.TableBuilder(context.i18n("commands.settings.setting"), context.i18n("commands.settings.current_value"));
            SettingsCommand.getSettingsFromClass(settingsClass).entrySet()
                    .stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .forEach((f) -> {
                        try {
                            tableBuilder.addRow(f.getName(), String.valueOf(f.get(context.getCoreSettings())));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
            messageBuilder.append(context.i18n("commands.settings.section_title", StringUtils.capitalize(FormatUtils.formatEnum(settingsClass.getAnnotation(SettingsContainer.class).module(), context.getLocale()))))
                    .append(tableBuilder.build().toString())
                    .append("\n\n");
        }
        PasteUtils.pasteIfLong(messageBuilder.toString(), 2000, context.getTypedMessaging()::replyInfo);
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public String parent() {
        return "settings";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

}
