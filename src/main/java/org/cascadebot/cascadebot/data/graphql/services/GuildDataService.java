package org.cascadebot.cascadebot.data.graphql.services;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLRootContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.data.graphql.objects.QLContext;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GuildDataService {

    @Getter
    private static GuildDataService instance = new GuildDataService();

    @GraphQLQuery
    public GuildData guild(@GraphQLRootContext QLContext context, long id) {
        return GuildDataManager.getGuildData(id);
    }

}
