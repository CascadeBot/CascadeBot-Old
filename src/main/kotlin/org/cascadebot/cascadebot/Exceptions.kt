package org.cascadebot.cascadebot

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.exceptions.PermissionException

// We need this because the permission constructor is protected :(
class DiscordPermissionException(permission: Permission) : PermissionException(permission)