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
    @Column(name = "guild_id")
    var guildId = guildId

    @Column(name = "delete_command")
    var deleteCommand: Boolean = true

    @Column(name = "use_embeds")
    var useEmbeds: Boolean = true

    @Column(name = "perm_errors")
    var permErrors: Boolean = true

    @Column(name = "module_errors")
    var moduleErrors: Boolean = true

    @Column(name = "admins_all_perms")
    var adminsAllPerms: Boolean = true

    @Column(name = "help_hide_no_perms")
    var helpHideNoPerms: Boolean = true

    @Column(name = "help_show_all_modules")
    var helpShowAllModules: Boolean = false



}