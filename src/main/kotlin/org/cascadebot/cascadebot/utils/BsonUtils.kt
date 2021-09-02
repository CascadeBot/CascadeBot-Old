package org.cascadebot.cascadebot.utils

import org.bson.BsonDocument
import org.bson.BsonValue

fun BsonDocument.ifContains(key: String, consumer: (BsonValue)->Unit) {
    if (this.containsKey(key)) {
        consumer(this[key]!!)
    }
}