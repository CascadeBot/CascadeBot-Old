package org.cascadebot.cascadebot.data.graphql.services;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLRootContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
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
    public Result userHasPermission(@GraphQLRootContext QLContext context, long guildId, long userId, String permission) {
        return context.runIfAuthenticatedGuild(guildId, (guild, member) -> {
            GuildData guildData = context.getGuildData(guildId);
            if (guildData == null) throw new IllegalStateException("GuildData is null!");

            Member memberToCheck = CascadeBot.INS.getShardManager().getGuildById(guildId).getMemberById(userId);

            CascadePermission cascadePermission = CascadeBot.INS.getPermissionsManager().getPermission(permission);
            if (cascadePermission == null) throw new IllegalArgumentException("The permission provided does not exist!");

            return guildData.getGuildPermissions().evalPermission(memberToCheck, cascadePermission, guildData.getCoreSettings());
        });
    }

}
