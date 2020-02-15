package org.cascadebot.cascadebot.scripting.objects;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.scripting.ScriptContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.util.List;
import java.util.stream.Collectors;

public class ScriptUser extends ScriptableObject {

    public ScriptUser() {

    }

    public String getAsTag() {
        return internalUser.getAsTag();
    }

    public String getAvatarId() {
        return internalUser.getAvatarId();
    }

    public String getAvatarUrl() {
        return internalUser.getAvatarUrl();
    }

    public String getDefaultAvatarId() {
        return internalUser.getDefaultAvatarId();
    }

    public String getDefaultAvatarUrl() {
        return internalUser.getDefaultAvatarUrl();
    }

    public String getDiscriminator() {
        return internalUser.getDiscriminator();
    }

    public String getEffectiveAvatarUrl() {
        return internalUser.getEffectiveAvatarUrl();
    }

    public List<String> getMutualGuilds() {
        return internalUser.getMutualGuilds().stream().map(Guild::getName).collect(Collectors.toList());
    }

    @JSFunction
    public String getName() {
        return internalUser.getName();
    }

    public boolean isBot() {
        return internalUser.isBot();
    }

    private

    @JSStaticFunction
    public static ScriptUser getUser(String id) {
        User user = CascadeBot.INS.getShardManager().getUserById(id);
        if (user == null) {
            return null;
        } else {
            return new ScriptUser();
        }
    }

    @Override
    public String getClassName() {
        return "User";
    }

    @Override
    public Object getDefaultValue(Class<?> typeHint) {
        return "[object ScriptUser]";
    }

}
