package org.cascadebot.cascadebot.scripting

import org.graalvm.polyglot.Value

abstract class Promise {

    abstract fun intThen(resolve: Value, reject: Value, callback: Value): Promise

    fun then(resolve: Value, reject: Value): Promise {
        return intThen(resolve, reject, Value.asValue({}))
    }

    fun then(resolve: Value): Promise {
        return then(resolve, Value.asValue({}))
    }

    fun then(): Promise {
        return then(Value.asValue({}), Value.asValue({}))
    }

    fun catch(reject: Value): Promise {
        return then(Value.asValue({}), reject)
    }

    fun catch(): Promise {
        return then(Value.asValue({}), Value.asValue({}))
    }

    fun finally(callback: Value): Promise {
        return intThen(Value.asValue({}), Value.asValue({}), callback)
    }

    fun finally(): Promise {
        return finally(Value.asValue({}))
    }
}