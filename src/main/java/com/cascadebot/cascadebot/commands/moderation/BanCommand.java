package com.cascadebot.cascadebot.commands.moderation;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.Argument;
import com.cascadebot.cascadebot.commandmeta.ArgumentType;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.messaging.MessageType;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import com.cascadebot.cascadebot.moderation.ModAction;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import org.apache.commons.lang.ObjectUtils;

import java.util.Set;

public class BanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {

        if (context.getArgs().length == 0) {
            context.replyDanger("Not enough arguments!");
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getArg(0), context.getGuild());

        if (targetMember == null) {
            EmbedBuilder builder = MessagingObjects.getStandardMessageEmbed("We couldn't find that user in this guild!\n" +
                    "To forcibly ban a user not in this guild, use `;forceban`!", sender.getUser());
            builder.setTitle("Error");
            context.replyDanger(builder);
            return;
        }

        String reason = null;

        if (context.getArgs().length >= 2) {
            reason = context.getMessage(1);
        }

        CascadeBot.INS.getModerationManager().ban(
                context,
                ModAction.BAN,
                targetMember.getUser(),
                sender,
                reason,
                7 // TODO: add this as an arg
        );
    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

    @Override
    public String command() {
        return "ban";
    }

    @Override
    public String description() {
        return "Bans people I guess ;)";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("reason", "The reason to ban the user for", ArgumentType.OPTIONAL));
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Ban command", "ban", false, Permission.BAN_MEMBERS);
    }

}
