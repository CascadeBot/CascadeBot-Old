package org.cascadebot.cascadebot.scripting.objects.exceptions;

public class ScriptGuildNotFoundException extends Exception {

    long guildId;

    public ScriptGuildNotFoundException(long guildId) {
        super("Guild of id " + guildId + " doesn't exist");
    }

}
