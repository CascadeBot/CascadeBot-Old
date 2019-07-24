package org.cascadebot.cascadebot.data.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.graphql.objects.GraphQLRequest;
import org.cascadebot.cascadebot.data.graphql.objects.QLContext;
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

//        Locale locale = Arrays.stream(Locale.values())
//                .filter(locale1 -> locale1.getLanguageCode().equalsIgnoreCase(request.headers("Cascade-Locale")))
//                .findFirst()
//                .orElse(Locale.getDefaultLocale());

        long userId;
        long guildId = -1;
        try {
            userId = Long.parseLong(request.headers("Cascade-UserID"));
            if (!StringUtils.isEmpty(request.headers("Cascade-GuildID"))) {
                guildId = Long.parseLong(request.headers("Cascade-GuildID"));
            }
        } catch (NumberFormatException e) {
            response.status(400);
            return "The ID's provided are invalid! " + e;
        }

        if (StringUtils.isEmpty(request.headers("Authorization"))) {
            response.status(401);
            return "You are not authorized!";
        }

        if (!Config.INS.getAuth().verifyEncrypt(Long.toString(userId), request.headers("Authorization"))) {
            response.status(401);
            return "You are not authorized!";
        }

        ExecutionInput input = ExecutionInput.newExecutionInput()
                .query(graphQLRequest.getQuery())
                .context(new QLContext(request, userId, guildId))
                .operationName(graphQLRequest.getOperationName())
                .variables(graphQLRequest.getVariables())
                .build();

        ExecutionResult executionResult = manager.getGraphQL().execute(input);

        response.type("application/json");
        return CascadeBot.getGSON().toJson(executionResult.toSpecification());
    }

}
