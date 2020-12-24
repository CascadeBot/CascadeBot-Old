package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptTextChannel;
import org.graalvm.polyglot.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ScriptContext {

    private String[] args;
    private ScriptTextChannel channel;
    private ScriptUser user;
    private ScriptGuild guild;
    private String message;

    private List<CompletableFuture<?>> futures = new ArrayList<>();

    private Context polyContext;

    public ScriptContext(String[] args, TextChannel textChannel, Member member, Guild guild, String message) {
        this.args = args;

        this.user = new ScriptUser(this);
        this.user.setInternalUser(member);

        this.channel = new ScriptTextChannel(this);
        this.channel.setInternalTextChannel(textChannel);

        this.guild = new ScriptGuild(this);
        this.guild.setInternalGuild(guild);

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

    public ScriptGuild getGuild() {
        return guild;
    }

    public Context getPolyContext() {
        return polyContext;
    }
}
