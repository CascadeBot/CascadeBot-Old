package org.cascadebot.cascadebot.scripting;

import delight.rhinosandox.RhinoSandbox;
import delight.rhinosandox.RhinoSandboxes;
import delight.rhinosandox.internal.RhinoSandboxImpl;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.scripting.objects.ScriptContext;
import org.cascadebot.cascadebot.scripting.objects.ScriptGuild;
import org.cascadebot.cascadebot.scripting.objects.ScriptUser;
import org.cascadebot.shared.utils.ThreadPoolExecutorLogged;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ScriptRunner {

    private static final RhinoSandbox SANDBOX = RhinoSandboxes.create();
    private static final ThreadGroup SANDBOX_THREADS = new ThreadGroup("Sandbox Thread Pool");
    private static final ExecutorService SANDBOX_POOL = ThreadPoolExecutorLogged.newCachedThreadPool(r -> new Thread(SANDBOX_THREADS, r,
            SANDBOX_THREADS.getName() + "-" + SANDBOX_THREADS.activeCount()), CascadeBot.LOGGER);
    private static final Timer SANDBOX_TIMER = new Timer();

    static {
        ((RhinoSandboxImpl) SANDBOX).assertContextFactory();
        SANDBOX.setUseSealedScope(false);

        SANDBOX.setInstructionLimit(100000);

        SANDBOX.allow(String.class);
        SANDBOX.allow(ScriptUser.class);
        SANDBOX.allow(ScriptGuild.class);

        SANDBOX.setUseSealedScope(true);
    }

    public static void runScript(ScriptContext scriptContext, String scriptName, String script) {
        Map<String, Object> variables = new HashMap<>();

        Future<?> future = SANDBOX_POOL.submit(() -> {
            SANDBOX.eval(scriptName, script, variables);
        });

        SANDBOX_TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                future.cancel(true);
            }
        }, 500);
    }

}
