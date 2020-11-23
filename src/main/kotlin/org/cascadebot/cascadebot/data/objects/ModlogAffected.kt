package org.cascadebot.cascadebot.data.objects

class ModlogAffected(val affectedType: AffectedType, val name: String, val mention: String?, val id : String?, val imageUrl: String?) {

    constructor() : this(AffectedType.UNKNOWN, "unknown", null, null, null)

}

enum class AffectedDisplayType {
    MENTION,
    NAME
}