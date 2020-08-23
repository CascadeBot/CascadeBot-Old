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

/**
 * Truncates the string to the specified length. If ellipses is enabled,
 * then the string is truncated to `length - 3` and `...` added to the end.
 *
 * @param length The maximum length of the output string.
 * @param ellipses Whether to add ellipses `...` to the end of the string (Not exceeding the maximum length).
 * @return The truncated string.
 */
fun String.truncate(length: Int, ellipses: Boolean = true): String =
        when {
            this.length < length -> this
            ellipses -> this.take(length - 3) + "..."
            else -> this.take(length)
        }

/**
 * Converts a string to lowercase and then capitalizes the first character.
 *
 * @return The string with only the first character capitalized.
 */
fun String.toCapitalized(): String = this.toLowerCase().capitalize()

/**
 * Converts each word to capitalized using the [toCapitalized] function.
 * Each word is converted to lowercase and then the first letter is capitalized.
 *
 * @return The string with each word having its first letter capitalized.
 * @see toCapitalized
 */
fun String.toTitleCase(): String = this.split(" ").joinToString(" ") { it.toCapitalized() }

/**
 * Converts the first letter of a sentence to capitalized using [toCapitalized].
 * Each sentence is converted to lowercase and then the first letter is capitalized.
 *
 * @return The string with each sentence having its first letter capitalized.
 * @see toCapitalized
 */
fun String.toSentenceCase() : String = this.split(".").joinToString(".") { it.toCapitalized() }

/**
 * Converts the double to a percentage to a certain number of decimal places (By default 0) and adds a
 * percent sign at the end.
 *
 * e.g. 0.56789 (1 decimal place) -> "56.8%"
 *
 * @return The double multiplied by 100, rounded to `dp` decimal places and a "%" appended.
 * @see round
 */
fun Double.toPercentage(dp: Int = 0): String {
    return (round((this * 100) * 10.0.pow(dp)) / 10.0.pow(dp)).roundToInt().toString() + "%"
}

/**
 * Converts a Java [Function] to a Kotlin Higher order function `(A)->B`.
 *
 * @return The Kotlin Higher order function that represents the Java function.
 */
fun <A, B> Function<A, B>.toKotlin(): (A) -> B = { this.apply(it) }

/**
 * Converts a Java [Consumer] to a Kotlin Higher order function `(T)->Unit`.
 *
 * @return The Kotlin Higher order function that represents the Java consumer.
 */
fun <T> Consumer<T>.toKotlin(): (T) -> Unit = { this.accept(it) }

/**
 * Converts a Java [Supplier] to a Kotlin Higher order function `()->T`.
 *
 * @return The Kotlin Higher order function that represents the Java supplier.
 */
fun <T> Supplier<T>.toKotlin(): () -> T = { this.get() }