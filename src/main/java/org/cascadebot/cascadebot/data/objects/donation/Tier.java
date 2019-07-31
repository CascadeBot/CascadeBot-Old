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
        List<Flag> flags = new ArrayList<>();
        flags.add(new FlagWithAmount("custom_commands", 3));
        flags.add(new FlagWithAmount("prefix_length", 5));
        Tier defaultTier = new Tier(flags);
        tiers.add(defaultTier);

        flags = new ArrayList<>();
        flags.add(new FlagWithAmount("custom_commands", 7));
        flags.add(new FlagWithAmount("prefix_length", 1000));
        flags.add(new Flag("music_node"));
        flags.add(new Flag("voice_stay"));
        Tier fiveDollarTier = new Tier(defaultTier, flags, new ArrayList<>());
        tiers.add(fiveDollarTier);

        flags = new ArrayList<>();
        flags.add(new FlagWithAmount("custom_commands", 15));
        flags.add(new Flag("companion_bot"));
        Tier sevenDollarTier = new Tier(fiveDollarTier, flags, new ArrayList<>());
        tiers.add(sevenDollarTier);

        flags = new ArrayList<>();
        flags.add(new FlagWithAmount("custom_commands", 20));
        Tier tenDollarTier = new Tier(sevenDollarTier, flags, new ArrayList<>());
        tiers.add(tenDollarTier);

        flags = new ArrayList<>();
        flags.add(new FlagWithAmount("custom_commands", 77));
        Tier thirtyFiveDollarTier = new Tier(tenDollarTier, flags, new ArrayList<>());
        tiers.add(thirtyFiveDollarTier);
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

    private Tier() {
        //default constructor for mongo.
    }

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
