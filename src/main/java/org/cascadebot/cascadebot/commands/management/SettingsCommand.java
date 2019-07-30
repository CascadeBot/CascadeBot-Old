/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.objects.Setting;
import org.cascadebot.cascadebot.data.objects.SettingsContainer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.SettingsUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

public class SettingsCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 2) {
            Field field = (SettingsUtils.getAllSettings().get(context.getArg(0).toLowerCase()));
            if (field != null) {
                try {
                    Setting settingAnnotation = field.getAnnotation(Setting.class);
                    if (settingAnnotation != null) {
                        if (!context.getData().getEnabledFlags().containsAll(Arrays.asList(settingAnnotation.flagRequired()))) {
                            String niceName = Language.i18n(context.getGuild().getIdLong(), "settings." +
                                    field.getDeclaringClass()
                                    .getAnnotation(SettingsContainer.class)
                                    .module()
                                    .name()
                                    .toLowerCase() + "." + field.getName() + ".nice_name");
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
        return Set.of(new SettingsListSubCommand());
    }

    @Override
    public Module getModule() {
        return Module.MANAGEMENT;
    }



}
