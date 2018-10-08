package com.cascadebot.cascadebot.tasks;

import com.cascadebot.cascadebot.CascadeBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class AutoDeleteMessageTask implements Task {

    private Message message;
    private long delay;

    public AutoDeleteMessageTask(Message message, long delay) throws PermissionException {
        this.message = message;
        this.delay = delay;
        if(message.getChannel().getType().isGuild()) {
            TextChannel channel = message.getTextChannel();
            if(channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) {
                throw new PermissionException("Don't have permission to delete this message");
            }
        } else {
            if(message.getAuthor().getIdLong() != CascadeBot.instance().getShardManager().getShardById(0).getSelfUser().getIdLong()) {
                throw new PermissionException("Don't have permission to delete this message");
            }
        }
    }

    @Override
    public void execute() {

    }

    @Override
    public long delay() {
        return delay;
    }
}
