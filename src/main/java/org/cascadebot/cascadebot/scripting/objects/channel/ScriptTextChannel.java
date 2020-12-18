package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.scripting.Promise;
import org.cascadebot.cascadebot.scripting.objects.ScriptContext;
import org.cascadebot.cascadebot.scripting.objects.ScriptMessage;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

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
        /*Value global = scriptContext.getPolyContext().getBindings("js");
        Value promiseConstructor = global.getMember("Promise");
        return promiseConstructor.newInstance((ProxyExecutable) arguments -> {
            Value resolve = arguments[0];
            Value reject = arguments[1];
            completableFuture.whenComplete((discordMessage, exception) -> {
                if (exception == null) {
                    ScriptMessage scriptMessage = new ScriptMessage(scriptContext);
                    scriptMessage.setInternalMessage(discordMessage);
                    resolve.executeVoid(scriptMessage);
                } else {
                    reject.executeVoid(exception);
                }
            });
            return null;
        });*/

        return (success, error) -> completableFuture.whenComplete((discordMessage, exception) -> {
            if (exception == null) {
                ScriptMessage scriptMessage = new ScriptMessage(scriptContext);
                scriptMessage.setInternalMessage(discordMessage);
                success.executeVoid(scriptMessage);
            } else {
                error.executeVoid(exception);
            }
        });
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
