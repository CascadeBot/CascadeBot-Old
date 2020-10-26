package org.cascadebot.cascadebot.runnables;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.User;
import org.bouncycastle.math.raw.Mod;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.ModlogEventStore;
import org.cascadebot.cascadebot.moderation.ModlogEmbedField;
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.utils.ModlogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ModlogChannelMoveCollectorRunnable implements Runnable {

    private Guild guild;
    private Runnable finishRunnable;

    public ModlogChannelMoveCollectorRunnable(Guild guild, Runnable finishRunnable) {
        this.guild = guild;
        this.finishRunnable = finishRunnable;
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
        finishRunnable.run();
        ModlogUtils.getAuditLogFromType(guild, auditLogEntry -> {
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            }
            int maxDistance = 0;
            List<ChannelMoveData> maxMoveDatas = new ArrayList<>();
            for (ChannelMoveData moveData : channelMoveDataList) {
                if (moveData == null) {
                    continue;
                }
                int distance = Math.abs(moveData.channel.getPosition() - moveData.oldPos);
                if (distance == maxDistance) {
                    maxMoveDatas.add(moveData);
                } else if (distance > maxDistance) {
                    maxMoveDatas = new ArrayList<>();
                    maxDistance = distance;
                    maxMoveDatas.add(moveData);
                }
            }

            GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
            if (maxMoveDatas.size() == 1) {
                ChannelMoveData data = maxMoveDatas.get(0);
                ModlogEmbedField field = new ModlogEmbedField(false,
                        "modlog.general.position",
                        "modlog.general.small_change",
                        String.valueOf(data.oldPos),
                        String.valueOf(data.channel.getPosition()));
                ModlogEventStore eventStore = new ModlogEventStore(ModlogEvent.CHANNEL_POSITION_UPDATED, responsible, maxMoveDatas.get(0).channel, List.of(field));
                guildData.getModeration().sendModlogEvent(guild.getIdLong(), eventStore);
            } else {
                List<ModlogEmbedPart> embedParts = new ArrayList<>();
                for (ChannelMoveData data : maxMoveDatas) {
                    ModlogEmbedField field = new ModlogEmbedField(false, "modlog.channel.position.title",
                            "modlog.general.small_change",
                            String.valueOf(data.oldPos), String.valueOf(data.channel.getPosition()));
                    field.addTitleObjects(data.channel.getName());
                    embedParts.add(field);
                }
                ModlogEventStore eventStore = new ModlogEventStore(ModlogEvent.MULTIPLE_CHANNEL_POSITION_UPDATED, responsible, maxMoveDatas.get(0).channel, embedParts);
                guildData.getModeration().sendModlogEvent(guild.getIdLong(), eventStore);
            }



        }, ActionType.CHANNEL_UPDATE);
    }

    public BlockingQueue<ChannelMoveData> getQueue() {
        return queue;
    }

    public static class ChannelMoveData {

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
