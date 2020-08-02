package org.cascadebot.cascadebot.data.objects

class ModlogAffected(var affectedType: AffectedType, var name: String, var id : String?) {

    constructor(affectedType: AffectedType, name: String) : this(affectedType, name, null)

    constructor() : this(AffectedType.UNKNOWN, "unknown")

}
