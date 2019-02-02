/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.permissions;

import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import com.cascadebot.shared.SecurityLevel;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PermissionsManager {

    private LoadingCache<Long, Set<Long>> officialGuildRoleIDCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(DiscordUtils::getAllOfficialRoleIds);
    private LoadingCache<Long, SecurityLevel> securityLevelCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(id -> Security.getSecurityLevelById(id, officialGuildRoleIDCache.get(id)));

    public boolean isAuthorised(ICommandExecutable command, GuildData guildData, Member member) {
        if (command instanceof ICommandRestricted) {
            SecurityLevel userLevel = securityLevelCache.get(member.getUser().getIdLong());
            if (userLevel == null) return false;
            SecurityLevel levelToCheck = ((ICommandRestricted) command).getCommandLevel();
            return userLevel.isAuthorised(levelToCheck);
        } else {
            // TODO: Checking command specific perms
            return true;
        }
        // return false;
    }


}
