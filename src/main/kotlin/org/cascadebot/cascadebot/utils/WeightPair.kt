/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

data class WeightPair<T>(val item: T?, val weight: Int) {

    private constructor() : this(null, 0)

}