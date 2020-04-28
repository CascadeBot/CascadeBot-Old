package org.cascadebot.cascadebot.data.objects.donation

open class FlagContainer {
    var flags: MutableSet<Flag> = HashSet();

    constructor() {

    }

    constructor(flags: MutableSet<Flag>) {
        this.flags = flags
    }

    open fun getFlag(id: String): Flag? {
        var returnFlag = flags.stream().filter { flag: Flag? -> flag!!.id == id }.findFirst().orElse(null)
        return returnFlag
    }

    fun hasFlag(id: String): Boolean {
        return getFlag(id) != null
    }
}