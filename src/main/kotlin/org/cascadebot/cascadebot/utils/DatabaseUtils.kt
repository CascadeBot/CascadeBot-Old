/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import org.hibernate.Session

fun <T : Any> Session.deleteById(clazz: Class<*>, columnName: String, id: T): Int {
    return deleteById(clazz, mapOf(columnName to id))
}

fun Session.deleteById(clazz: Class<*>, keys: Map<String, Any>): Int {
    val builder = criteriaBuilder
    var criteria = builder.createCriteriaDelete(clazz)
    require(keys.isNotEmpty()) { "Keys must not be empty!" }

    keys.forEach {
        criteria = criteria.where(builder.equal(criteria.root.get<Any>(it.key), it.value))
    }

    return createQuery(criteria).executeUpdate()
}

fun <K : Any, V : Any> Session.listOf(clazz: Class<V>, columnName: String, id: K): MutableList<V> {
    return listOf(clazz, mapOf(columnName to id))
}

fun <T> Session.listOf(clazz: Class<T>, keys: Map<String, Any>): MutableList<T> {
    val builder = criteriaBuilder
    var criteria = builder.createQuery(clazz)

    keys.forEach {
        criteria = criteria.where(builder.equal(criteria.from(clazz).get<Any>(it.key), it.value))
    }

    return this.createQuery(criteria).list();
}