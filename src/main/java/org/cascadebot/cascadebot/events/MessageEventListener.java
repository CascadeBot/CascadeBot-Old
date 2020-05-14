package org.cascadebot.cascadebot.events;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.utils.CryptUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MessageEventListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (Config.INS.getEncryptKey() != null) {
            try {
                CryptUtils.EncryptResults results = CryptUtils.encryptString(Config.INS.getEncryptKey(), Config.INS.getIvSpec(), Config.INS.getMac(), event.getMessage().getContentRaw());
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("sender", new JsonPrimitive(event.getMember().getIdLong()));
                jsonObject.add("content", CascadeBot.getGSON().toJsonTree(results));
                String json = CascadeBot.getGSON().toJson(jsonObject);
                CascadeBot.INS.getRedisClient().set(event.getMessageId(), json);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | ShortBufferException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException ignored) {
                CascadeBot.LOGGER.warn("Failed to encrypt", ignored);
            }
        } else {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("sender", new JsonPrimitive(event.getMember().getIdLong()));
            jsonObject.add("content", CascadeBot.getGSON().toJsonTree(event.getMessage().getContentRaw()));
            String json = CascadeBot.getGSON().toJson(jsonObject);
            CascadeBot.INS.getRedisClient().set(event.getMessageId(), json);
        }
    }

}
