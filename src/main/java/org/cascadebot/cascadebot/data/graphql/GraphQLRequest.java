package org.cascadebot.cascadebot.data.graphql;

import java.util.HashMap;
import java.util.Map;

public class GraphQLRequest {

    private String query;
    private Map<String, Object> variables = new HashMap<>();
    private String operationName;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

}
