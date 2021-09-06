package org.cascadebot.cascadebot.commandmeta

import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.commands.OptionMapping

@JvmInline
value class CommandArgs(val internal: Map<String, List<OptionMapping>>) {
    
    //region primitives
    fun getArgAsLong(name: String): Long? {
        return internal[name]?.firstOrNull()?.asLong
    }

    fun getArgsAsLong(name: String): List<Long> {
        return internal[name]?.map { it.asLong } ?: listOf()
    }

    fun getArgAsBoolean(name: String): Boolean? {
        return internal[name]?.firstOrNull()?.asBoolean
    }

    fun getArgsAsBoolean(name: String): List<Boolean> {
        return internal[name]?.map { it.asBoolean } ?: listOf()
    }

    fun getArgAsString(name: String): String? {
        return internal[name]?.firstOrNull()?.asString
    }

    fun getArgsAsString(name: String): List<String> {
        return internal[name]?.map { it.asString } ?: listOf()
    }
    //endregion
    
    //region discord
    fun getArgAsRole(name: String): Role? {
        return internal[name]?.firstOrNull()?.asRole
    }

    fun getArgsAsRole(name: String): List<Role> {
        return internal[name]?.map { it.asRole } ?: listOf()
    }

    fun getArgAsMessageChannel(name: String): MessageChannel? {
        return internal[name]?.firstOrNull()?.asMessageChannel
    }

    fun getArgsAsMessageChannel(name: String): List<MessageChannel> {
        return internal[name]?.mapNotNull { it.asMessageChannel } ?: listOf()
    }
    
    fun getArgAsGuildChannel(name: String): GuildChannel? {
        return internal[name]?.firstOrNull()?.asGuildChannel
    }

    fun getArgsAsGuildChannel(name: String): List<GuildChannel> {
        return internal[name]?.map { it.asGuildChannel } ?: listOf()
    }

    fun getArgAsMember(name: String): Member? {
        return internal[name]?.firstOrNull()?.asMember
    }

    fun getArgsAsMember(name: String): List<Member> {
        return internal[name]?.mapNotNull { it.asMember } ?: listOf()
    }

    fun getArgAsUser(name: String): User? {
        return internal[name]?.firstOrNull()?.asUser
    }

    fun getArgsAsUser(name: String): List<User> {
        return internal[name]?.map { it.asUser } ?: listOf()
    }

    fun getArgAsMentionable(name: String): IMentionable? {
        return internal[name]?.firstOrNull()?.asMentionable
    }

    fun getArgsAsMentionable(name: String): List<IMentionable> {
        return internal[name]?.map { it.asMentionable } ?: listOf()
    }
    //endregion
    
    

}