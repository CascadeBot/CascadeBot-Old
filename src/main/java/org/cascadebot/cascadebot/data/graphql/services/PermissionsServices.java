package org.cascadebot.cascadebot.data.graphql.services;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.graphql.objects.QLContext;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.Result;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionsServices {

    @Getter
    private static PermissionsServices instance = new PermissionsServices();

    @GraphQLQuery
    public Set<CascadePermission> allPermissions() {
        return CascadeBot.INS.getPermissionsManager().getPermissions();
    }

    @GraphQLQuery
    public CascadePermission permission(@GraphQLNonNull String permission) {
        return CascadeBot.INS.getPermissionsManager().getPermission(permission);
    }

    @GraphQLMutation
    public Result userHasPermission(@GraphQLContext QLContext context, long userId, String permission) {
        return context.runIfAuthenticated(QLContext.AuthenticationLevel.GUILD, () -> {
            GuildData guildData = context.getGuildData();
            if (guildData == null) throw new IllegalStateException("GuildData is null!");

            Member member = context.getGuild().getMemberById(userId);

            CascadePermission cascadePermission = CascadeBot.INS.getPermissionsManager().getPermission(permission);
            if (cascadePermission == null) throw new IllegalArgumentException("The permission provided does not exist!");

            return guildData.getGuildPermissions().evalPermission(member, cascadePermission, guildData.getCoreSettings());
        });
    }

}
