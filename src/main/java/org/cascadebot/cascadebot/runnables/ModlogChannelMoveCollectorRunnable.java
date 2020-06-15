package org.cascadebot.cascadebot.runnables;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.utils.ModlogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ModlogChannelMoveCollectorRunnable implements Runnable {

    private Guild guild;
    private Consumer<Void> finishedConsumer;

    public ModlogChannelMoveCollectorRunnable(Guild guild, Consumer<Void> finishedConsumer) {
        this.guild = guild;
        this.finishedConsumer = finishedConsumer;
    }

    private BlockingQueue<ChannelMoveData> queue = new LinkedBlockingQueue<>();
    private List<ChannelMoveData> channelMoveDataList = new ArrayList<>();

    @Override
    public void run() {
        boolean collecting = true;
        while (collecting) {
            try {
                ChannelMoveData moveData = queue.poll(500, TimeUnit.MILLISECONDS);
                channelMoveDataList.add(moveData);
                if (moveData == null) {
                    collecting = false;
                    afterCollected();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void afterCollected() {
        finishedConsumer.accept(null);
        ModlogUtils.getAuditLogFromType(guild, auditLogEntry -> {
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            }

        }, ActionType.CHANNEL_UPDATE);
    }

    static class ChannelMoveData {

        private ChannelType type;
        private int oldPos;
        private GuildChannel channel;

        public ChannelMoveData(ChannelType type, int oldPos, GuildChannel channel) {
            this.type = type;
            this.oldPos = oldPos;
            this.channel = channel;
        }

        public ChannelType getType() {
            return type;
        }

        public int getOldPos() {
            return oldPos;
        }

        public GuildChannel getChannel() {
            return channel;
        }

    }

}
