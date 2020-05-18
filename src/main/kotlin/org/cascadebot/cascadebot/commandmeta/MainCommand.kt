/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta

import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale

abstract class MainCommand : ExecutableCommand() {

    abstract fun module(): Module

    open fun subCommands(): Set<SubCommand> = setOf()

    fun globalAliases(locale: Locale): Set<String> {
        val element = Language.getLanguage(locale)!!.getElement("commands.$absoluteCommand.aliases")
        if (element.isEmpty || !element.get().isJsonArray) return setOf()
        val array = element.get().asJsonArray
        val aliases = mutableSetOf<String>()
        array.forEach {
            if (it.isJsonPrimitive) {
                aliases.add(it.asString)
            }
        }
        return aliases.toSet()
    }

}