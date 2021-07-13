/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.buttons;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.utils.interactions.InteractionMessage;

@FunctionalInterface
public interface IButtonRunnable {

    void run(Member runner, TextChannel channel, InteractionMessage message);

}
