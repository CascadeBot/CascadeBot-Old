/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

public enum VoteMessageType {

    /**
     * This vote type will use ✅ (\u2705) and ❌ (\u274C) for votes
     */
    YES_NO,

    /**
     * This vote type will use discord regional indicator numbers up to the amount specified in the builder.
     */
    NUMBERS,

    /**
     * This vote type will use discord regional indicator letters up to the amount specified in the builder.
     */
    LETTERS,

    /**
     * This vote type will give you complete control over what reactions you want to use .
     */
    CUSTOM

}
