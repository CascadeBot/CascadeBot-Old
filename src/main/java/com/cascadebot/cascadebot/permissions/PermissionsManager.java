/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.permissions;

import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PermissionsManager {

    private LoadingCache<Long, Set<Long>> roleIDCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(DiscordUtils::getAllOfficialRoleIds);
    private LoadingCache<Long, SecurityLevel> securityLevelCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(id -> SecurityLevel.getLevelById(id, roleIDCache.get(id)));

    public boolean isAuthorised(ICommand command, GuildData guildData, Member member) {
        if (command instanceof ICommandRestricted) {
            SecurityLevel levelToCheck = ((ICommandRestricted) command).getCommandLevel();
            SecurityLevel userLevel = securityLevelCache.get(member.getUser().getIdLong());
            if (levelToCheck == null || userLevel == null) return false;
            return userLevel.isAuthorised(levelToCheck);
        }
        return false;
    }


}
