/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import org.hibernate.Session

fun <T> Session.deleteById(clazz: Class<*>, columnName: String, id: T): Int {
    val builder = session.criteriaBuilder
    var criteria = builder.createCriteriaDelete(clazz)
    val root = criteria.root

    criteria = criteria.where(builder.equal(root.get<T>(columnName), id))

    return createQuery(criteria).executeUpdate()
}

fun Session.deleteById(clazz: Class<*>, keys: Map<String, Any>) : Int{
    val builder = session.criteriaBuilder
    var criteria = builder.createCriteriaDelete(clazz)
    val root = criteria.root

    require(keys.isNotEmpty()) { "Keys must not be empty!" }

    keys.forEach {
        criteria = criteria.where(builder.equal(root.get<Any>(it.key), it.value))
    }

    return createQuery(criteria).executeUpdate()
}