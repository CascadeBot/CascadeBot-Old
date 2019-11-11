/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects.donation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.binaryoverload.JSONConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@ToString
public class Tier {

    @Getter
    private static Map<String, Tier> tiers = new HashMap<>();

    public static void parseTiers() {
        JSONConfig config;
        try {
            config = new JSONConfig(CascadeBot.class.getClassLoader().getResourceAsStream("./default_tiers.json"));
        } catch (Exception e) {
            // We have no default tiers :(
            CascadeBot.LOGGER.warn("The default tiers file was unable to be loaded!", e);
            return;
        }

        for (String tierName : config.getKeys(false)) {
            if (tierName.equalsIgnoreCase("example")) continue;
            tiers.put(tierName, parseTier(config.getSubConfig(tierName).orElseThrow()));
        }
    }

    private static Tier parseTier(JSONConfig config) {
        String parent = config.getString("parent").orElse(null);

        List<TierExtra> extras = new ArrayList<>();
        JsonElement extrasElement = config.getElement("extras").orElse(new JsonArray());
        if (extrasElement.isJsonArray()) {
            for (JsonElement element : extrasElement.getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                String path = object.get("path").getAsString();
                String scopeStr = object.get("scope").getAsString();
                Flag.FlagScope scope = null;
                switch (scopeStr) {
                    case "user":
                        scope = Flag.FlagScope.USER;
                        break;
                    case "guild":
                        scope = Flag.FlagScope.GUILD;
                        break;
                }

                extras.add(new TierExtra(path, scope));

            }
        }

        JsonElement flagsEle = config.getElement("flags").orElse(new JsonArray());
        Set<Flag> flags = new HashSet<>();
        if (flagsEle.isJsonArray()) {
            for (JsonElement jsonElement : flagsEle.getAsJsonArray()) {
                JsonObject object = jsonElement.getAsJsonObject();
                String name = object.get("name").getAsString();
                String scopeStr = object.get("scope").getAsString();
                Flag.FlagScope scope = null;
                switch (scopeStr) {
                    case "user":
                        scope = Flag.FlagScope.USER;
                        break;
                    case "guild":
                        scope = Flag.FlagScope.GUILD;
                        break;
                }
                if (object.has("type")) {
                    String type = object.get("type").getAsString();
                    switch (type) {
                        case "amount":
                            flags.add(new AmountFlag(name, scope).parseFlagData(object.get("data").getAsJsonObject()));
                            break;
                        case "time":
                            flags.add(new TimeFlag(name, scope).parseFlagData(object.get("data").getAsJsonObject()));
                    }
                } else {
                    flags.add(new Flag(name, scope));
                }
            }
        }
        return new Tier(parent, flags, extras);
    }

    public static Tier getTier(String id) {
        return tiers.get(id);
    }

    /**
     * Parent tier
     */
    private String parent;

    /**
     * This is the list of flags the the bot checks agents for actions
     */
    private Set<Flag> flags;

    /**
     * This is a list of ids (for use with lang) of other benefits giving at this tier
     */
    @Getter
    private List<TierExtra> extras = new ArrayList<>();

    private Tier() {
        //default constructor for mongo.
    }

    public Tier(Set<Flag> flags) {
        this.flags = flags;
    }

    public void addFlag(Flag flag) {
        flags.add(flag);
    }

    public Tier getParent() {
        if (parent == null) return null;
        return tiers.get(parent);
    }

    public Set<Flag> getFlags() {
        Set<Flag> flags = new HashSet<>(this.flags);
        if (getParent() != null) {
            flags.addAll(getParent().getFlags());
        }
        return flags;
    }

    public Flag getFlag(String id) {
        Flag return_flag = flags.stream().filter(flag -> flag.getId().equals(id)).findFirst().orElse(null);
        if (parent != null && return_flag == null) {
            return_flag = tiers.get(parent).getFlag(id);
        }
        return return_flag;
    }

    public boolean isTierParent(String tier) {
        if (this.parent == null) {
            return false;
        }
        if (this.parent.equals(tier)) {
            return true;
        }
        if (this.parent.isEmpty()) {
            return false;
        }
        return tiers.get(parent).isTierParent(tier);
    }

    /**
     * Returns the benefits gave in this tier to a guild.
     *
     * @param locale The locale to use for translations
     * @return The guild benefits gave in this tier
     */
    public String getGuildTierString(Locale locale, List<String> idsUsed) {
        if (idsUsed == null) {
            idsUsed = new ArrayList<>();
        }
        StringBuilder tierStringBuilder = new StringBuilder();
        for (Flag flag : flags) {
            if (!idsUsed.contains(flag.getId())) {
                if (flag.scope.equals(Flag.FlagScope.GUILD)) {
                    idsUsed.add(flag.getId());
                    tierStringBuilder.append(" - **").append(flag.getName(locale)).append(":** ").append(flag.getDescription(locale)).append('\n');
                }
            }
        }
        for (TierExtra extra : extras) {
            if (extra.scope.equals(Flag.FlagScope.GUILD)) { //Ignore t-shirt related extra stuff as that isn't a guild thing.
                String extraStr = Language.getLanguage(locale).getString(extra.path).orElse("No language string defined");
                tierStringBuilder.append(" - ").append(extraStr).append('\n');
            }
        }

        if (parent != null && !parent.equals("default")) {
            tierStringBuilder.append('\n');
            tierStringBuilder.append("**__Inherited from ").append(parent).append(":__**\n");
            tierStringBuilder.append(tiers.get(parent).getGuildTierString(locale, idsUsed));
        }

        return tierStringBuilder.toString();
    }

    /*
     * Get's this tiers text as it would appear on patreon, and other front end stuffs.
     *
     * @param locale he locale to use for translations
     * @return This tiers text as it would appear on patreon, and other front end stuffs.
     */
    /*
    public String toDonateString(Locale locale) {
        return "";
    }
     */

    public static class TierExtra {

        @Getter
        protected final String path;
        @Getter
        protected final Flag.FlagScope scope;

        protected TierExtra() {
            this.path = null;
            this.scope = null;
        }

        public TierExtra(String path, Flag.FlagScope scope) {
            this.path = path;
            this.scope = scope;
        }

    }

}
