package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.scripting.Promise;
import org.cascadebot.cascadebot.scripting.objects.ScriptContext;
import org.cascadebot.cascadebot.scripting.objects.ScriptMessage;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ScriptTextChannel extends ScriptChannel {

    protected TextChannel internalTextChannel;

    public ScriptTextChannel(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public Promise sendMessage(String message) {
        CompletableFuture<Message> completableFuture = internalTextChannel.sendMessage(message).submit();
        scriptContext.addFuture(completableFuture);

        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                completableFuture.whenComplete((lMessage, throwable) -> {
                    if (throwable != null) {
                        reject.executeVoid(throwable);
                    } else {
                        resolve.executeVoid(lMessage);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
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
