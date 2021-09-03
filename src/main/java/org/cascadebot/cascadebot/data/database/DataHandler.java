package org.cascadebot.cascadebot.data.database;

import com.mongodb.client.model.Updates;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.UpdateDescription;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.codecs.DecoderContext;
import org.bson.conversions.Bson;
import org.cascadebot.cascadebot.CascadeBot;
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
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
                if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        field.set(obj, field.get(original));
                    }
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

    public static class RemovedTree {
        private String name;
        public List<RemovedTree> children = new ArrayList<>();

        public RemovedTree(String name) {
            this.name = name;
        }

        public RemovedTree getChild(String name) {
            return children.stream().filter(removedTree -> removedTree.name.equals(name)).findFirst().orElse(null);
        }

        public static RemovedTree buildTree(String name, List<String> paths) {
            Map<String, List<String>> map = new HashMap<>();
            RemovedTree tree = new RemovedTree(name);
            if (paths.size() == 0) {
                return tree;
            }
            for (String path : paths) {
                String[] split = path.split("\\.");
                String newPath = String.join(".", Arrays.copyOfRange(split, 1, split.length - 1));
                if (newPath.isEmpty()) {
                    map.put(split[0], new ArrayList<>());
                    continue;
                }
                if (map.containsKey(split[0])) {
                    map.get(split[0]).add(newPath);
                } else {
                    List<String> strings = new ArrayList<>();
                    strings.add(newPath);
                    map.put(split[0], strings);
                }
            }
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                tree.children.add(RemovedTree.buildTree(entry.getKey(), entry.getValue()));
            }
            return tree;
        }
    }

}
