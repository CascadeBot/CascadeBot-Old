package org.cascadebot.cascadebot.data.graphql;

import com.google.gson.JsonObject;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.graphql.objects.GraphQLRequest;
import org.cascadebot.cascadebot.data.graphql.objects.QLContext;
import org.cascadebot.cascadebot.data.language.Locale;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Arrays;

public class GraphQLRoute implements Route {

    private final GraphQLManager manager;

    public GraphQLRoute(GraphQLManager manager) {
        this.manager = manager;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        GraphQLRequest graphQLRequest = CascadeBot.getGSON().fromJson(request.body(), GraphQLRequest.class);

        response.type("application/json");

        Locale locale = Arrays.stream(Locale.values())
                .filter(locale1 -> locale1.getLanguageCode().equalsIgnoreCase(request.headers("Cascade-Locale")))
                .findFirst()
                .orElse(Locale.getDefaultLocale());

        ExecutionInput input = ExecutionInput.newExecutionInput()
                .query(graphQLRequest.getQuery())
                .context(new QLContext(request, locale))
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
