package org.cascadebot.cascadebot.data.graphql;

import graphql.ExceptionWhileDataFetching;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import org.cascadebot.cascadebot.CascadeBot;

public class GraphQLExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();
        SourceLocation sourceLocation = handlerParameters.getSourceLocation();
        ExecutionPath path = handlerParameters.getPath();

        ExceptionWhileDataFetching error = new ExceptionWhileDataFetching(path, exception, sourceLocation);
        CascadeBot.LOGGER.warn(error.getMessage());
        CascadeBot.LOGGER.debug(error.getMessage(), exception);

        return DataFetcherExceptionHandlerResult.newResult().error(error).build();
    }

}
