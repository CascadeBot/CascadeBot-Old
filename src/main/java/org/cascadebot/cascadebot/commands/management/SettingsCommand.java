/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.Setting;
import org.cascadebot.cascadebot.data.objects.SettingsContainer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.PasteUtils;
import org.cascadebot.cascadebot.utils.ReflectionUtils;
import org.cascadebot.cascadebot.utils.Table;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SettingsCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {

        List<Class<?>> settingsClasses = new ArrayList<>();

        try {
            ReflectionUtils.getClasses("org.cascadebot.cascadebot.data.objects")
                    .stream()
                    .filter(classToFilter -> classToFilter.getAnnotation(SettingsContainer.class) != null)
                    .forEach(settingsClasses::add);
        } catch (ClassNotFoundException | IOException e) {
            context.getTypedMessaging().replyException("Could not process settings!", e);
            return;
        }

        Field field;
        if (context.getArgs().length == 0 || context.getArg(0).equalsIgnoreCase("list")) {
            StringBuilder messageBuilder = new StringBuilder();
            for (Class<?> settingsClass : settingsClasses) {
                Table.TableBuilder tableBuilder = new Table.TableBuilder("Setting", "Current value");
                getSettingsFromClass(settingsClass).entrySet()
                        .stream()
                        .sorted(Comparator.comparing(Map.Entry::getKey))
                        .map(Map.Entry::getValue)
                        .forEach((f) -> {
                            try {
                                tableBuilder.addRow(f.getName(), String.valueOf(f.get(context.getSettings())));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                messageBuilder.append(StringUtils.repeat("-", 5))
                        .append(" ")
                        .append(FormatUtils.formatEnum(settingsClass.getAnnotation(SettingsContainer.class).module()))
                        .append(" module ")
                        .append(StringUtils.repeat("-", 5))
                        .append(tableBuilder.build().toString())
                        .append("\n\n");
            }
            PasteUtils.pasteIfLong(messageBuilder.toString(), 2000, context.getTypedMessaging()::replyInfo);
        } else if ((field = getAllSettings(settingsClasses).get(context.getArg(0).toLowerCase())) != null) {
            try {
                Setting settingAnnotation = field.getAnnotation(Setting.class);
                if (settingAnnotation != null) {
                    if (!context.getData()
                            .getEnabledFlags()
                            .containsAll(Arrays.asList(settingAnnotation.flagRequired()))) {
                        settingAnnotation.niceName();
                        context.getTypedMessaging()
                                .replyDanger(context.i18n("commands.settings.cannot_edit", settingAnnotation.niceName()));
                        return;
                    }
                }

                String value = context.getArg(1);
                if (field.getType() == boolean.class) {
                    boolean booleanValue = Boolean.valueOf(value);
                    value = String.valueOf(booleanValue);
                    field.setBoolean(context.getSettings(), booleanValue);
                } else if (field.getType() == String.class) {
                    field.set(context.getSettings(), value);
                } else {
                    return;
                }
                context.getTypedMessaging()
                        .replySuccess(context.i18n("commands.settings.setting_set", field.getName(), value));
            } catch (IllegalAccessException e) {
                context.getTypedMessaging().replyException(context.i18n("commands.settings.cannot_access"), e);
            }
        } else {
            context.getTypedMessaging().replyDanger(context.i18n("commands.settings.cannot_find_field"));
        }
    }

    @Override
    public String command() {
        return "settings";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("settings", false, Permission.MANAGE_SERVER);
    }

    @Override
    public Module getModule() {
        return Module.MANAGEMENT;
    }

    // This is theoretically safe because we will always create the values field to match this
    @SuppressWarnings("unchecked")
    private Map<String, Field> getSettingsFromClass(Class<?> classForScanning) {
        try {
            Field values = classForScanning.getField("VALUES");
            values.setAccessible(true);
            if (values.getType().isAssignableFrom(Map.class)) {
                return (Map<String, Field>) values.get(null);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // If we can't get the values field, we'll loop through manually
        }
        Map<String, Field> settings = new HashMap<>();
        Arrays.stream(classForScanning.getFields())
                .filter(field -> field.getAnnotation(Setting.class) != null &&
                        field.getAnnotation(Setting.class).directlyEditable())
                .forEach(setting -> settings.put(setting.getName(), setting));
        return settings;
    }

    private Map<String, Field> getAllSettings(List<Class<?>> settingClasses) {
        return settingClasses.stream()
                .map(this::getSettingsFromClass)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
