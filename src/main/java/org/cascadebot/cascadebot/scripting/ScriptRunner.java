package org.cascadebot.cascadebot.scripting;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
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

    private static final GraalSandbox SANDBOX = GraalSandboxes.create();
    private static SandboxClassFilter sandboxClassFilter = new SandboxClassFilter();
    private static final ThreadGroup SANDBOX_THREADS = new ThreadGroup("Sandbox Thread Pool");
    private static final ExecutorService SANDBOX_POOL = ThreadPoolExecutorLogged.newCachedThreadPool(r -> new Thread(SANDBOX_THREADS, r,
            SANDBOX_THREADS.getName() + "-" + SANDBOX_THREADS.activeCount()), CascadeBot.LOGGER);
    private static final Timer SANDBOX_TIMER = new Timer();

    static {
        SANDBOX.allowNoBraces(true);
        SANDBOX.setMaxCPUTime(100);

        sandboxClassFilter.add(Object.class);
        sandboxClassFilter.add(String.class);
        sandboxClassFilter.add(Integer.class);
        sandboxClassFilter.add(Boolean.class);
        sandboxClassFilter.add(Double.class);
        sandboxClassFilter.add(Float.class);

        sandboxClassFilter.add(ScriptUser.class);
        sandboxClassFilter.add(ScriptGuild.class);
        sandboxClassFilter.add(ScriptSnowflake.class);
        sandboxClassFilter.add(ScriptRole.class);
        sandboxClassFilter.add(ScriptEmote.class);
        sandboxClassFilter.add(ScriptCategory.class);
        sandboxClassFilter.add(ScriptChannel.class);
        sandboxClassFilter.add(ScriptStoreChannel.class);
        sandboxClassFilter.add(ScriptTextChannel.class);
        sandboxClassFilter.add(ScriptVoiceChannel.class);
        sandboxClassFilter.add(Value.class);

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
        SANDBOX.allow(Value.class);
        SANDBOX.allowNoBraces(true);
        SANDBOX.allowExitFunctions(true);
        SANDBOX.allowLoadFunctions(true);
        SANDBOX.allowReadFunctions(true);

        SANDBOX.setExecutor(SANDBOX_POOL);

        SANDBOX.allowPrintFunctions(true);
        SANDBOX.allowGlobalsObjects(true);
    }

    public static void runScript(ScriptContext scriptContext, Map<String, Object> variables, String scriptName, String script) {
        Map<String, Object> scriptVariables = scriptContext.getVariableMap();
        scriptVariables.put("config", variables);

        StringWriter stringWriter = new StringWriter();

        SANDBOX.setWriter(stringWriter);
        //scriptContext.setPolyContext(((GraalSandboxImpl)SANDBOX).getScriptEngine().getPolyglotContext());

        try {
            Bindings bindings = new SimpleBindings();
            bindings.putAll(scriptVariables);
            /*Context context = Context.newBuilder("js").allowAllAccess(true).build();
            scriptContext.setPolyContext(context);
            for (Map.Entry<String, Object> entry : scriptVariables.entrySet()) {
                context.getBindings("js").putMember(entry.getKey(), entry.getValue());
            }
            context.eval("js", script);*/
            GraalJSScriptEngine scriptEngine = GraalJSScriptEngine.create(null, Context.newBuilder("js")//.option("inspect", "3001").option("inspect.Path", "test")
                    .allowExperimentalOptions(true)
                    .allowPolyglotAccess(PolyglotAccess.ALL)
                    .allowHostAccess(HostAccess.ALL)
                    .allowAllAccess(true));
            scriptEngine.getBindings(100).put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> sandboxClassFilter.getStringCache().contains(s));

            //scriptContext.setPolyContext(scriptEngine.getPolyglotContext());

            scriptEngine.eval(script, bindings);

            SANDBOX.eval(script, bindings);

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

        /*SANDBOX_TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                *//*boolean running = !future.isDone();
                future.cancel(true);*//*
                boolean canceledAny = scriptContext.cancelAllFutures();
                if (*//* running || *//* canceledAny) {
                    EmbedBuilder longRunning = new EmbedBuilder();
                    longRunning.setTitle("Script ran too long!");
                    longRunning.setDescription("The script ran too long and therefore was forcefully canceled");
                    Messaging.sendEmbedMessage(MessageType.DANGER, scriptContext.getChannel(), longRunning);
                }
            }
        }, 5000);*/
    }

}
