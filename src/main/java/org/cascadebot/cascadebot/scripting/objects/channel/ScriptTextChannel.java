package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.scripting.objects.ScriptContext;
import org.cascadebot.cascadebot.scripting.objects.ScriptMessage;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ScriptTextChannel extends ScriptChannel {

    protected TextChannel internalTextChannel;

    public ScriptTextChannel(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public void sendMessage(String message) {
        sendMessage(message, null);
    }

    public void sendMessage(String message, Consumer<ScriptMessage> messageConsumer) {
        CompletableFuture<Message> completableFuture = internalTextChannel.sendMessage(message).submit();
        scriptContext.addFuture(completableFuture);
        if (messageConsumer != null) {
            completableFuture.thenAccept(discordMessage -> {
                ScriptMessage scriptMessage = new ScriptMessage(scriptContext);
                scriptMessage.setInternalMessage(discordMessage);
                messageConsumer.accept(scriptMessage);
            });
        }
    }

    public void getMessageById(String id, Consumer<ScriptMessage> messageConsumer) {
        CompletableFuture<Message> completableFuture = internalTextChannel.retrieveMessageById(id).submit();
        scriptContext.addFuture(completableFuture);
        completableFuture.thenAccept(message -> {
            ScriptMessage scriptMessage = new ScriptMessage(scriptContext);
            scriptMessage.setInternalMessage(message);
            messageConsumer.accept(scriptMessage);
        });
    }

    public void setInternalTextChannel(TextChannel textChannel) {
        internalTextChannel = textChannel;
        internalChannel = textChannel;
        internalSnowflake = textChannel;
    }

}
