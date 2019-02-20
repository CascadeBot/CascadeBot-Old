/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.messaging.Messaging;
import com.cascadebot.cascadebot.permissions.PermissionNode;
import com.cascadebot.cascadebot.utils.PasteUtils;
import com.cascadebot.shared.SecurityLevel;
import com.cascadebot.shared.utils.ThreadPoolExecutorLogged;
import net.dv8tion.jda.core.entities.Member;
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
            EVAL_THREADS.getName() + EVAL_THREADS.activeCount()), CascadeBot.logger);

    private static final List<String> IMPORTS = List.of(
            "com.cascadebot.cascadebot",
            "com.cascadebot.cascadebot.data",
            "com.cascadebot.cascadebot.messaging",
            "com.cascadebot.cascadebot.utils",
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
    public void onCommand (Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            //TODO add utils for error messages
            Messaging.sendWarningMessage(context.getChannel(), "Not enough args", false);
            return;
        }

        ScriptEngine scriptEngine;
        String code;

        scriptEngine = new GroovyScriptEngineImpl();
        code = context.getMessage(0);

        for (String blacklistedItem : BLACKLIST) {
            if (new PermissionNode(blacklistedItem).test(code)) {
                context.replyDanger("You cannot run this code as it contains blacklisted items!");
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
                context.replyDanger("Error running script: %s \n**%s** \n```swift\n%s```",
                        PasteUtils.paste(PasteUtils.getStackTrace(e)),
                        e.getClass().getName(),
                        e.getMessage()
                );
            }
        });
    }

    @Override
    public String command() {
        return "eval";
    }

    @Override
    public String description() {
        return "evaluate code";
    }

    @Override
    public Module getModule() {
        return Module.DEVELOPER;
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
