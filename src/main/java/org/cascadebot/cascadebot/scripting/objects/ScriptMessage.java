package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
        CompletableFuture<Message> completableFuture = internalMessage.editMessage(message).override(true).submit();
        return scriptContext.handleMessageCompletableFuture(completableFuture);
    }

    public Promise editMessage(String message, boolean override) {
        CompletableFuture<Message> completableFuture = internalMessage.editMessage(message).override(override).submit();
        return scriptContext.handleMessageCompletableFuture(completableFuture);
    }

    public Promise editMessage(MessageEmbed messageEmbed) {
        CompletableFuture<Message> completableFuture = internalMessage.editMessage(messageEmbed).override(true).submit();
        return scriptContext.handleMessageCompletableFuture(completableFuture);
    }

    public Promise editMessage(MessageEmbed messageEmbed, boolean override) {
        CompletableFuture<Message> completableFuture = internalMessage.editMessage(messageEmbed).override(override).submit();
        return scriptContext.handleMessageCompletableFuture(completableFuture);
    }

    public Promise addReaction(String unicode) {
        CompletableFuture<Void> voidCompletableFuture = internalMessage.addReaction(unicode).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise addReaction(ScriptEmote emote) {
        CompletableFuture<Void> voidCompletableFuture = internalMessage.addReaction(emote.internalEmote).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise pin() {
        CompletableFuture<Void> voidCompletableFuture = internalMessage.pin().submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise unPin() {
        CompletableFuture<Void> voidCompletableFuture = internalMessage.unpin().submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public boolean isPinned() {
        return internalMessage.isPinned();
    }

    public Promise publish() {
        CompletableFuture<Message> completableFuture = internalMessage.crosspost().submit();
        return scriptContext.handleMessageCompletableFuture(completableFuture);
    }

    public Promise delete() {
        CompletableFuture<Void> voidCompletableFuture = internalMessage.delete().submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public void setInternalMessage(Message message) {
        internalMessage = message;
        internalSnowflake = message;
    }

}
