/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

    public static <E> Set<E> hashSet(E... e) {
        return new HashSet<>(Arrays.asList(e));
    }

    public static <E> Set<E> hashSet(Collection<E> eCollection) {
        return new HashSet<>(eCollection);
    }

}
