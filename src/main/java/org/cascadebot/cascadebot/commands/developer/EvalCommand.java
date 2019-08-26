/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.PermissionNode;
import org.cascadebot.cascadebot.utils.PasteUtils;
import org.cascadebot.shared.SecurityLevel;
import org.cascadebot.shared.utils.ThreadPoolExecutorLogged;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class EvalCommand implements ICommandRestricted {

    private ScriptEngineManager manager = new ScriptEngineManager();

    private static final ThreadGroup EVAL_THREADS = new ThreadGroup("EvalCommand Thread Pool");
    private static final ExecutorService EVAL_POOL = ThreadPoolExecutorLogged.newCachedThreadPool(r -> new Thread(EVAL_THREADS, r,
            EVAL_THREADS.getName() + EVAL_THREADS.activeCount()), CascadeBot.LOGGER);

    private static final List<String> IMPORTS = List.of(
            "org.cascadebot.cascadebot",
            "org.cascadebot.cascadebot.commandmeta",
            "org.cascadebot.cascadebot.data",
            "org.cascadebot.cascadebot.data.objects",
            "org.cascadebot.cascadebot.permissions",
            "org.cascadebot.cascadebot.permissions.objects",
            "org.cascadebot.cascadebot.messaging",
            "org.cascadebot.cascadebot.utils",
            "net.dv8tion.jda.core",
            "net.dv8tion.jda.core.managers",
            "net.dv8tion.jda.core.entities",
            "net.dv8tion.jda.core.entities.impl",
            "net.dv8tion.jda.core.utils",
            "java.util",
            "java.util.stream",
            "java.lang",
            "java.text",
            "java.math",
            "java.time",
            "java.io",
            "java.nio",
            "java.nio.file");

    private static final List<String> BLACKLIST = List.of(
            "ShutdownHandler.*",
            "System.exit"
    );

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getTypedMessaging().replyWarning("Not enough arguments!");
            return;
        }

        ScriptEngine scriptEngine;
        String code;

        scriptEngine = new GroovyScriptEngineImpl();
        code = context.getMessage(0);

        for (String blacklistedItem : BLACKLIST) {
            if (new PermissionNode(blacklistedItem).test(code)) {
                context.getTypedMessaging().replyDanger("You cannot run this code as it contains blacklisted items!");
                return;
            }
        }

        EVAL_POOL.submit(() -> {
            try {
                scriptEngine.put("sender", sender);
                scriptEngine.put("context", context);
                scriptEngine.put("channel", context.getChannel());
                scriptEngine.put("guild", context.getGuild());

                String imports = IMPORTS.stream().map(s -> "import " + s + ".*;").collect(Collectors.joining(" "));

                String codeToRun = imports + " " + code;
                String results = String.valueOf(scriptEngine.eval(codeToRun));
                if (results.isBlank()) results = "Empty result!";
                PasteUtils.pasteIfLong(results, 2048, context::reply);
            } catch (ScriptException e) {
                context.getTypedMessaging().replyDanger("Error running script: %s \n**%s** \n```swift\n%s```" ,PasteUtils.paste(PasteUtils.getStackTrace(e)), e.getClass().getName(), e.getMessage());
            }
        });
    }

    @Override
    public String command() {
        return "eval";
    }

    @Override
    public Module getModule() {
        return Module.DEVELOPER;
    }

    @Override
    public String description() {
        return "Evaluates code for the developers.";
    }

    @Override
    public SecurityLevel getCommandLevel() {
        return SecurityLevel.DEVELOPER;
    }

    @Override
    public boolean deleteMessages() {
        return false;
    }

    public static void shutdownEvalPool() {
        EVAL_POOL.shutdown();
    }

}
