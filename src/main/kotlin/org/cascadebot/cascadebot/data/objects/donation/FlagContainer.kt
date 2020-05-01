package org.cascadebot.cascadebot.data.objects.donation

open class FlagContainer {
    var flags: MutableSet<Flag> = mutableSetOf();

    constructor()

    constructor(flags: MutableSet<Flag>) {
        this.flags = flags
    }

    open fun getFlag(id: String): Flag? {
        return flags.stream().filter { flag: Flag -> flag.id == id }.findFirst().orElse(null)
    }

    fun hasFlag(id: String): Boolean {
        return getFlag(id) != null
    }
}