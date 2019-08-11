package org.cascadebot.cascadebot.data.graphql.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import spark.Request;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class QLContext {

    private final Request request;
    private final User user;
    private final Guild guild;
    private final boolean authenticated;

    // TODO: Add methods to check that user is authenticated for this guild

    public <T> T runIfAuthenticatedUser(Function<User, T> function) {
        if (!authenticated) return null;
        if (user != null) return function.apply(user);
        // TODO: Throw errors and do permission checks
        return null;
    }

    public void runIfAuthenticatedUser(Consumer<User> consumer) {
        if (!authenticated) return;
        if (user != null) consumer.accept(user);
        // TODO: Throw errors and do permission checks
    }

    public <T> T runIfAuthenticatedGuild(Function<Member, T> function) {
        if (!authenticated) return null;
        if (user != null && guild.getMember(user) != null) return function.apply(guild.getMember(user));
        // TODO: Throw errors and do permission checks
        return null;
    }

    public void runIfAuthenticatedGuild(Consumer<Member> consumer) {
        if (!authenticated) return;
        if (user != null && guild.getMember(user) != null) consumer.accept(guild.getMember(user));
        // TODO: Throw errors and do permission checks
    }

    public GuildData getGuildData() {
        return GuildDataManager.getGuildData(guild.getIdLong());
    }

    public enum AuthenticationLevel {
        GUILD, USER
    }

}
