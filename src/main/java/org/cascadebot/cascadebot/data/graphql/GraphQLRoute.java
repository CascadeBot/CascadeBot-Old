package org.cascadebot.cascadebot.data.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import io.jsonwebtoken.Claims;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
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

        long userId = -1;
        User user = null;

        boolean authenticated;

        boolean authHeaderPresent = !StringUtils.isEmpty(request.headers("Authorization"));
        Claims claims = Config.INS.getAuth().verify(request.headers("Authorization"));

        boolean jwtMatches = claims != null;

        if(jwtMatches) {
            userId = Long.parseLong(claims.getSubject());
            user = CascadeBot.INS.getShardManager().getUserById(userId);
        }

        authenticated = (userId != -1 && authHeaderPresent && jwtMatches);

        ExecutionInput input = ExecutionInput.newExecutionInput()
                .query(graphQLRequest.getQuery())
                .context(new QLContext(request, user, authenticated))
                .operationName(graphQLRequest.getOperationName())
                .variables(graphQLRequest.getVariables())
                .build();

        ExecutionResult executionResult = manager.getGraphQL().execute(input);

        response.type("application/json");
        return CascadeBot.getGSON().toJson(executionResult.toSpecification());
    }

}
