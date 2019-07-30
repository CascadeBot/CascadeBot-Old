package org.cascadebot.cascadebot.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.objects.Setting;
import org.cascadebot.cascadebot.data.objects.SettingsContainer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class SettingsUtils {

    @Getter
    private static Set<Class<?>> settingsClasses;
    @Getter
    private static Map<String, Field> allSettings;

    static {
        try {
            settingsClasses = ReflectionUtils.getClasses("org.cascadebot.cascadebot.data.objects")
                    .stream()
                    .filter(classToFilter -> classToFilter.getAnnotation(SettingsContainer.class) != null)
                    .collect(ImmutableSet.toImmutableSet());
            allSettings = ImmutableMap.copyOf(collectSettingsFromParents(settingsClasses));
        } catch (ClassNotFoundException | IOException e) {
            CascadeBot.LOGGER.error("Could not load settings!", e);
        }
    }


    // This is theoretically safe because we will always create the values field to match this
    @SuppressWarnings("unchecked")
    public static Map<String, Field> getSettingsFromClass(Class<?> classForScanning) {
        Map<String, Field> settings = new HashMap<>();
        Arrays.stream(classForScanning.getDeclaredFields())
                .filter(field -> field.getAnnotation(Setting.class) != null)
                .forEach(setting -> settings.put(setting.getName(), setting));
        return settings;
    }

    private static Map<String, Field> collectSettingsFromParents(Set<Class<?>> parentClasses) {
        return parentClasses.stream()
                .map(SettingsUtils::getSettingsFromClass)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
