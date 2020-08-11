/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt


fun String.truncate(length: Int, ellipses: Boolean = true): String =
        when {
            this.length < length -> this
            ellipses -> this.take(length - 3) + "..."
            else -> this.take(length)
        }

fun Double.toPercentage(dp: Int = 0): String {
    return (round((this * 100) * 10.0.pow(dp)) / 10.0.pow(dp)).roundToInt().toString() + "%"
}

fun <A, B> Function<A, B>.toKotlin(): (A) -> B = { this.apply(it) }

fun <A> Consumer<A>.toKotlin(): (A) -> Unit = { this.accept(it) }

fun <A> Supplier<A>.toKotlin(): () -> A = { this.get() }