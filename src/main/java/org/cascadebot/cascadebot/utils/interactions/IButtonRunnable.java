/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.interactions;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

@FunctionalInterface
public interface IButtonRunnable {

    void run(Member runner, TextChannel channel, InteractionMessage message);

}
