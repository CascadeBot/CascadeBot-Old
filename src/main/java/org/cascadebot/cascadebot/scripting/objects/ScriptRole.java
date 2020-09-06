package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Role;

public class ScriptRole extends ScriptSnowflake {

    protected Role internalRole;

    public void setInternalRole(Role role) {
        internalRole = role;
        internalSnowflake = role;
    }

}
