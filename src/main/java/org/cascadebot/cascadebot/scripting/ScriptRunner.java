package org.cascadebot.cascadebot.scripting;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.circular.CircularReferenceMatchingMode;
import de.danielbechler.diff.node.DiffNode;
import delight.graaljssandbox.GraalSandbox;
import delight.graaljssandbox.GraalSandboxes;
import delight.graaljssandbox.internal.GraalSandboxImpl;
import delight.nashornsandbox.internal.SandboxClassFilter;
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
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Value;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.StringWriter;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

public class ScriptRunner {

    private static final ThreadGroup SANDBOX_THREADS = new ThreadGroup("Sandbox Thread Pool");
    private static final ExecutorService SANDBOX_POOL = ThreadPoolExecutorLogged.newCachedThreadPool(r -> new Thread(SANDBOX_THREADS, r,
            SANDBOX_THREADS.getName() + "-" + SANDBOX_THREADS.activeCount()), CascadeBot.LOGGER);
    private static final Timer SANDBOX_TIMER = new Timer();

    private static GraalSandbox createSandbox() {
        GraalSandbox sandbox = GraalSandboxes.create();

        sandbox.allowNoBraces(true);
        sandbox.setMaxCPUTime(1000);

        sandbox.allow(Object.class);
        sandbox.allow(String.class);
        sandbox.allow(Integer.class);
        sandbox.allow(Boolean.class);
        sandbox.allow(Double.class);
        sandbox.allow(Float.class);

        sandbox.allow(ScriptUser.class);
        sandbox.allow(ScriptGuild.class);
        sandbox.allow(ScriptSnowflake.class);
        sandbox.allow(ScriptRole.class);
        sandbox.allow(ScriptEmote.class);
        sandbox.allow(ScriptCategory.class);
        sandbox.allow(ScriptChannel.class);
        sandbox.allow(ScriptStoreChannel.class);
        sandbox.allow(ScriptTextChannel.class);
        sandbox.allow(ScriptVoiceChannel.class);
        sandbox.allow(Value.class);

        sandbox.setExecutor(SANDBOX_POOL);

        sandbox.allowPrintFunctions(true);

        return sandbox;
    }

    public static void runScript(ScriptContext scriptContext, Map<String, Object> variables, String scriptName, String script) {
        Map<String, Object> scriptVariables = scriptContext.getVariableMap();
        scriptVariables.put("config", variables);

        StringWriter stringWriter = new StringWriter();
        GraalSandbox sandbox = createSandbox();
        sandbox.setWriter(stringWriter);

        try {
            Bindings bindings = new SimpleBindings();
            bindings.putAll(scriptVariables);

            sandbox.eval(script, bindings);

            stringWriter.flush();
            String content = stringWriter.toString();
            if (content.length() > 0) {
                scriptContext.getChannel().sendMessage(content).queue();
            }
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
                if (/*running || */ canceledAny) {
                    EmbedBuilder longRunning = new EmbedBuilder();
                    longRunning.setTitle("Script ran too long!");
                    longRunning.setDescription("The script ran too long and therefore was forcefully canceled");
                    Messaging.sendEmbedMessage(MessageType.DANGER, scriptContext.getChannel(), longRunning);
                }
            }
        }, 5000);
    }

}
