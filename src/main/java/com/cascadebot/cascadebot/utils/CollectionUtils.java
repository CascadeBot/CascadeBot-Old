/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class CollectionUtils {

    public static <E> Set<E> hashSet(E... e) {
        return new HashSet<>(Arrays.asList(e));
    }

    public static <E> Set<E> hashSet(Collection<E> eCollection) {
        return new HashSet<>(eCollection);
    }

    /**
     * Returns a lookup map for an enum, using the passed transform function.
     *
     * @param clazz  The clazz of the enum
     * @param mapper The mapper. Must be bijective as it otherwise overwrites keys/values.
     * @param <T>    the enum type
     * @param <R>    the type of map key
     * @return a map with the given key and the enum value associated with it
     * @apiNote Thanks to I Al Istannen#1564 for this
     */
    public static <T extends Enum, R> Map<R, T> getReverseMapping(Class<T> clazz, Function<T, R> mapper) {
        Map<R, T> result = new HashMap<>();

        for (T t : clazz.getEnumConstants()) {
            result.put(mapper.apply(t), t);
        }

        return result;
    }

}
