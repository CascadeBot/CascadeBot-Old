package org.cascadebot.cascadebot.utils.buttons;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.utils.interactions.InteractionMessage;

import java.util.List;

public interface ISelectionRunnable {

    void run(Member runner, TextChannel channel, InteractionMessage message, List<String> selected);

}
