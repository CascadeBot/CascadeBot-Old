/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects.donation;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cascadebot.cascadebot.data.language.Locale;

@AllArgsConstructor
public class Tier {

    private static List<Tier> tiers = new ArrayList<>();

    public static void buildTiers() {
        //TODO Should i move this else where?
        List<Flag> flags = new ArrayList<>();
        flags.add(new FlagWithAmount("custom_commands", 3)); //TODO store flags in list so they can be accessed elsewhere
        flags.add(new FlagWithAmount("prefix_length", 5));
        Tier defaultTier = new Tier(flags);
        tiers.add(defaultTier);
    }

    public static Tier getTier(int index) {
        return tiers.get(index);
    }

    /**
     * Parent tier
     */
    @Getter
    private Tier parent;

    /**
     * This is the list of flags the the bot checks agents for actions
     */
    @Getter
    private List<Flag> flags;

    /**
     * This is a list of ids (for use with lang) of other benefits giving at this tier
     */
    @Getter
    private List<String> otherBenefits = new ArrayList<>();

    public Tier(List<Flag> flags) {
        this.flags = flags;
    }

    public void addFlag(Flag flag) {
        flags.add(flag);
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
