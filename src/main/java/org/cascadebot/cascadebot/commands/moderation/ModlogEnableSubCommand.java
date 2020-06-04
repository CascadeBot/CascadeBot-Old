package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.objects.LoopMode;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModlogEnableSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }
        TextChannel textChannel = DiscordUtils.getTextChannel(context.getGuild(), context.getArg(0));
        if (textChannel == null) {
            context.reply("Invalid channel");
            // Invalid channel
            return;
        }
        List<String> events = new ArrayList<>(Arrays.asList(context.getArgs()));
        events.remove(0);
        List<ModlogEvent> modlogEvents = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        for (String event : events) {
            if (EnumUtils.isValidEnum(ModlogEvent.class, event.toUpperCase())) {
                modlogEvents.add(ModlogEvent.valueOf(event.toUpperCase()));
            } else if (EnumUtils.isValidEnum(ModlogEvent.Category.class, event.toUpperCase())){
                ModlogEvent.Category category = ModlogEvent.Category.valueOf(event.toUpperCase());
                List<ModlogEvent> additionalEvents = ModlogEvent.getEventsFromCategory(category);
                modlogEvents.addAll(additionalEvents);
            } else {
                failed.add(event);
            }
        }
        if (failed.size() == events.size()) {
            context.getTypedMessaging().replyDanger("Failed to find all specified events");
            return;
        }
        List<ModlogEvent> failedEvents = new ArrayList<>();
        List<ModlogEvent> succeed = new ArrayList<>();
        for (ModlogEvent event : modlogEvents) {
            if (context.getData().getModeration().enableEvent(textChannel, event)) {
                succeed.add(event);
            } else {
                failedEvents.add(event);
            }
        }
        List<Page> pageList = new ArrayList<>();
        if (failedEvents.size() == modlogEvents.size()) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(MessageType.DANGER.getColor());
            embedBuilder.setTitle("Failed to enable all specified events");
            embedBuilder.setDescription("Failed to enable or find all specified events. Refer to the other pages for more info");
            pageList.add(new PageObjects.EmbedPage(embedBuilder));
        } else {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(MessageType.SUCCESS.getColor());
            embedBuilder.setTitle("Successfully enabled events!");
            embedBuilder.setDescription("Some events may have still failed to enable, check other pages for more info");
            pageList.add(new PageObjects.EmbedPage(embedBuilder));
        }
        if (succeed.size() > 0) {
            List<String> pageValues = PageUtils.splitString(succeed.stream().map(event -> Language.i18n(context.getLocale(), "enums.moldogevent." + event.name().toLowerCase() + ".display")).collect(Collectors.joining("\n")), 1000, '\n');
            int subPage = 1;
            for (String pageValue: pageValues) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(MessageType.SUCCESS.getColor());
                embedBuilder.setTitle("Enabled Events");
                embedBuilder.setDescription("These events where successfully enabled in the specified channel");
                embedBuilder.addField("Events", pageValue, false);
                if (pageValues.size() > 1) {
                    embedBuilder.addField("Sub Page", subPage + "/" + pageValues.size(), true);
                    subPage++;
                }
                pageList.add(new PageObjects.EmbedPage(embedBuilder));
            }
        }
        if (failedEvents.size() > 0) {
            List<String> pageValues = PageUtils.splitString(failedEvents.stream().map(event -> Language.i18n(context.getLocale(), "enums.moldogevent." + event.name().toLowerCase() + ".display")).collect(Collectors.joining("\n")), 1000, '\n');
            int subPage = 1;
            for (String pageValue: pageValues) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(MessageType.WARNING.getColor());
                embedBuilder.setTitle("Failed to enable events");
                embedBuilder.setDescription("These events failed to enable. This means they are probably already enabled in that channel");
                embedBuilder.addField("Events", pageValue, false);
                if (pageValues.size() > 1) {
                    embedBuilder.addField("Sub Page", subPage + "/" + pageValues.size(), true);
                    subPage++;
                }
                pageList.add(new PageObjects.EmbedPage(embedBuilder));
            }
        }
        if (failed.size() > 0) {
            List<String> pageValues = PageUtils.splitString(String.join("\n", failed), 1000, '\n');
            int subPage = 1;
            for (String pageValue: pageValues) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(MessageType.DANGER.getColor());
                embedBuilder.setTitle("Failed to find events");
                embedBuilder.setDescription("The events specified where not found, so could not be enabled. Refer to `;modlog events` for list of available events");
                embedBuilder.addField("Events", pageValue, false);
                if (pageValues.size() > 1) {
                    embedBuilder.addField("Sub Page", subPage + "/" + pageValues.size(), true);
                    subPage++;
                }
                pageList.add(new PageObjects.EmbedPage(embedBuilder));
            }
        }
        context.getUiMessaging().sendPagedMessage(pageList);
    }

    public String command() {
        return "enable";
    }

    public CascadePermission permission() {
        return CascadePermission.of("modlog.enable", false, Module.MODERATION);
    }

    public String parent() {
        return "modlog";
    }

}
