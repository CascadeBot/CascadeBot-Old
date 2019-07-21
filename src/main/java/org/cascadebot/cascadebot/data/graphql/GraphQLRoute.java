package org.cascadebot.cascadebot.data.graphql;

import com.google.gson.JsonObject;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import org.cascadebot.cascadebot.CascadeBot;
import spark.Request;
import spark.Response;
import spark.Route;

public class GraphQLRoute implements Route {

    private final GraphQLManager manager;

    public GraphQLRoute(GraphQLManager manager) {
        this.manager = manager;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        GraphQLRequest graphQLRequest = CascadeBot.getGSON().fromJson(request.body(), GraphQLRequest.class);

        response.type("application/json");

        ExecutionInput input = ExecutionInput.newExecutionInput()
                .query(graphQLRequest.getQuery())
                .context(request)
                .operationName(graphQLRequest.getOperationName())
                .variables(graphQLRequest.getVariables())
                .build();

        ExecutionResult executionResult = manager.getGraphQL().execute(input);
        JsonObject graphQlResponse = new JsonObject();
        if (!executionResult.getErrors().isEmpty() || executionResult.getData() == null) {
            graphQlResponse.add("errors", CascadeBot.getGSON().toJsonTree(executionResult.getErrors()));
        }
        graphQlResponse.add("data", CascadeBot.getGSON().toJsonTree(executionResult.getData()));

        return CascadeBot.getGSON().toJson(graphQlResponse);
    }

}
