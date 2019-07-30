package org.cascadebot.cascadebot.data.graphql.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.data.objects.Setting;

import java.lang.reflect.Field;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SettingsWrapper {

    private final String name;
    private final Flag[] flagsRequired;
    private final Module[] modules;
    private final boolean directlyEditable;

    public static SettingsWrapper from(Field field, Setting setting) {
        return new SettingsWrapper(field.getName(), setting.flagRequired(), setting.modules(), setting.directlyEditable());
    }

}
