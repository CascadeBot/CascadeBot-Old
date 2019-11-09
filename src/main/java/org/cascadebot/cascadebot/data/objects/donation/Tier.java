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

        List<String> extras = new ArrayList<>();
        JsonElement extrasElement = config.getElement("extras").orElse(new JsonArray());
        if (extrasElement.isJsonArray()) {
            ((JsonArray) extrasElement).forEach(element -> extras.add(element.getAsString()));
        }

        JsonElement flagsEle = config.getElement("flags").orElse(new JsonArray());
        Set<Flag> flags = new HashSet<>();
        if (flagsEle.isJsonArray()) {
            for (JsonElement jsonElement : flagsEle.getAsJsonArray()) {
                if (jsonElement.isJsonPrimitive()) {
                    flags.add(new Flag(jsonElement.getAsString()));
                } else if (jsonElement.isJsonObject()) {
                    JsonObject object = jsonElement.getAsJsonObject();
                    String name = object.get("name").getAsString();
                    String type = object.get("type").getAsString();
                    switch (type) {
                        case "amount":
                            flags.add(new AmountFlag(name).parseFlagData(object.get("data").getAsJsonObject()));
                            break;
                        case "time":
                            flags.add(new TimeFlag(name).parseFlagData(object.get("data").getAsJsonObject()));
                    }
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
    private List<String> extras = new ArrayList<>();

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
        for (Flag flag : flags) {
            if (flag.getId().equals(id)) {
                return flag;
            }
        }
        if (!parent.isEmpty()) {
            return tiers.get(parent).getFlag(id);
        }
        return null;
    }

    /**
     * Returns the guild benefits gave in this tier.
     *
     * @param locale The locale to use for translations
     * @return The guild benefits gave in this tier
     */
    public String toTierString(Locale locale) {
        return "";
    }

    /**
     * Get's this tiers text as it would appear on patreon, and other front end stuffs.
     *
     * @param locale he locale to use for translations
     * @return This tiers text as it would appear on patreon, and other front end stuffs.
     */
    public String toDonateString(Locale locale) {
        return "";
    }
}
