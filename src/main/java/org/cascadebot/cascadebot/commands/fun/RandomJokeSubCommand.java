package org.cascadebot.cascadebot.commands.fun;

import com.google.gson.JsonElement;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.WebUtils;

import java.io.IOException;

public class RandomJokeSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        try {
            context.getTypedMessaging().replyInfo(getJoke());
        } catch (IOException e) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.random.joke.error_loading"));
        };
    }

    private String getJoke() throws IOException {
        JsonElement jsonElement = WebUtils.getJsonFromURL("https://icanhazdadjoke.com/");
        return jsonElement.getAsJsonObject().get("joke").getAsString();
    }

    @Override
    public String parent() {
        return "random";
    }

    @Override
    public String command() {
        return "joke";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }
}
