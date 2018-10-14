/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils.buttons;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class ButtonGroup {

    private List<Button> buttons;
    private long ownerId;
    private long guildId;

    public ButtonGroup(long ownerId, long guildId) {
        buttons = new ArrayList<>();
        this.ownerId = ownerId;
        this.guildId = guildId;
    }

    public void addButton(Button button) {
        buttons.add(button);
    }

    public Member getOwnner() {
        return DiscordUtils.getMember(String.valueOf(ownerId), CascadeBot.instance().getClient().getGuildById(guildId)); //TODO util method for getting guild from id
    }

    public List<Button> getButtons() {
        return buttons;
    }
}
