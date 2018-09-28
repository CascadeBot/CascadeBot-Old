package com.cascadebot.cascadebot.commands.commands.developer;

import com.cascadebot.cascadebot.commands.Command;
import com.cascadebot.cascadebot.commands.CommandContext;
import com.cascadebot.cascadebot.commands.CommandType;
import net.dv8tion.jda.core.entities.Member;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EvalCommand implements Command {

    private ScriptEngineManager manager = new ScriptEngineManager();

    private static final ThreadGroup EVAL_POOL = new ThreadGroup("EvalCommand Thread Pool");
    private static final ExecutorService POOL = Executors.newCachedThreadPool(r -> new Thread(EVAL_POOL, r,
            EVAL_POOL.getName() + EVAL_POOL.activeCount()));

    private static final List<String> IMPORTS = Arrays.asList("com.cascadebot.cascadebot.utils");

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            //TODO add utils for error messages
            context.getChannel().sendMessage("Needs more args").queue();
            return;
        }
        String engine = context.getArgs()[0];

        POOL.submit(() -> {
            try {
                ScriptEngine scriptEngine = manager.getEngineByName(engine);
                if (scriptEngine == null) {
                    context.getChannel().sendMessage("\u2139 Using script engine `jshell`").queue();
                    scriptEngine = manager.getEngineByName("jshell");
                }

                String code = context.getMessageFromArgs(1);
                scriptEngine.put("sender", sender);
                scriptEngine.put("context", context);
                String imports = IMPORTS.stream().map(s -> "import " + s + ".*;").collect(Collectors.joining("\n"));
                String codeToRun = imports + "\n" + code;

                String results = String.valueOf(scriptEngine.eval(codeToRun));
                if (results.length() < 2048) {
                    context.getChannel().sendMessage(results).queue();
                } else {
                    context.getChannel().sendMessage("Results too big").queue();
                }
            } catch (ScriptException e) {
                context.getChannel().sendMessage("Error running script: " + e.getMessage()).queue(); //TODO implement this better
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
    public CommandLevel getCommandLevel() {
        return CommandLevel.DEVELOPER;
    }

    @Override
    public boolean forceDefault() {
        return true;
    }

}
