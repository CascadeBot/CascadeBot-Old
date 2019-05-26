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
                                                           tableBuilder.addRow(f.getName(), String.valueOf(f.get(context
                                                                   .getSettings())));
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
            PasteUtils.pasteIfLong(StringUtils.repeat(messageBuilder.toString(), 6), 2000, context.getTypedMessaging()::replyInfo);
        } else if ((field = getAllSettings(settingsClasses).get(context.getArg(0).toLowerCase())) != null) {
            try {
                Setting settingAnnotation = field.getAnnotation(Setting.class);
                if (settingAnnotation != null) {
                    if (!context.getData()
                                .getEnabledFlags()
                                .containsAll(Arrays.asList(settingAnnotation.flagRequired()))) {
                        settingAnnotation.niceName();
                        context.getTypedMessaging()
                               .replyDanger("You cannot edit this setting! You need the: %s flag to do this!", settingAnnotation
                                       .niceName());
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
                       .replySuccess("Setting `%s` has been set to a value of `%s`", field.getName(), value);
            } catch (IllegalAccessException e) {
                context.getTypedMessaging().replyException("Could not access that setting!", e);
            }
        } else {
            context.getTypedMessaging().replyDanger("Cannot find that setting!");
        }
    }

    @Override
    public String command() {
        return "settings";
    }

    @Override
    public String description() {
        return "Allows users to change settings for the guild";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("list", "Lists the current settings for the guild", ArgumentType.COMMAND), Argument.of("setting", "", ArgumentType.REQUIRED, Set
                .of(Argument.of("value", "The value for the setting", ArgumentType.REQUIRED))));
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Settings command", "settings", false, Permission.MANAGE_SERVER);
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
