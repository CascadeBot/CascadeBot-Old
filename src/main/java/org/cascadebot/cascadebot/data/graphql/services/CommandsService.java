package org.cascadebot.cascadebot.data.graphql.services;

import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandsService {

    @Getter
    private static CommandsService instance = new CommandsService();

    @GraphQLQuery
    public List<ICommandMain> allCommands(Module module) {
        if (module == null) {
            return CascadeBot.INS.getCommandManager().getCommands();
        } else {
            return CascadeBot.INS.getCommandManager().getCommandsByModule(module);
        }
    }

}
