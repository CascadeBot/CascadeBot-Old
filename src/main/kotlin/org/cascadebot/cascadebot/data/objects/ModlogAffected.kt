package org.cascadebot.cascadebot.data.objects

class ModlogAffected(val affectedType: AffectedType, val name: String, val id : String, @Transient val baseObject: Any) {

    constructor(affectedType: AffectedType, name: String, baseObject: Any) : this(affectedType, name, "null", baseObject)

    constructor() : this(AffectedType.UNKNOWN, "unknown", "null")

}
