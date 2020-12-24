package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Message;
import org.cascadebot.cascadebot.scripting.Promise;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ScriptMessage extends ScriptSnowflake {

    private Message internalMessage;

    public ScriptMessage(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public Promise editMessage(String message) {
        CompletableFuture<Message> completableFuture = internalMessage.editMessage(message).override(true).submit(); // TODO not override, let user change these options
        return handleMessageCompletableFuture(scriptContext, completableFuture);
    }

    public static Promise handleMessageCompletableFuture(ScriptContext scriptContext, CompletableFuture<Message> messageCompletableFuture) {
        scriptContext.addFuture(messageCompletableFuture);
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                messageCompletableFuture.whenComplete((lMessage, throwable) -> {
                    if (throwable != null) {
                        reject.executeVoid(throwable);
                    } else {
                        ScriptMessage scriptMessage = new ScriptMessage(scriptContext);
                        scriptMessage.setInternalMessage(lMessage);
                        resolve.executeVoid(scriptMessage);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public void setInternalMessage(Message message) {
        internalMessage = message;
        internalSnowflake = message;
    }

}
