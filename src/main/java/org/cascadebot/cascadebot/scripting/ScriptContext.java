package org.cascadebot.cascadebot.scripting;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class ScriptContext {

    private Guild guild;
    private User user;

    private ScriptContext(Guild guild, User user) {
        this.guild = guild;
        this.user = user;
    }

    private static ThreadLocal<ScriptContext> scriptContextThreadLocal = new ThreadLocal<>();

    public static ScriptContext enterContext(Guild guild, User user) {
        if (scriptContextThreadLocal.get() != null) {
            throw new IllegalStateException("A script context already exists for this thread! Please close the old context before opening a new one.");
        }
        ScriptContext scriptContext = new ScriptContext(guild, user);
        scriptContextThreadLocal.set(scriptContext);
        return scriptContext;
    }

    public static void exitContext() {
        if (scriptContextThreadLocal.get() == null) {
            throw new IllegalStateException("There is no script context present for this thread!");
        }
        scriptContextThreadLocal.remove();
    }

    public static ScriptContext getContext() {
        return scriptContextThreadLocal.get();
    }

    public Guild getGuild() {
        return guild;
    }

    public User getUser() {
        return user;
    }

}
