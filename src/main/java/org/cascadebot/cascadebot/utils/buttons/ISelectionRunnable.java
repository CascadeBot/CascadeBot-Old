package org.cascadebot.cascadebot.utils.buttons;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public interface ISelectionRunnable {

    void run(Member runner, TextChannel channel, Message message, List<String> selected);

}
