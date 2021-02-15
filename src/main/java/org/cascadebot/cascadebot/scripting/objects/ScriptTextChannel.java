package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.cascadebot.cascadebot.scripting.Promise;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ScriptTextChannel extends ScriptChannel {

    protected TextChannel internalTextChannel;

    public ScriptTextChannel(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public Promise sendMessage(String message) {
        CompletableFuture<Message> completableFuture = internalTextChannel.sendMessage(message).submit();
        return scriptContext.handleMessageCompletableFuture(completableFuture);
    }

    public Promise sendMessage(MessageEmbed embed) {
        CompletableFuture<Message> completableFuture = internalTextChannel.sendMessage(embed).submit();
        return scriptContext.handleMessageCompletableFuture(completableFuture);
    }

    public String getTopic() {
        return internalTextChannel.getTopic();
    }

    public boolean isNSFW() {
        return internalTextChannel.isNSFW();
    }

    public boolean isNews() {
        return internalTextChannel.isNews();
    }

    public Promise getWebhooks() {
        CompletableFuture<List<Webhook>> completableFuture = internalTextChannel.retrieveWebhooks().submit();

        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                completableFuture.whenComplete((webhooks, throwable) -> {
                    if (throwable == null) {
                        resolve.executeVoid(webhooks.stream().map(webhook -> {
                            ScriptWebhook scriptWebhook = new ScriptWebhook(scriptContext);
                            scriptWebhook.setInternalWebhook(webhook);
                            return scriptWebhook;
                        }).collect(Collectors.toList()));
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise createWebhook(String name) {
        CompletableFuture<Webhook> completableFuture = internalTextChannel.createWebhook(name).submit();

        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                completableFuture.whenComplete((webhook, throwable) -> {
                    if (throwable == null) {
                        ScriptWebhook scriptWebhook = new ScriptWebhook(scriptContext);
                        scriptWebhook.setInternalWebhook(webhook);
                        resolve.executeVoid(scriptWebhook);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public boolean canTalk() {
        return internalTextChannel.canTalk();
    }

    public Promise setTopic(String topic) {
        CompletableFuture<Void> voidCompletableFuture = internalTextChannel.getManager().setTopic(topic).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise setNSFW(boolean nsfw) {
        CompletableFuture<Void> voidCompletableFuture = internalTextChannel.getManager().setNSFW(nsfw).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise getMessageById(String id) {
        CompletableFuture<Message> completableFuture = internalTextChannel.retrieveMessageById(id).submit();
        scriptContext.addFuture(completableFuture);

        return scriptContext.handleMessageCompletableFuture(completableFuture);
    }

    public void setInternalTextChannel(TextChannel textChannel) {
        internalTextChannel = textChannel;
        internalChannel = textChannel;
        internalSnowflake = textChannel;
    }

}
