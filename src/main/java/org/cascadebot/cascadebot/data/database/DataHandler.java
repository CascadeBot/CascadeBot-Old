package org.cascadebot.cascadebot.data.database;

import com.mongodb.client.model.Updates;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.UpdateDescription;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.utils.diff.Difference;
import org.cascadebot.cascadebot.utils.lists.ChangeList;
import org.cascadebot.cascadebot.utils.lists.CollectionDiff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataHandler<T> {

    public T deepCopy(T original) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return deepCopyRec(original);
    }

    private <Y> Y deepCopyRec(Y original) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException, IOException, ClassNotFoundException {
        if (original == null) {
            return null;
        }
        System.out.println(original.getClass().getName());
        if (original.getClass().isPrimitive()) {
            return original; // TODO does this work?
        } else if (original.getClass().isArray()) {
            throw new UnsupportedOperationException();
        } else if (original instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) original;
            Map newMap = (Map) original.getClass().getDeclaredConstructor().newInstance();
            for (Map.Entry entry : map.entrySet()) {
                newMap.put(deepCopyRec(entry.getKey()), deepCopyRec(entry.getValue()));
            }
            return (Y) newMap;
        } else if (original instanceof Collection) {
            Collection<?> collection = (Collection<?>) original;
            Collection newCol = (Collection) original.getClass().getDeclaredConstructor().newInstance();
            for (Object obj : collection) {
                newCol.add(deepCopyRec(obj));
            }
            return (Y) newCol;
        } else if (original instanceof Serializable) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            objectOutputStream.writeObject(original);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            Y copy = (Y) objectInputStream.readObject();
            return copy;
        } else {
            Constructor constructor;
            try {
                constructor = (Constructor) original.getClass().getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                return null;
            }
            constructor.setAccessible(true);
            Y obj = (Y) constructor.newInstance();

            for(Field field : original.getClass().getDeclaredFields()) {
                /* TODO copy transient stuffs so we don't have to specifically copy those
                Ideas:
                - Store a map of the hashCodes encountered plus their copy and if we encounter the same one then use the copy from the map instead of diffing in order to avoid a stack overflow
                - Look into how gson is able to create any object no matter what constructor it has
                 */
                if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                Object fieldOrig = field.get(original);
                Object copy = deepCopyRec(fieldOrig);

                Field toEdit = obj.getClass().getDeclaredField(field.getName());
                makeAccessible(toEdit);

                toEdit.set(obj, copy);
            }

            return obj;
        }
    }

    private void makeAccessible(Field field) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

    public Bson diffUpdate(T original, T changed) throws NoSuchFieldException, IllegalAccessException {
        List<Bson> updates = new ArrayList<>();
        diffUpdateRec(original, changed, "", updates);
        return Updates.combine(updates);
    }

    private <Y> List<Bson> diffUpdateRec(Y original, Y changed, String path, List<Bson> updates) throws IllegalAccessException, NoSuchFieldException {
        if (original == null) {
            return updates;
        }
        if (original.equals(changed)) {
            return updates;
        }
        String objPath = path.substring(0, path.lastIndexOf('.'));
        if (original.getClass().isPrimitive() || String.class.isAssignableFrom(original.getClass())) {
            updates.add(Updates.set(objPath, changed));
        } else if (original.getClass().isArray()) {
            throw new UnsupportedOperationException();
        } else if (original instanceof Map) {
            Set<? extends Map.Entry<?, ?>> entrySetOrig = ((Map<?, ?>) original).entrySet();
            Set<? extends Map.Entry<?, ?>> entrySetChanged = ((Map<?, ?>) changed).entrySet();
            if (entrySetOrig.isEmpty() && entrySetChanged.isEmpty()) {
                return updates;
            }

            boolean stringKey;
            if (!entrySetOrig.isEmpty()) {
                Map.Entry entry = entrySetOrig.iterator().next();
                if (entry.getKey() instanceof String) {
                    stringKey = true;
                } else {
                    stringKey = false;
                }
            } else {
                Map.Entry entry = entrySetChanged.iterator().next();
                if (entry.getKey() instanceof String) {
                    stringKey = true;
                } else {
                    stringKey = false;
                }
            }

            if (stringKey) {
                CollectionDiff<Map.Entry> diff = new CollectionDiff(entrySetOrig, entrySetChanged, Map.Entry.comparingByKey());
                for (Map.Entry added : diff.getAdded()) {
                    updates.add(Updates.set(path + added.getKey(), added.getValue()));
                }
                for (Map.Entry removed : diff.getRemoved()) {
                    updates.add(Updates.unset(path + removed.getKey()));
                }
                for (Map.Entry both : diff.getInBoth()) {
                    diffUpdateRec(((Map<?, ?>) original).get(both.getKey()), ((Map<?, ?>) changed).get(both.getKey()), path + both.getKey() + ".", updates);
                }
            } else {
                diffUpdateRec(entrySetOrig, entrySetChanged, path, updates);
            }
        } else if (original instanceof Collection) {
            updates.add(Updates.set(objPath, changed)); // TODO look into updating individual objects in array (UpdateOptions?)
        } else {
            for (Field field : original.getClass().getDeclaredFields()) {
                String name = field.getName();
                Object orig = field.get(original);
                Object chan = changed.getClass().getDeclaredField(name).get(changed);
                if (orig == null && chan != null) {
                    updates.add(Updates.set(path + name, chan));
                } else if (chan == null && orig != null) {
                    updates.add(Updates.unset(path + name));
                }  else {
                    diffUpdateRec(orig, chan, path + name + ".", updates);
                }
            }
        }

        return updates;
    }

    public T handleChangeStream(ChangeStreamDocument<T> changeStreamDocument, T original) throws NoSuchFieldException, IllegalAccessException {
        if (changeStreamDocument.getFullDocument() != null) {
            return changeStreamDocument.getFullDocument(); // TODO copy over transient
        } else {
            UpdateDescription updateDescription = changeStreamDocument.getUpdateDescription();
            T current = original;
            if (updateDescription.getRemovedFields() != null) {
                current = handleRemoved(updateDescription.getRemovedFields(), current);
            }
            if (updateDescription.getUpdatedFields() != null) {
                current = handleUpdates(updateDescription.getUpdatedFields(), current);
            }
            return current;  // TODO copy over transient
        }
    }

    private T handleRemoved(List<String> removed, T original) throws NoSuchFieldException, IllegalAccessException {
        for (String removedPath: removed) {
            List<String> path = List.of(removedPath.split("\\."));
            handleRemovedRec(original, path);
        }
        return original;
    }

    private <Y> void handleRemovedRec(Y currentObj, List<String> path) throws NoSuchFieldException, IllegalAccessException {
        if (path.size() == 1) {
            removeItem(currentObj, path.get(0));
        } else {
            String item = path.remove(0);
            if (currentObj instanceof Map) {
                handleRemovedRec(((Map<?, ?>) currentObj).get(item), path);
            } else {
                Field field = currentObj.getClass().getDeclaredField(item);
                makeAccessible(field);
                handleRemovedRec(field.get(currentObj), path);
            }
        }
    }

    private <Y> void removeItem(Y obj, String item) throws NoSuchFieldException, IllegalAccessException {
        // We only need to account for maps and objects as arrays are updated, never removed
        if (obj instanceof Map) {
            ((Map<?, ?>) obj).remove(item);
        } else {
            Field field = obj.getClass().getDeclaredField(item);
            makeAccessible(field);
            field.set(obj, null);
        }
    }

    private T handleUpdates(BsonDocument updates, T original) throws NoSuchFieldException, IllegalAccessException {
        handleUpdatesRec(original, null, updates);
        return original;
    }

    private <Y> void handleUpdatesRec(Y currentObj, String currentItem, BsonValue currentValue) throws NoSuchFieldException, IllegalAccessException {
        switch (currentValue.getBsonType()) {
            case DOCUMENT:
                if (currentObj instanceof Map) {
                    if (currentItem != null && !((Map<?, ?>) currentObj).containsKey(currentItem)) {
                        updateItem(currentObj, currentItem, currentValue);
                    } else {
                        for (Map.Entry<String, BsonValue> entry: currentValue.asDocument().entrySet()) {
                            Object newObj = ((Map<?, ?>) currentObj).get(entry.getKey());
                            handleUpdatesRec(newObj, entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    Field field = null;
                    if (currentItem != null) {
                        field = currentObj.getClass().getDeclaredField(currentItem);
                        makeAccessible(field);
                    }
                    if (field != null && field.get(currentObj) == null) {
                        updateItem(currentObj, currentItem, currentValue);
                    } else {
                        for (Map.Entry<String, BsonValue> entry: currentValue.asDocument().entrySet()) {
                            Field field2 = currentObj.getClass().getDeclaredField(entry.getKey());
                            makeAccessible(field2);
                            handleUpdatesRec(field2.get(currentObj), entry.getKey(), entry.getValue());
                        }
                    }
                }
                break;
            case ARRAY:
                if (currentObj instanceof Map) {
                    Map<Object, Object> map = new HashMap<>();
                    for (BsonValue bsonValue: currentValue.asArray()) {
                        BsonDocument doc = bsonValue.asDocument();
                        Object key = doc.get("key"); // TODO somehow get the key and value type from the map so we can convert it to java
                        Object value = doc.get("value");
                        map.put(key, value);
                        // TODO replace map
                    }
                } else {
                    updateArray(currentObj, currentItem, currentValue.asArray());
                }
                break;
            default:
                updateItem(currentObj, currentItem, currentValue);
        }
    }

    private <Y> void updateItem(Y obj, String toSet, BsonValue currentValue) {
        // Convert the item to java, and then update it depending on what obj is
    }

    private <Y> void updateArray(Y obj, String toSet, BsonArray array) {
        // Replace the array
    }

}
