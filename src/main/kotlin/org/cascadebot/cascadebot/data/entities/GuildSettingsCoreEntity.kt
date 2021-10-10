/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "guild_settings_core")
class GuildSettingsCoreEntity(guildId: Long) {

    @Id
    @Column(name = "guild_id", nullable = false)
    var guildId = guildId

    @Column(name = "delete_command", nullable = false)
    var deleteCommand: Boolean = true

    @Column(name = "use_embeds", nullable = false)
    var useEmbeds: Boolean = true

    @Column(name = "perm_errors", nullable = false)
    var permErrors: Boolean = true

    @Column(name = "module_errors", nullable = false)
    var moduleErrors: Boolean = true

    @Column(name = "admins_all_perms", nullable = false)
    var adminsAllPerms: Boolean = true

    @Column(name = "help_hide_no_perms", nullable = false)
    var helpHideNoPerms: Boolean = true

    @Column(name = "help_show_all_modules", nullable = false)
    var helpShowAllModules: Boolean = false


}