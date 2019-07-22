package org.cascadebot.cascadebot.data.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.cascadebot.cascadebot.data.graphql.services.CommandsService;
import org.cascadebot.cascadebot.data.graphql.services.GuildDataService;
import org.cascadebot.cascadebot.data.graphql.services.LanguageService;
import org.cascadebot.cascadebot.data.graphql.services.PermissionsServices;
import org.cascadebot.cascadebot.data.graphql.services.PlaylistService;

public class GraphQLManager {

    private GraphQLSchema schema = new GraphQLSchemaGenerator()
            .withOperationsFromSingleton(GuildDataService.getInstance())
            .withOperationsFromSingleton(PlaylistService.getInstance())
            .withOperationsFromSingleton(CommandsService.getInstance())
            .withOperationsFromSingleton(LanguageService.getInstance())
            .withOperationsFromSingleton(PermissionsServices.getInstance())
            .withBasePackages()
            .generate();

    private GraphQL graphQL = GraphQL.newGraphQL(schema).build();

    public GraphQLSchema getSchema() {
        return schema;
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }

}
