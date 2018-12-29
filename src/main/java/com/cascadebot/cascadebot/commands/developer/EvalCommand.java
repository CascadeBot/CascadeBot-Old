/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.permissions.SecurityLevel;
import com.cascadebot.cascadebot.utils.ErrorUtils;
import com.cascadebot.cascadebot.utils.objects.ThreadPoolExecutorLogged;
import net.dv8tion.jda.core.entities.Member;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class EvalCommand implements ICommandRestricted {

    private ScriptEngineManager manager = new ScriptEngineManager();

    private static final ThreadGroup EVAL_THREADS = new ThreadGroup("EvalCommand Thread Pool");
    private static final ExecutorService EVAL_POOL = ThreadPoolExecutorLogged.newCachedThreadPool(r -> new Thread(EVAL_THREADS, r,
            EVAL_THREADS.getName() + EVAL_THREADS.activeCount()));

    private static final List<String> IMPORTS = Arrays.asList(
            "com.cascadebot.cascadebot.data",
            "com.cascadebot.cascadebot.messaging",
            "com.cascadebot.cascadebot.utils",
            "net.dv8tion.jda.core",
            "net.dv8tion.jda.core.managers",
            "net.dv8tion.jda.core.entities.impl",
            "net.dv8tion.jda.core.entities",
            "net.dv8tion.jda.core.utils",
            "java.util.stream",
            "java.util",
            "java.lang",
            "java.text",
            "java.math",
            "java.time",
            "java.io",
            "java.nio",
            "java.nio.file");

    private static final List<String> ENGINES = Arrays.asList("groovy", "java", "jshell");

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            //TODO add utils for error messages
            context.sendWarning("Needs more args");
            return;
        }


        ScriptEngine scriptEngine;
        String code;

        if (ENGINES.contains(context.getArg(0).toLowerCase())) {
            scriptEngine = manager.getEngineByName(context.getArg(0).toLowerCase());
            code = context.getMessage(1);
        } else {
            scriptEngine = manager.getEngineByName(ENGINES.get(0));
            code = context.getMessage(0);
        }


        EVAL_POOL.submit(() -> {
            try {

                scriptEngine.put("sender", sender);
                scriptEngine.put("context", context);
                scriptEngine.put("channel", context.getChannel());
                scriptEngine.put("guild", context.getGuild());
                scriptEngine.put("sender", context.getMember());
                String imports = IMPORTS.stream().map(s -> "import " + s + ".*;").collect(Collectors.joining("\n"));

                String codeToRun = imports + "\n" + code;
                String results = String.valueOf(scriptEngine.eval(codeToRun));
                if (results.length() < 2048) {
                    context.reply(results);
                } else {
                    context.reply(ErrorUtils.paste(results));
                }
            } catch (ScriptException e) {
                context.getChannel().sendMessage("Error running script: " + ErrorUtils.paste(ErrorUtils.getStackTrace(e))).queue(); //Maybe we should give the error message as well so you don't have to click on link?
            }
        });
    }

    @Override
    public String defaultCommand() {
        return "eval";
    }

    @Override
    public CommandType getType() {
        return CommandType.DEVELOPER;
    }

    @Override
    public SecurityLevel getCommandLevel() {
        return SecurityLevel.DEVELOPER;
    }

    @Override
    public boolean forceDefault() {
        return true;
    }

    public static void shutdownEvalPool() {
        EVAL_POOL.shutdown();
    }

}
