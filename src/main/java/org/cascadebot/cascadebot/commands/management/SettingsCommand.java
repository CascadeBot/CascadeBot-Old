/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.language.Language;
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

    private static List<Class<?>> settingsClasses = new ArrayList<>();

    static {
        try {
            ReflectionUtils.getClasses("org.cascadebot.cascadebot.data.objects").stream().filter(classToFilter -> classToFilter.getAnnotation(SettingsContainer.class) != null).forEach(settingsClasses::add);
        } catch (ClassNotFoundException | IOException e) {
            CascadeBot.LOGGER.error("Could not load settings!", e);
        }
    }

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 2) {
            Field field = (getAllSettings(settingsClasses).get(context.getArg(0).toLowerCase()));
            if (field != null) {
                try {
                    Setting settingAnnotation = field.getAnnotation(Setting.class);
                    if (settingAnnotation != null) {
                        if (!context.getData().getEnabledFlags().containsAll(Arrays.asList(settingAnnotation.flagRequired()))) {
                            String niceName = Language.i18n(context.getGuild().getIdLong(), "settings." + field.getDeclaringClass().getAnnotation(SettingsContainer.class).module().name().toLowerCase() + "." + field.getName() + ".nice_name");
                            context.getTypedMessaging().replyDanger(context.i18n("commands.settings.cannot_edit", niceName));
                            return;
                        }
                    }

                    if (context.getArgs().length != 2) {
                        context.getUIMessaging().replyUsage();
                        return;
                    }
                    String value = context.getArg(1);
                    if (field.getType() == boolean.class) {
                        boolean booleanValue = Boolean.valueOf(value);
                        value = String.valueOf(booleanValue);
                        field.setBoolean(context.getCoreSettings(), booleanValue);
                    } else if (field.getType() == String.class) {
                        field.set(context.getCoreSettings(), value);
                    } else {
                        return;
                    }
                    context.getTypedMessaging().replySuccess(context.i18n("commands.settings.setting_set", field.getName(), value));
                } catch (IllegalAccessException e) {
                    context.getTypedMessaging().replyException(context.i18n("commands.settings.cannot_access"), e);
                }
            } else {
                context.getTypedMessaging().replyDanger(context.i18n("commands.settings.cannot_find_field"));
            }
        } else {
            context.getUIMessaging().replyUsage();
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
    public Set<ISubCommand> getSubCommands() {
        return Set.of(new SettingsListSubCommand(settingsClasses));
    }

    @Override
    public Module getModule() {
        return Module.MANAGEMENT;
    }

    // This is theoretically safe because we will always create the values field to match this
    @SuppressWarnings("unchecked")
    static Map<String, Field> getSettingsFromClass(Class<?> classForScanning) {
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

    private static Map<String, Field> getAllSettings(List<Class<?>> settingClasses) {
        return settingClasses.stream()
                .map(SettingsCommand::getSettingsFromClass)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
