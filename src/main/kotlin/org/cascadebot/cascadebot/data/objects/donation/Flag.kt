package org.cascadebot.cascadebot.data.objects.donation

import org.cascadebot.cascadebot.data.language.Language.getLanguage
import org.cascadebot.cascadebot.data.language.Locale

open class Flag (
        val id: String,
        val scope: FlagScope
) {

    constructor() : this ("", FlagScope.GUILD)

    fun getName(locale: Locale): String {
        return getLanguage(locale)!!.getString("flags.$id.name").orElse("No language string defined")
    }

    open fun getDescription(locale: Locale): String {
        return getLanguage(locale)!!.getString("flags.$id.description").orElse("No language string defined")
    }

    enum class FlagScope {
        USER, GUILD
    }
}