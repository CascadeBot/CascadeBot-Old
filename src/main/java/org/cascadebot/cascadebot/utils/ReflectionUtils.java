/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import com.google.common.reflect.ClassPath;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ClassUtils;
import org.cascadebot.cascadebot.CascadeBot;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ReflectionUtils {

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException Thrown if the loader cannot find the class.
     * @throws IOException            If something goes badly.
     */
    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        List<ClassPath.ClassInfo> classInfos = ClassPath.from(CascadeBot.class.getClassLoader()).getTopLevelClassesRecursive(packageName).asList();
        return classInfos.stream().map(ClassPath.ClassInfo::load).collect(Collectors.toList());
    }

    public static <T> T assignMapToObject(T original, Map<String, Object> newFields, boolean ignoreInvalid) throws NoSuchFieldException, IllegalAccessException {
        for (var entry : newFields.entrySet()) {
            try {
                Field field = original.getClass().getDeclaredField(entry.getKey());
                setFieldOnObject(field, original, entry.getValue());
            } catch (NoSuchFieldException e) {
                if (!ignoreInvalid) {
                    throw e;
                }
            }
        }
        return original;
    }

    public static void setFieldOnObject(Field field, Object target, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        if (!field.getType().isPrimitive()) {
            if (field.getType().isAssignableFrom(target.getClass())) {
                field.setAccessible(true);
                field.set(target, value);
            } else {
                throw new IllegalArgumentException(String.format(
                        "Type mismatch for field \"%s\"! Existing type: %s New type: %s",
                        field.getName(),
                        field.getType().getSimpleName(),
                        value.getClass().getSimpleName()
                ));
            }
        }
        Class<?> primitiveClass = ClassUtils.wrapperToPrimitive(value.getClass());
        if (!field.getType().equals(primitiveClass)) {
            throw new IllegalArgumentException(String.format(
                    "Type mismatch for field \"%s\"! Existing type: %s New type: %s",
                    field.getName(),
                    field.getType().getSimpleName(),
                    value.getClass().getSimpleName()
            ));
        }
        if (primitiveClass == byte.class) {
            field.setByte(target, (Byte) value);
        } else if (primitiveClass == short.class) {
            field.setShort(target, (Short) value);
        } else if (primitiveClass == int.class) {
            field.setInt(target, (Integer) value);
        } else if (primitiveClass == long.class) {
            field.setLong(target, (Long) value);
        } else if (primitiveClass == float.class) {
            field.setFloat(target, (Float) value);
        } else if (primitiveClass == double.class) {
            field.setDouble(target, (Double) value);
        } else if (primitiveClass == boolean.class) {
            field.setBoolean(target, (Boolean) value);
        } else if (primitiveClass == char.class) {
            field.setChar(target, (Character) value);
        }
    }


}
