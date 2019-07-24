package org.cascadebot.cascadebot.data.graphql.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cascadebot.cascadebot.data.language.Locale;
import spark.Request;

@AllArgsConstructor
@Getter
public class QLContext {

    private final Request request;
    private final long userId;
    private final long guildId;

    // TODO: Add methods to check that user is authenticated for this guild

}
