/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.cascadebot.cascadebot.CascadeBot;

public interface ISubCommand extends ICommandExecutable {

    @GraphQLQuery(name = "parent")
    String parent();

    @GraphQLIgnore
    default ICommandMain getParent() {
        return CascadeBot.INS.getCommandManager().getCommand(parent());
    }

}
