package org.cascadebot.cascadebot.utils.diff;

import com.google.gson.reflect.TypeToken;
import de.bild.codec.annotations.Transient;
import lombok.SneakyThrows;
import org.cascadebot.cascadebot.utils.lists.CollectionDiff;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DiffUtils {

    public static Difference diff(Object oldObj, Object newObj) {
        return diff(new Difference(), "", oldObj, newObj);
    }

    @SneakyThrows
    private static Difference diff(Difference currentDiff, String path, Object oldObj, Object newObj) {
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();

        Map<String, Object> oldMap = convertToMap(oldObj);
        Map<String, Object> newMap = convertToMap(newObj);

        List<String> oldKeys = new ArrayList<>(oldMap.keySet());
        List<String> newKeys = new ArrayList<>(newMap.keySet());

        CollectionDiff<String> collectionDiff = new CollectionDiff<>(oldKeys, newKeys);

        for (String added : collectionDiff.getAdded()) {
            currentDiff.added.put(path + added, newMap.get(added));
        }

        for (String removed : collectionDiff.getRemoved()) {
            currentDiff.removed.put(path + removed, oldMap.get(removed));
        }

        for (String key : collectionDiff.getInBoth()) {
            Object oldValue = oldMap.get(key);
            Object newValue = newMap.get(key);

            if (!oldValue.equals(newValue)) {
                //System.out.println(Arrays.stream(newValue.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.joining(", ")));

                if (!oldValue.getClass().getTypeName().equals(newValue.getClass().getTypeName())) {
                    DifferenceChanged<?> changed = new DifferenceChanged<>(oldValue, newValue);
                    currentDiff.changed.put(path + key, changed);
                } else if (oldValue instanceof String) {
                    DifferenceChanged<String> changed = new DifferenceChanged<>((String) oldValue, (String) newValue);
                    currentDiff.changed.put(path + key, changed);
                } else if (oldValue instanceof Number) {
                    DifferenceChanged<Number> changed = new DifferenceChanged<>((Number) oldValue, (Number) newValue);
                    currentDiff.changed.put(path + key, changed);
                } else if (oldValue instanceof Boolean) {
                    DifferenceChanged<Boolean> changed = new DifferenceChanged<>((Boolean) oldValue, (Boolean) newValue);
                    currentDiff.changed.put(path + key, changed);
                } else if (oldValue instanceof Collection) {
                    CollectionDiff<?> changed = new CollectionDiff<>((Collection<?>) oldValue, (Collection<?>) newValue);
                    currentDiff.changed.put(path + key, changed);
                } else {
                    diff(currentDiff, path + key + ".", oldValue, newValue);
                }
            }
        }

        return currentDiff;
    }

    public static <T> T deepCopy(T original) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if (original == null) {
            return null;
        }
        Constructor<T> constructor;
        try {
            constructor = (Constructor<T>) original.getClass().getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return original;
        }
        try {
            constructor.setAccessible(true);
        } catch (InaccessibleObjectException e) {
            return original;
        }
        T newObj = constructor.newInstance();
        Field[] fields = original.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers()) || field.getAnnotation(Transient.class) != null) {
                continue;
            }
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            Object fieldValue = field.get(original);
            if (field.getType().isPrimitive()) {
                field.set(newObj, fieldValue);
            } else {
                field.set(newObj, deepCopy(fieldValue));
            }
        }
        return newObj;
    }

    public static <T> Map<String, Object> convertToMap(T original) throws IllegalAccessException, NoSuchFieldException {
        if (original == null) {
            return null;
        }
        if (original instanceof Map) {
            Set<Map.Entry<String, Object>> newSet = ((Map<?, ?>) original).entrySet().stream().map(entry -> new Map.Entry<String, Object>() {
                @Override
                public String getKey() {
                    return entry.getKey().toString();
                }

                @Override
                public Object getValue() {
                    return entry.getValue();
                }

                @Override
                public Object setValue(Object value) {
                    throw  new UnsupportedOperationException();
                }
            }).collect(Collectors.toSet());
            Map<String, Object> newMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : newSet) {
                newMap.put(entry.getKey(), entry.getValue());
            }
            return newMap;
        }
        Map<String, Object> map = new HashMap<>();
        Field[] fields = original.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers()) || field.getAnnotation(Transient.class) != null) {
                continue;
            }
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            Object fieldValue = field.get(original);
            if (field.getType().isPrimitive()) {
                map.put(field.getName(), fieldValue);
            } else {
                map.put(field.getName(), convertToMap(fieldValue));
            }
        }
        return map;
    }

}
