/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects.tags
import java.io.Serializable
import java.util.regex.Pattern
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "guild_tag")
@IdClass(GuildTagId::class)
class GuildTagEntity(guildId: Long, name: String, content: String) {

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Id
    @Column(name = "name")
    val name: String = name

    @Column(name = "content", nullable = false)
    var content: String = content

    @Column(name = "category")
    var category: String? = null

    fun formatTag(commandContext: CommandContext): String {
        return tags.formatMessage(commandContext.locale, content, commandContext)
    }

    fun getPermission(): String {
        return (if (category != null) {
            "$category."
        } else {""}) + name;
    }

    fun getParent(): String {
        return "tag"
    }

    fun cascadeModule(): Module {
        return Module.MANAGEMENT
    }

    companion object {
        // https://regex101.com/r/hlsgVW/1
        @JvmStatic
        val TAG_PATTERN = Pattern.compile("\\{([A-z]+)(?::((?:,?\\w+)+))?}")
    }

}

data class GuildTagId(val guildId: Long, val name: String) : Serializable {
    constructor() : this(0, "")
}
