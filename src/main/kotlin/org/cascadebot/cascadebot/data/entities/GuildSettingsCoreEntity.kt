/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.objects.Setting
import org.cascadebot.cascadebot.data.objects.SettingsContainer
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@SettingsContainer(module = Module.CORE)
@Table(name = "guild_settings_core")
class GuildSettingsCoreEntity(guildId: Long) {

    @Id
    @Column(name = "guild_id")
    var guildId = guildId

    @Column(name = "locale")
    var locale: Locale = Locale.ENGLISH_UK

    @Column(name = "delete_command", nullable = false)
    @Setting
    var deleteCommand: Boolean = true

    @Column(name = "use_embeds", nullable = false)
    @Setting
    var useEmbeds: Boolean = true

    @Column(name = "perm_errors", nullable = false)
    @Setting
    var permErrors: Boolean = true

    @Column(name = "module_errors", nullable = false)
    @Setting
    var moduleErrors: Boolean = true

    @Column(name = "admins_all_perms", nullable = false)
    @Setting
    var adminsAllPerms: Boolean = true

    @Column(name = "help_hide_no_perms", nullable = false)
    @Setting
    var helpHideNoPerms: Boolean = true

    @Column(name = "help_show_all_modules", nullable = false)
    @Setting
    var helpShowAllModules: Boolean = false


}