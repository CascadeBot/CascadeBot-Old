/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import com.google.gson.JsonArray;
import io.github.binaryoverload.JSONConfig;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.ShutdownHandler;
import org.cascadebot.cascadebot.data.objects.ArgumentType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ArgumentManager {

    @Getter
    private JSONConfig argumentsFile;

    private Map<String, Argument> arguments = new ConcurrentHashMap<>();

    public void initArguments() {
        try {
            argumentsFile = new JSONConfig(Objects.requireNonNull(CascadeBot.class.getClassLoader()
                    .getResourceAsStream("arguments.json")));
            argumentsFile.setAllowedSpecialCharacters(ArrayUtils.add(argumentsFile.getAllowedSpecialCharacters(), '*'));
            for (String key : argumentsFile.getKeys(true)) {
                if (key.startsWith("_")) continue;
                Argument argument = getArgumentFromObject(key);
                if (argument != null) {
                    arguments.put(argument.getId(), argument);
                }
            }
        } catch (Exception e) {
            CascadeBot.LOGGER.error("Cannot load arguments!", e);
            ShutdownHandler.exitWithError();
        }
    }

    /**
     * Gets the argument for a specific command. This argument contains all the arguments for the command.
     * If this method returns null, it means a command doesn't have any arguments or the arguments haven't
     * been specified in the argument.json.
     *
     * @param command The command to get the argument for
     * @return The command argument containing all the sub arguments
     */
    public Argument getCommandArgument(ExecutableCommand command) {
        return getArgument(command.getAbsoluteCommand());
    }

    /**
     * Gets all the arguments under a parent path.
     *
     * @param parent The path to look for arguments under
     * @return
     */
    private List<Argument> getArguments(String parent) {
        Optional<JSONConfig> argumentsConfig = argumentsFile.getSubConfig(parent);
        if (argumentsConfig.isEmpty()) return List.of();
        List<Argument> arguments = new ArrayList<>();
        for (String key : argumentsConfig.get().getKeys(false)) {
            /*
                Checks if the last path node contains a "_". If it does, it means this path refers to a meta key
                and not an actual argument
                Example:
                    tag.raw - "raw" doesn't start with "_" so the loop for this key will not be skipped
                    tag.raw._type - "_type" starts with a "_" so the loop for this key will be skipped
            */
            if (key.charAt(key.lastIndexOf(".") + 1) == '_') continue;

            String id = (parent.isBlank() ? "" : parent + ".") + key;
            Argument argument = getArgumentFromObject(id);
            if (argument != null) {
                arguments.add(argument);
            }
        }
        return arguments;
    }

    /**
     * Gets an argument by id. An id is '.' separated.
     *
     * @param id The id of the argument to get
     * @return The argument if one exists, else null.
     */
    public Argument getArgument(String id) {
        return arguments.get(id);
    }

    /**
     * Returns the parent for an argument; that is, the argument directly above in the tree.
     *
     * @param argumentId The id of the argument of which to get the parent
     * @return The parent of the argument or null if argument has no parent (It's a root element)
     */
    public Argument getParent(String argumentId) {
        if (!argumentId.contains(".")) return null;
        return arguments.get(argumentId.substring(0, argumentId.lastIndexOf('.')));
    }

    /**
     * Creates a argument object from JSON.
     * The JSON should look something like this:
     * <pre>
     * "id": {
     *     "_type": "command" or "required" or "optional"
     *     "_aliases": [] <-- Optional
     * }
     * </pre>
     *
     * If the ID is suffixed by an asterisk, the display alone flag will be set to false.
     *
     * @param id The path at which to get the argument from.
     * @return THe constructed argument if valid object else null.
     */
    private Argument getArgumentFromObject(String id) {
        // Don't bother if it's not an actual object
        if (argumentsFile.getElement(id).isEmpty() || !argumentsFile.getElement(id).get().isJsonObject()) return null;
        Optional<JSONConfig> subConfig = argumentsFile.getSubConfig(id);
        if (subConfig.isEmpty()) return null;

        String typeRaw = subConfig.get().getString("_type").orElse("command");
        // If no valid argument type is given, this defaults to the "command" type
        ArgumentType type = EnumUtils.isValidEnumIgnoreCase(ArgumentType.class, typeRaw) ? EnumUtils.getEnumIgnoreCase(ArgumentType.class, typeRaw) : ArgumentType.COMMAND;
        boolean varArgs = subConfig.get().getBoolean("_vararg").orElse(false);

        if (varArgs && type == ArgumentType.COMMAND) {
            throw new IllegalArgumentException("Var args cannot be used with a command typ!");
        }

        boolean displayAlone = true;
        if (id.endsWith("*")) {
            /*
              This means that the argument itself won't be displayed by itself.
              This is useful for nested permissions. This will always be treated as true
              if the argument has no sub-arguments.

              Example:
              displayAlone = true for the "queue" argument
              ;queue
              ;queue save

              displayAlone = false
              ;queue save
             */
            displayAlone = false;
        }
        // Make sure there are no asterisks left in the path which would throw off getting by id
        String newId = id.replace("*", "");

        JsonArray aliasesRaw = subConfig.get().getArray("_aliases").orElse(new JsonArray());
        Set<String> aliases = new HashSet<>();
        aliasesRaw.iterator().forEachRemaining(element -> aliases.add(element.getAsString()));

        List<Argument> subArgs = getArguments(id);

        return new Argument(newId, type, displayAlone, varArgs, subArgs, aliases);
    }

}
