package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.scripting.Promise;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ScriptCategory extends ScriptChannel {

    private Category internalCategory;

    public ScriptCategory(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public List<ScriptChannel> getChannels() {
        return internalCategory.getChannels().stream()
                .map(guildChannel -> ScriptChannel.fromJda(scriptContext, guildChannel))
                .collect(Collectors.toList());
    }

    public Promise createTextChannel(String name) {
        CompletableFuture<TextChannel> channelCompletableFuture = internalCategory.createTextChannel(name).submit();
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                channelCompletableFuture.whenComplete((textChannel, throwable) -> {
                    if (throwable == null) {
                        ScriptTextChannel scriptTextChannel = new ScriptTextChannel(scriptContext);
                        scriptTextChannel.setInternalTextChannel(textChannel);
                        resolve.executeVoid(scriptTextChannel);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise createVoiceChannel(String name) {
        CompletableFuture<VoiceChannel> channelCompletableFuture = internalCategory.createVoiceChannel(name).submit();
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                channelCompletableFuture.whenComplete((voiceChannel, throwable) -> {
                    if (throwable == null) {
                        ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel(scriptContext);
                        scriptVoiceChannel.setInternalVoiceChannel(voiceChannel);
                        resolve.executeVoid(scriptVoiceChannel);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public void setInternalCategory(Category category) {
        internalCategory = category;
        internalChannel = category;
        internalSnowflake = category;
    }

}
