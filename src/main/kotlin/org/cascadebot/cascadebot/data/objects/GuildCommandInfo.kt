package org.cascadebot.cascadebot.data.objects

import org.cascadebot.cascadebot.commandmeta.ICommandMain
import org.cascadebot.cascadebot.data.language.Locale

class GuildCommandInfo(var enabled: Boolean,
                       command: String,
                       defaultCommand: String,
                       var aliases: MutableSet<String>) {

    lateinit var command: String
        private set

    lateinit var defaultCommand: String
        private set

    // Private constructor for MongoDB
    constructor() : this(true, "", "", mutableSetOf())

    constructor(command: ICommandMain, locale: Locale) :
            this(
                    true,
                    command.command(locale),
                    command.command(),
                    command.getGlobalAliases(locale)
            )

}