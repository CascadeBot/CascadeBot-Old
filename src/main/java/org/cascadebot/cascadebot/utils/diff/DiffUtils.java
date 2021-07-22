package org.cascadebot.cascadebot.utils.diff;

import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.utils.lists.CollectionDiff;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiffUtils {

    /**
     * Find the difference between two arbitrary objects, and return it using the {@link Difference} object
     *
     * @param oldObj The old objects to get the difference from
     * @param newObj The new object to get the difference from
     * @return a {@link Difference} object
     */
    public static Difference diff(Object oldObj, Object newObj) {
        return diff(new Difference(), "", oldObj, newObj);
    }

    @SneakyThrows
    private static Difference diff(Difference currentDiff, String path, Object oldObj, Object newObj) {
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> oldMap = CascadeBot.getGSON().fromJson(CascadeBot.getGSON().toJson(oldObj), mapType);
        Map<String, Object> newMap = CascadeBot.getGSON().fromJson(CascadeBot.getGSON().toJson(newObj), mapType);

        List<String> oldKeys = new ArrayList<>(oldMap.keySet());
        List<String> newKeys = new ArrayList<>(oldMap.keySet());

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



}
