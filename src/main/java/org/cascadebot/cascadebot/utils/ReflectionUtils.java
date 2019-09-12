/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import com.google.common.reflect.ClassPath;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.internal.utils.Checks;
import org.apache.commons.lang3.ClassUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.jetbrains.annotations.Contract;

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

    /**
     * Updates an object using values from a {@code Map<String, Object>} where the key is the name of the field and the
     * value is the new value.
     *
     * @param original      The original object to be updated.
     * @param newFields     A map of field names to values to be introduced into the object.
     * @param ignoreInvalid Whether to ignore invalid field names. If this is false, the method will throw
     *                      {@link NoSuchFieldException} when a key exists in the map that does not correspond to a field.
     * @param <T>           The type of the object to be updated and subsequently returned.
     * @return The updated object.
     * @throws NoSuchFieldException     Thrown if {@code ignoreInvalid} is false and the map contains keys that do no correspond to a field.
     * @throws IllegalAccessException   Thrown by {@link Field#set(Object, Object)}.
     * @throws IllegalArgumentException If the type of the value in the map does not match the type of the field.
     *                                  Boxed primitives will automatically be converted to normal primitives.
     */
    public static <T> T assignMapToObject(T original, Map<String, Object> newFields, boolean ignoreInvalid) throws NoSuchFieldException, IllegalAccessException {
        if (newFields == null || newFields.isEmpty()) return original;
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

    /**
     * Updates an object using an completely or partially filled object.
     *
     * @param originalObject the object that will be updated with the new values.
     * @param updateObject   the object that will be used to update the original object. If this is null, the original object will not be changed.
     *                       Any null fields on the object will not update the original object and will be ignored.
     * @param <T>            The type of the object to be updated.
     * @return The original object modified with any non-null fields from
     * @throws IllegalAccessException thrown by {@link Field#set(Object, Object)}
     */
    public static <T> T partiallyUpdateObject(T originalObject, T updateObject) throws IllegalAccessException {
        Checks.notNull(originalObject, "originalObject");
        if (updateObject == null) {
            return originalObject;
        }
        for (Field field : originalObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object newValue = field.get(updateObject);
            if (newValue != null) {
                field.set(originalObject, newValue);
            }
        }
        return originalObject;
    }

    private static void setFieldOnObject(Field field, Object target, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        if (!field.getType().isPrimitive()) {
            if (field.getType().isAssignableFrom(value.getClass())) {
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
        } else {
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


}
