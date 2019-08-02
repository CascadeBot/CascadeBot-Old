package org.cascadebot.cascadebot.data.graphql.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import spark.Request;

import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class QLContext {

    private final Request request;
    private final User user;
    private final Guild guild;
    private final boolean authenticated;

    // TODO: Add methods to check that user is authenticated for this guild

    public <T> T runIfAuthenticated(AuthenticationLevel level, Supplier<T> runnable) {
        if (!authenticated) return null;
        if (level == AuthenticationLevel.USER && user != null) return runnable.get();
        if (level == AuthenticationLevel.GUILD && user != null && guild.getMember(user) != null) return runnable.get();
        // TODO: Throw errors and do permission checks
        return null;
    }

    public void runIfAuthenticated(AuthenticationLevel level, Runnable runnable) {
        if (!authenticated) return;
        if (level == AuthenticationLevel.USER && user != null) runnable.run();
        if (level == AuthenticationLevel.GUILD && user != null && guild.getMember(user) != null) runnable.run();
        // TODO: Throw errors and do permission checks
    }

    public GuildData getGuildData() {
        return GuildDataManager.getGuildData(guild.getIdLong());
    }

    public enum AuthenticationLevel {
        GUILD, USER
    }

}
