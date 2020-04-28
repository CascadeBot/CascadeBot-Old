package org.cascadebot.cascadebot.data.objects.donation

import com.google.gson.JsonObject
import org.cascadebot.cascadebot.data.language.Locale

abstract class DataFlag : Flag {
    constructor(id: String, scope: FlagScope) : super(id, scope)
    protected constructor() : super()

    abstract fun parseFlagData(flagDataObject: JsonObject): DataFlag
    abstract override fun getDescription(locale: Locale): String

    abstract operator fun compareTo(flag: DataFlag): Int
}