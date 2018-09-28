package com.cascadebot.cascadebot.commands.commands.developer;

import com.cascadebot.cascadebot.commands.Command;
import com.cascadebot.cascadebot.commands.CommandContext;
import com.cascadebot.cascadebot.commands.CommandType;
import net.dv8tion.jda.core.entities.Member;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EvalCommand implements Command {
    private ScriptEngineManager manager = new ScriptEngineManager();

    private List<String> engineNames = new ArrayList<>();

    private List<String> imports = Arrays.asList("com.cascadebot.cascadebot.utils.*", "");

    public EvalCommand() {
        manager.getEngineFactories().forEach(scriptEngineFactory -> {
            engineNames.addAll(scriptEngineFactory.getNames());
        });
    }

    @Override
    public void onCommand(Member sender, CommandContext context) {
        //TODO acutely eval
        if(context.getArgs().length < 2) {
            //TODO add utils for error messages
            context.getChannel().sendMessage("Needs more args").queue();
            return;
        }
        String engine = context.getArgs()[0];
        if(!engineNames.contains(engine)) {
            //TODO add utils for error messages
            context.getChannel().sendMessage("Invalid script engine giving").queue();
            return;
        }
        String code = context.getMessageFromArgs(1);
        ScriptEngine scriptEngine = manager.getEngineByExtension(engine);
        scriptEngine.put("sender", sender);
        scriptEngine.put("context", context);
        String imports = this.imports.stream().map(s -> "import " + s + ";").collect(Collectors.joining("\n"));
        String codeToRun = imports + "\n" + code;
        try {
            String results = String.valueOf(scriptEngine.eval(codeToRun));
            if(results.length() < 2048) {
                context.getChannel().sendMessage(results).queue();
            } else {
                context.getChannel().sendMessage("Results too big").queue();
            }
        } catch (ScriptException e) {
            context.getChannel().sendMessage("Error running script").queue(); //TODO implement this better
        }
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
