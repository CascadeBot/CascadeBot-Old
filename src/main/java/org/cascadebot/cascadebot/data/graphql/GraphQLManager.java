package org.cascadebot.cascadebot.data.graphql;

import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.cascadebot.cascadebot.data.graphql.services.CommandsService;
import org.cascadebot.cascadebot.data.graphql.services.GuildDataService;
import org.cascadebot.cascadebot.data.graphql.services.LanguageService;
import org.cascadebot.cascadebot.data.graphql.services.PermissionsServices;
import org.cascadebot.cascadebot.data.graphql.services.PlaylistService;
import org.cascadebot.cascadebot.data.graphql.services.SettingsService;

public class GraphQLManager {

    private GraphQLSchema schema = new GraphQLSchemaGenerator()
            .withOperationsFromSingleton(GuildDataService.getInstance())
            .withOperationsFromSingleton(PlaylistService.getInstance())
            .withOperationsFromSingleton(CommandsService.getInstance())
            .withOperationsFromSingleton(LanguageService.getInstance())
            .withOperationsFromSingleton(PermissionsServices.getInstance())
            .withOperationsFromSingleton(SettingsService.getInstance())
            .generate();

    private GraphQLExceptionHandler exceptionHandler = new GraphQLExceptionHandler();
    private GraphQL graphQL = GraphQL.newGraphQL(schema)
            .queryExecutionStrategy(new AsyncExecutionStrategy(exceptionHandler))
            .mutationExecutionStrategy(new AsyncSerialExecutionStrategy(exceptionHandler))
            .subscriptionExecutionStrategy(new SubscriptionExecutionStrategy(exceptionHandler))
            .build();

    public GraphQLSchema getSchema() {
        return schema;
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }

}
