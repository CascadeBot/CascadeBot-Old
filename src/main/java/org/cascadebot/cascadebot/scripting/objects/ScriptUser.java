package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.util.List;
import java.util.stream.Collectors;

public class ScriptUser {

    private Member internalUser;

    public ScriptUser() {

    }

    public String getAsTag() {
        return internalUser.getUser().getAsTag();
    }

    public String getAvatarId() {
        return internalUser.getUser().getAvatarId();
    }

    public String getAvatarUrl() {
        return internalUser.getUser().getAvatarUrl();
    }

    public String getDefaultAvatarId() {
        return internalUser.getUser().getDefaultAvatarId();
    }

    public String getDefaultAvatarUrl() {
        return internalUser.getUser().getDefaultAvatarUrl();
    }

    public String getDiscriminator() {
        return internalUser.getUser().getDiscriminator();
    }

    public String getEffectiveAvatarUrl() {
        return internalUser.getUser().getEffectiveAvatarUrl();
    }

    public String getName() {
        return internalUser.getUser().getName();
    }

    public boolean isBot() {
        return internalUser.getUser().isBot();
    }

    public String getNickname() {
        return internalUser.getNickname();
    }

    public static ScriptUser getUser(Guild guild, String id) {
        User user = CascadeBot.INS.getShardManager().getUserById(id);
        if (user == null) {
            return null;
        } else {
            Member member = guild.getMember(user);
            ScriptUser scriptUser = new ScriptUser();
            scriptUser.internalUser = member;
            return scriptUser;
        }
    }

    protected void setInternalUser(Member member) {
        this.internalUser = member;
    }

}
