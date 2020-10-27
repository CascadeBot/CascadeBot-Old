package org.cascadebot.cascadebot.data.objects

class ModlogAffected(val affectedType: AffectedType, val name: String, val id : String = "null", val imageUrl: String? = null) {

    constructor() : this(AffectedType.UNKNOWN, "unknown", "null")

}
