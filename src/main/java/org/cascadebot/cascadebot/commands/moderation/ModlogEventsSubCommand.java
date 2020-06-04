package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModlogEventsSubCommand extends SubCommand {

    public void onCommand(Member sender, CommandContext context) {
        Map<ModlogEvent.Category, List<ModlogEvent>> modlogCategoryMap = ModlogEvent.getModlogCategoryMap();
        List<Page> embedPages = new ArrayList<>();
        for (Map.Entry<ModlogEvent.Category, List<ModlogEvent>> categoryListEntry : modlogCategoryMap.entrySet()) {
            ModlogEvent.Category category = categoryListEntry.getKey();
            if (category.equals(ModlogEvent.Category.ALL)) {
                continue;
            }
            List<String> pageValues = PageUtils.splitString(categoryListEntry.getValue().stream().map(event -> event.name().toLowerCase()).collect(Collectors.joining("\n")), 1000, '\n');
            int subPage = 1;
            for (String pageValue: pageValues) {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(MessageType.INFO.getColor())
                        .setTitle("Modlog Events");
                if (subPage == 1) {
                        embedBuilder.addField(category.name().toLowerCase(), pageValue, false);
                } else {
                    embedBuilder.addField(category.name().toLowerCase() + " cont.", pageValue, false);
                }
                if (pageValues.size() > 1) {
                    embedBuilder.addField("Sub Page", subPage + "/" + pageValues.size(), true);
                    subPage++;
                }
                embedPages.add(new PageObjects.EmbedPage(embedBuilder));
            }
        }
        context.getUiMessaging().sendPagedMessage(embedPages);
    }

    public String command() {
        return "events";
    }

    public CascadePermission permission() {
        return CascadePermission.of("modlog.events", false, Module.MODERATION);
    }

    public String parent() {
        return "modlog";
    }

}
