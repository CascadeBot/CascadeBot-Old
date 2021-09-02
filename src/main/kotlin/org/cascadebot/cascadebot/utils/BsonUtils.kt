package org.cascadebot.cascadebot.utils

import org.bson.BsonArray
import org.bson.BsonDocument
import org.bson.BsonValue

fun BsonDocument.ifContains(key: String, consumer: (BsonValue)->Unit) {
    if (this.containsKey(key)) {
        consumer(this[key]!!)
    }
}

fun BsonDocument.ifContainsArray(key: String, consumer: (BsonArray)->Unit) {
    if (this.containsKey(key) && this[key]!!.isArray) {
        consumer(this[key]!!.asArray()!!)
    }
}

fun BsonDocument.ifContainsDocument(key: String, consumer: (BsonDocument)->Unit) {
    if (this.containsKey(key) && this[key]!!.isDocument) {
        consumer(this[key]!!.asDocument()!!)
    }
}

fun BsonDocument.ifContainsBoolean(key: String, consumer: (Boolean)->Unit) {
    if (this.containsKey(key) && this[key]!!.isBoolean) {
        consumer(this[key]!!.asBoolean()!!.value)
    }
}

fun BsonDocument.ifContainsString(key: String, consumer: (String)->Unit) {
    if (this.containsKey(key) && this[key]!!.isString) {
        consumer(this[key]!!.asString()!!.value)
    }
}

fun BsonDocument.ifContainsInt(key: String, consumer: (Int)->Unit) {
    if (this.containsKey(key) && this[key]!!.isInt32) {
        consumer(this[key]!!.asInt32().value)
    }
}

fun BsonDocument.ifContainsLong(key: String, consumer: (Long)->Unit) {
    if (this.containsKey(key) && this[key]!!.isInt64) {
        consumer(this[key]!!.asInt64().value)
    }
}