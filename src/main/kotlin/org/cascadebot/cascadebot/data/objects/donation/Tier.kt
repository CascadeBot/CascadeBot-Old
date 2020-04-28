/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */
package org.cascadebot.cascadebot.data.objects.donation

import com.google.gson.JsonArray
import io.github.binaryoverload.JSONConfig
import lombok.Getter
import org.apache.commons.lang3.StringUtils
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.language.Language.getLanguage
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.objects.donation.Flag.FlagScope
import java.util.HashMap

class Tier : FlagContainer {
    var parent: String? = null
    var extras: MutableList<TierExtra> = mutableListOf()

    private constructor()

    constructor(flags: MutableSet<Flag>) :
            this(
                    null,
                    flags,
                    ArrayList()
            )

    constructor(parent: String?, flags: MutableSet<Flag>, extras: MutableList<TierExtra>) {
        this.parent = parent
        this.flags = flags
        this.extras = extras
    }

    fun addFlag(flag: Flag) {
        flags.add(flag)
    }

    fun getParent(): Tier? {
        return if (parent == null) null else tiers[parent]
    }

    fun getAllFlags(): Set<Flag> {
        val flags = flags.toMutableSet()
        if (getParent() != null) {
            flags.addAll(getParent()!!.getAllFlags())
        }
        return flags
    }

    override fun getFlag(id: String): Flag? {
        var returnFlag = flags.stream().filter { flag: Flag -> flag.id == id }.findFirst().orElse(null)
        if (parent != null && returnFlag == null) {
            returnFlag = tiers[parent]!!.getFlag(id)
        }
        return returnFlag
    }

    fun isTierParent(tier: String): Boolean {
        if (StringUtils.isBlank(parent)) {
            return false
        }
        return if (parent == tier) {
            true
        } else tiers[parent]!!.isTierParent(tier)
    }

    /**
     * Returns the benefits gave in this tier to a guild.
     *
     * @param locale The locale to use for translations
     * @param idsUsed This parameter should be null. It's used for when calling parent method. It stores flags that have been used already.
     * @return The guild benefits gave in this tier
     */
    fun getGuildTierString(locale: Locale, idsUsed: MutableList<String>): String {
        val tierStringBuilder = StringBuilder()
        for (flag in flags) {
            if (!idsUsed.contains(flag.id)) {
                if (flag.scope == FlagScope.GUILD) {
                    idsUsed.add(flag.id)
                    tierStringBuilder.append(" - **").append(flag.getName(locale)).append(":** ").append(flag.getDescription(locale)).append('\n')
                }
            }
        }
        for (extra in extras) {
            if (extra.scope == FlagScope.GUILD) { //Ignore t-shirt related extra stuff as that isn't a guild thing.
                val extraStr = getLanguage(locale)!!.getString(extra.path).orElse("No language string defined")
                tierStringBuilder.append(" - ").append(extraStr).append('\n')
            }
        }
        if (parent != null && parent != "default") {
            tierStringBuilder.apply {
                append('\n')
                append("**__Inherited from ").append(parent).append(":__**\n")
                append(tiers[parent]!!.getGuildTierString(locale, idsUsed))
            }
        }
        return tierStringBuilder.toString()
    }

    class TierExtra {
        @Getter
        val path: String?

        @Getter
        val scope: FlagScope?

        private constructor() {
            path = null
            scope = null
        }

        constructor(path: String?, scope: FlagScope?) {
            this.path = path
            this.scope = scope
        }
    }

    companion object {
        val tiers: MutableMap<String?, Tier> = HashMap()
        fun parseTiers() {
            val config: JSONConfig
            config = try {
                JSONConfig(CascadeBot::class.java.classLoader.getResourceAsStream("./default_tiers.json"))
            } catch (e: Exception) {
                // We have no default tiers :(
                CascadeBot.LOGGER.warn("The default tiers file was unable to be loaded!", e)
                return
            }
            for (tierName in config.getKeys(false)) {
                if (tierName.equals("example", ignoreCase = true)) continue
                tiers[tierName] = parseTier(config.getSubConfig(tierName).orElseThrow())
            }
        }

        private fun parseTier(config: JSONConfig): Tier {
            val parent = config.getString("parent").orElse(null)
            val extras: MutableList<TierExtra> = ArrayList()
            val extrasElement = config.getElement("extras").orElse(JsonArray())
            if (extrasElement.isJsonArray) {
                for (element in extrasElement.asJsonArray) {
                    val `object` = element.asJsonObject
                    val path = `object`["path"].asString
                    val scopeStr = `object`["scope"].asString
                    var scope: FlagScope? = null
                    when (scopeStr) {
                        "user" -> scope = FlagScope.USER
                        "guild" -> scope = FlagScope.GUILD
                    }
                    extras.add(TierExtra(path, scope))
                }
            }
            val flagsEle = config.getElement("flags").orElse(JsonArray())
            val flags: MutableSet<Flag> = HashSet()
            if (flagsEle.isJsonArray) {
                for (jsonElement in flagsEle.asJsonArray) {
                    val jsonObject = jsonElement.asJsonObject
                    val name = jsonObject["name"].asString
                    val scope = when (jsonObject["scope"].asString) {
                        "user" -> FlagScope.USER
                        "guild" -> FlagScope.GUILD
                        else -> FlagScope.USER
                    }
                    if (jsonObject.has("type")) {
                        when (jsonObject["type"].asString) {
                            "amount" -> flags.add(AmountFlag(name, scope).parseFlagData(jsonObject["data"].asJsonObject))
                            "time" -> flags.add(TimeFlag(name, scope).parseFlagData(jsonObject["data"].asJsonObject))
                        }
                    } else {
                        flags.add(Flag(name, scope))
                    }
                }
            }
            return Tier(parent, flags, extras)
        }

        fun getTier(id: String): Tier? {
            return tiers[id]
        }
    }

}