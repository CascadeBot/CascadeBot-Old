package com.cascadebot.cascadebot.commands.commands.developer;

import com.cascadebot.cascadebot.commands.Command;
import com.cascadebot.cascadebot.commands.CommandContext;
import com.cascadebot.cascadebot.commands.CommandType;
import net.dv8tion.jda.core.entities.Member;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EvalCommand implements Command {

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("jshell");

    @Override
    public void onCommand(Member sender, CommandContext context) {
        //TODO acutely eval
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
    public boolean forceDefault() {
        return true;
    }
}
