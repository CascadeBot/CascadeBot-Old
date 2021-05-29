package org.cascadebot.cascadebot.runnables;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.ShutdownHandler;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.utils.CryptUtils;
import org.cascadebot.cascadebot.utils.PasteUtils;
import org.cascadebot.cascadebot.utils.SerializableMessage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageReceivedRunnable implements Runnable {

    private BlockingQueue<GuildMessageReceivedEvent> queue = new LinkedBlockingQueue<>();

    private static MessageReceivedRunnable instance;

    static {
        instance = new MessageReceivedRunnable();
    }

    @Override
    public void run() {
        while (!ShutdownHandler.SHUTDOWN_LOCK.get()) {
            try {
                GuildMessageReceivedEvent event = queue.take();
                if (event.getMember() == null) {
                    continue;
                }
                if (CascadeBot.INS.getRedisClient() == null) {
                    continue;
                }
                SerializableMessage serializableMessage = SerializableMessage.createSerializeMessageFromJda(event.getMessage());
                String messageJson = CascadeBot.getGSON().toJson(serializableMessage);
                String message = "";
                if (Config.INS.getEncryptKey() != null) {
                    try {
                        byte[] messageId = ByteBuffer.allocate(Long.BYTES).putLong(event.getMessage().getIdLong()).array();
                        byte[] iv = new byte[messageId.length * 2];
                        System.arraycopy(messageId, 0, iv, 0, messageId.length);
                        System.arraycopy(messageId, 0, iv, messageId.length, messageId.length);
                        byte[] results = CryptUtils.encryptString(Config.INS.getEncryptKey(), iv, messageJson);
                        message = CascadeBot.getGSON().toJson(CascadeBot.getGSON().toJsonTree(results));
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | ShortBufferException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException exception) {
                        CascadeBot.LOGGER.warn("Failed to encrypt", exception);
                        continue;
                    }
                } else {
                    message = messageJson;
                }
                CascadeBot.INS.getRedisClient().setex("message:" + event.getMessageId(), (int) TimeUnit.HOURS.toSeconds(24), message);
            } catch (InterruptedException e) {
                CascadeBot.LOGGER.warn("Message thread interrupted: " + PasteUtils.getStackTrace(e));
                return;
            }
        }
    }

    public BlockingQueue<GuildMessageReceivedEvent> getQueue() {
        return queue;
    }

    public static MessageReceivedRunnable getInstance() {
        return instance;
    }

}
