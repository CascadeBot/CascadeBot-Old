/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes

import net.dv8tion.jda.api.entities.TextChannel

enum class VoteFinishFunction(val function: (TextChannel, List<VoteResult>) -> Unit) {
    DUMMY({ _, _ -> });
}