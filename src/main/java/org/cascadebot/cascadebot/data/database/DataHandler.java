package org.cascadebot.cascadebot.data.database;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.cascadebot.cascadebot.utils.diff.Difference;

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
import java.util.Map;

public class DataHandler<T> {

    public <Y> Y deepCopy(Y original) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException, IOException, ClassNotFoundException {
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
                newMap.put(deepCopy(entry.getKey()), deepCopy(entry.getValue()));
            }
            return (Y) newMap;
        } else if (original instanceof Collection) {
            Collection<?> collection = (Collection<?>) original;
            Collection newCol = (Collection) original.getClass().getDeclaredConstructor().newInstance();
            for (Object obj : collection) {
                newCol.add(deepCopy(obj));
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
                    continue;
                }
                field.setAccessible(true);
                Object fieldOrig = field.get(original);
                Object copy = deepCopy(fieldOrig);

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

    /*public Difference diff(T original, T changed) {

    }

    public Bson getUpdate(Difference difference) {

    }*/

}
