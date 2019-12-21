package org.cascadebot.cascadebot.commands.fun;

import com.google.gson.JsonElement;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.WebUtils;

import java.io.IOException;

public class JokeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        try {
            context.getTypedMessaging().replyInfo(getJoke());
        } catch (IOException e) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.joke.error_loading"));
        };
    }

    private String getJoke() throws IOException {
        JsonElement jsonElement = WebUtils.getJsonFromURL("https://icanhazdadjoke.com/");
        return jsonElement.getAsJsonObject().get("joke").getAsString();
    }

    @Override
    public String command() {
        return "joke";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("joke", true);
    }

    @Override
    public Module getModule() {
        return Module.FUN;
    }
}
