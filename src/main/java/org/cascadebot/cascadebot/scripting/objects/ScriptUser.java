package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.util.List;
import java.util.stream.Collectors;

public class ScriptUser {

    public User internalUser;

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

    public String getName() {
        return internalUser.getName();
    }

    public boolean isBot() {
        return internalUser.isBot();
    }

    public static ScriptUser getUser(String id) {
        User user = CascadeBot.INS.getShardManager().getUserById(id);
        if (user == null) {
            return null;
        } else {
            ScriptUser scriptUser = new ScriptUser();
            scriptUser.internalUser = user;
            return scriptUser;
        }
    }

}
