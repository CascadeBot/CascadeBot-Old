package org.cascadebot.cascadebot.data.graphql.services;

import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionsServices {

    @Getter
    private static PermissionsServices instance = new PermissionsServices();

    @GraphQLQuery
    public Set<CascadePermission> allPermissions() {
        return CascadeBot.INS.getPermissionsManager().getPermissions();
    }

}
