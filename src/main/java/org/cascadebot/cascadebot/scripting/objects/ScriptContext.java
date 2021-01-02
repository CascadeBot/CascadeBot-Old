package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.scripting.Promise;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ScriptContext {

    private String[] args;
    private ScriptTextChannel channel;
    private Member runner;
    private ScriptUser user;
    private ScriptGuild guild;
    private Guild intGuild;
    private String message;

    private List<CompletableFuture<?>> futures = new ArrayList<>();

    private Context polyContext;

    private ScriptContext instance = this;

    public ScriptContext(String[] args, TextChannel textChannel, Member member, Guild guild, String message) {
        this.args = args;

        this.user = new ScriptUser(this);
        this.user.setInternalUser(member);
        this.runner = member;

        this.channel = new ScriptTextChannel(this);
        this.channel.setInternalTextChannel(textChannel);

        this.guild = new ScriptGuild(this);
        this.guild.setInternalGuild(guild);
        this.intGuild = guild;

        this.message = message;
    }

    public TextChannel getChannel() {
        return (TextChannel) channel.internalSnowflake;
    }

    public Map<String, Object> getVariableMap() {
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("args", args);
        variableMap.put("channel", channel);
        variableMap.put("user", user);
        variableMap.put("guild", guild);
        variableMap.put("message", message);

        return variableMap;
    }

    public void addFuture(CompletableFuture<?> future) {
        futures.add(future);
    }

    public boolean cancelAllFutures() {
        boolean canceledAny = false;
        for (CompletableFuture<?> future : futures) {
            if (!future.isDone()) {
                future.cancel(true);
                canceledAny = true;
            }
        }
        return canceledAny;
    }

    public void setPolyContext(Context polyContext) {
        this.polyContext = polyContext;
    }

    public Guild getGuild() {
        return intGuild;
    }

    public Context getPolyContext() {
        return polyContext;
    }

    public Promise handleVoidCompletableFuture(CompletableFuture<Void> voidCompletableFuture) {
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                voidCompletableFuture.whenComplete((unused, throwable) -> {
                    if (throwable == null) {
                        resolve.executeVoid(true);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise handleMessageCompletableFuture(CompletableFuture<Message> messageCompletableFuture) {
        this.addFuture(messageCompletableFuture);
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                messageCompletableFuture.whenComplete((lMessage, throwable) -> {
                    if (throwable != null) {
                        reject.executeVoid(throwable);
                    } else {
                        ScriptMessage scriptMessage = new ScriptMessage(instance);
                        scriptMessage.setInternalMessage(lMessage);
                        resolve.executeVoid(scriptMessage);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Member getRunner() {
        return runner;
    }
}
