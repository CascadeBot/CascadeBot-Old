package org.cascadebot.cascadebot.runnables;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.ShutdownHandler;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.utils.CryptUtils;
import org.cascadebot.cascadebot.utils.PasteUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageReceivedRunnable implements Runnable {

    private BlockingQueue<GuildMessageReceivedEvent> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while (!ShutdownHandler.SHUTDOWN_LOCK.get()) {
            try {
                GuildMessageReceivedEvent event = queue.take();
                if (event.getMember() == null) {
                    return;
                }
                String message = "";
                if (Config.INS.getEncryptKey() != null) {
                    try {
                        byte[] results = CryptUtils.encryptString(Config.INS.getEncryptKey(), Config.INS.getIvSpec(), event.getMessage().getContentRaw());
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.add("sender", new JsonPrimitive(event.getMember().getIdLong()));
                        jsonObject.add("content", CascadeBot.getGSON().toJsonTree(results));
                        message = CascadeBot.getGSON().toJson(jsonObject);
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | ShortBufferException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException ignored) {
                        CascadeBot.LOGGER.warn("Failed to encrypt", ignored);
                        return;
                    }
                } else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("sender", new JsonPrimitive(event.getMember().getIdLong()));
                    jsonObject.add("content", CascadeBot.getGSON().toJsonTree(event.getMessage().getContentRaw()));
                    message = CascadeBot.getGSON().toJson(jsonObject);
                }
                CascadeBot.INS.getRedisClient().set("message:" + event.getMessageId(), message);
                CascadeBot.INS.getRedisClient().expire("message:" + event.getMessageId(), (int) TimeUnit.HOURS.toSeconds(24));
            } catch (InterruptedException e) {
                CascadeBot.LOGGER.warn("Message thread interrupted: " + PasteUtils.getStackTrace(e));
            }
        }
    }

    public BlockingQueue<GuildMessageReceivedEvent> getQueue() {
        return queue;
    }

}
