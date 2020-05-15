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
        CascadeBot.INS.getMessageReceivedRunnable().getQueue().add(event);
    }

}
