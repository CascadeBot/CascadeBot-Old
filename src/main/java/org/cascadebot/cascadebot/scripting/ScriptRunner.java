package org.cascadebot.cascadebot.scripting;


import delight.rhinosandox.RhinoSandbox;
import delight.rhinosandox.RhinoSandboxes;
import delight.rhinosandox.internal.RhinoSandboxImpl;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.scripting.objects.ScriptGuild;
import org.cascadebot.cascadebot.scripting.objects.ScriptUser;

import java.util.HashMap;
import java.util.Map;

public class ScriptRunner {

    private static final RhinoSandbox SANDBOX = RhinoSandboxes.create();

    static {
        ((RhinoSandboxImpl) SANDBOX).assertContextFactory();
        SANDBOX.setUseSealedScope(false);

        SANDBOX.inject(ScriptUser.class);
        SANDBOX.allow(String.class);

        SANDBOX.setUseSealedScope(true);
    }

    public static Object runScript(Guild guild, User user, String scriptName, String script) {
        try {
            ScriptContext.enterContext(guild, user);

            Map<String, Object> variables = new HashMap<>();

            variables.put("guild", new ScriptGuild(guild));
            ScriptUser user1 = new ScriptUser();
            user1.internalUser = user;
            variables.put("user", user);

            return SANDBOX.eval(scriptName, script, variables);
        } finally {
            ScriptContext.exitContext();
        }

    }

}
