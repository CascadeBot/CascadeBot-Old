package org.cascadebot.cascadebot.scripting;

import delight.graaljssandbox.GraalSandbox;
import delight.graaljssandbox.GraalSandboxes;
import net.dv8tion.jda.api.EmbedBuilder;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.scripting.objects.ScriptContext;
import org.cascadebot.cascadebot.scripting.objects.ScriptEmote;
import org.cascadebot.cascadebot.scripting.objects.ScriptGuild;
import org.cascadebot.cascadebot.scripting.objects.ScriptRole;
import org.cascadebot.cascadebot.scripting.objects.ScriptSnowflake;
import org.cascadebot.cascadebot.scripting.objects.ScriptUser;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptCategory;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptChannel;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptStoreChannel;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptTextChannel;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptVoiceChannel;
import org.cascadebot.shared.utils.ThreadPoolExecutorLogged;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class ScriptRunner {

    private static final GraalSandbox SANDBOX = GraalSandboxes.create();
    private static final ThreadGroup SANDBOX_THREADS = new ThreadGroup("Sandbox Thread Pool");
    private static final ExecutorService SANDBOX_POOL = ThreadPoolExecutorLogged.newCachedThreadPool(r -> new Thread(SANDBOX_THREADS, r,
            SANDBOX_THREADS.getName() + "-" + SANDBOX_THREADS.activeCount()), CascadeBot.LOGGER);
    private static final Timer SANDBOX_TIMER = new Timer();

    static {
        SANDBOX.allowNoBraces(true);
        SANDBOX.setMaxCPUTime(100);

        SANDBOX.allow(Object.class);
        SANDBOX.allow(String.class);
        SANDBOX.allow(Integer.class);
        SANDBOX.allow(Boolean.class);
        SANDBOX.allow(Double.class);
        SANDBOX.allow(Float.class);

        SANDBOX.allow(ScriptUser.class);
        SANDBOX.allow(ScriptGuild.class);
        SANDBOX.allow(ScriptSnowflake.class);
        SANDBOX.allow(ScriptRole.class);
        SANDBOX.allow(ScriptEmote.class);
        SANDBOX.allow(ScriptCategory.class);
        SANDBOX.allow(ScriptChannel.class);
        SANDBOX.allow(ScriptStoreChannel.class);
        SANDBOX.allow(ScriptTextChannel.class);
        SANDBOX.allow(ScriptVoiceChannel.class);

        SANDBOX.setExecutor(SANDBOX_POOL);
    }

    public static void runScript(ScriptContext scriptContext, Map<String, Object> variables, String scriptName, String script) {
        Map<String, Object> scriptVariables = scriptContext.getVariableMap();
        scriptVariables.put("config", variables);

        try {
            Bindings bindings = new SimpleBindings();
            bindings.putAll(scriptVariables);
            SANDBOX.eval(script, bindings);
        } catch (ScriptException scriptException) {
            EmbedBuilder errorBuilder = new EmbedBuilder();
            errorBuilder.setTitle("Error running script");
            errorBuilder.addField("Error", scriptException.getMessage(), false);
            Messaging.sendEmbedMessage(MessageType.DANGER, scriptContext.getChannel(), errorBuilder);
            scriptException.printStackTrace();
        }

        SANDBOX_TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                /*boolean running = !future.isDone();
                future.cancel(true);*/
                boolean canceledAny = scriptContext.cancelAllFutures();
                if (/* running || */ canceledAny) {
                    EmbedBuilder longRunning = new EmbedBuilder();
                    longRunning.setTitle("Script ran too long!");
                    longRunning.setDescription("The script ran too long and therefore was forcefully canceled");
                    Messaging.sendEmbedMessage(MessageType.DANGER, scriptContext.getChannel(), longRunning);
                }
            }
        }, 5000);
    }

}
