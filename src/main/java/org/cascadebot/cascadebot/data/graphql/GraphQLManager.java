package org.cascadebot.cascadebot.data.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;

public class GraphQLManager {

    private GraphQLSchema schema = new GraphQLSchemaGenerator()
            .withOperationsFromSingleton(GuildDataService.getInstance())
            .withOperationsFromSingleton(PlaylistService.getInstance())
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
