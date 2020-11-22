package org.cascadebot.cascadebot.data.objects

class ModlogAffected(val affectedType: AffectedType, val name: String, val mention: String?, val id : String?, val imageUrl: String? = null) {

    constructor() : this(AffectedType.UNKNOWN, "unknown", null, null)

}

enum class AffectedDisplayType {
    MENTION,
    NAME
}