package org.cascadebot.cascadebot.data.objects

import org.cascadebot.cascadebot.commandmeta.ICommandMain
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.utils.ChangeList

class GuildCommandInfo(var enabled: Boolean,
                       val command: String,
                       val locale: Locale,
                       val aliases: ChangeList<String>) {

    // Private constructor for MongoDB
    private constructor() : this(true, "", Locale.getDefaultLocale(), ChangeList())

    constructor(command: ICommandMain, locale: Locale) :
            this(
                    true,
                    command.command(),
                    locale,
                    ChangeList()
            )

}