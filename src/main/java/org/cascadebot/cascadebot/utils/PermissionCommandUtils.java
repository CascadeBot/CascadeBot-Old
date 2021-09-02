/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow;
import org.cascadebot.cascadebot.utils.interactions.CascadeButton;
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer;
import org.cascadebot.cascadebot.utils.interactions.InteractionMessage;

import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public class PermissionCommandUtils {

    public static void tryGetGroupFromString(CommandContext context, GuildData data, String s, Consumer<Group> groupConsumer, long sender) {
        Group groupById = data.getManagement().getPermissions().getGroupById(s);
        if (groupById != null) {
            groupConsumer.accept(groupById);
            return;
        }

        List<Group> groupList = data.getManagement().getPermissions().getGroupsByName(s);
        if (groupList.size() == 1) {
            groupConsumer.accept(groupList.get(0));
            return;
        }

        if (groupList.isEmpty()) {
            // TODO Language!
            context.getTypedMessaging().replyDanger("No group found with name or id " + s);
            return;
        }

        if (groupList.size() > 5) {
            // TODO Language!
            context.getTypedMessaging().replyDanger("Too many groups with the same name! Use these groups ids instead.");
            return;
        }

        EmbedBuilder groupsEmbed = new EmbedBuilder();

        ComponentContainer container = new ComponentContainer();
        CascadeActionRow firstPageRow = new CascadeActionRow();

        // integer for creating buttons, and numbering the possible groups to select
        int i = 1;

        StringBuilder groupsBuilder = new StringBuilder();
        // TODO Language!
        groupsBuilder.append("Multiple Groups With the same name found. Select a group to view more info on it, and then use said group\n\n");

        for (Group group : groupList) {
            char unicode = (char) (0x0030 + i);

            groupsBuilder.append(i).append(": ").append(group.getName()).append(" (id: `").append(group.getId()).append("`)\n");

            EmbedBuilder groupEmbed = new EmbedBuilder();
            groupEmbed.setTitle(group.getName() + " (" + group.getId() + ")");

            if (group.getPermissions().isEmpty()) {
                groupEmbed.addField(context.i18n("words.permissions"), context.i18n("utils.permission_command.no_permissions"), false);
            } else {
                Table.TableBuilder tableBuilder = new Table.TableBuilder();
                tableBuilder.addHeading(context.i18n("words.permission"));

                //integer for detecting when we hit 5 permissions so we can stop adding more.
                int pi = 0;

                for (String perm : group.getPermissions()) {
                    tableBuilder.addRow(perm);
                    if (pi >= 5) {
                        break;
                    }
                    pi++;
                }

                groupEmbed.addField(context.i18n("words.permissions"), tableBuilder.build().toString(), false);
            }

            if (group.getRoleIds().isEmpty()) {
                groupEmbed.addField(context.i18n("words.linked_roles"), context.i18n("utils.permission_command.no_linked_roles"), false);
            } else {
                StringBuilder rolesBuilder = new StringBuilder();

                //integer for detecting when we hit 5 roles so we can stop adding more.
                int ri = 0;

                for (Long roleId : group.getRoleIds()) {
                    Role role = context.getGuild().getRoleById(roleId);
                    if (role == null) continue;
                    rolesBuilder.append(role.getName()).append(" (").append(role.getId()).append(")\n");
                    if (ri >= 5) {
                        break;
                    }
                    ri++;
                }

                groupEmbed.addField(context.i18n("words.linked_roles"), "```" + rolesBuilder.toString() + "```", false);
            }

            CascadeActionRow groupPageRow = new CascadeActionRow();

            groupPageRow.addComponent(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.LEFT_ARROW), (runner, channel, message) -> {
                handleSwitchButtons(runner, message, groupsEmbed.build(), container, firstPageRow, context);
            }));

            groupPageRow.addComponent(CascadeButton.success("Select", Emoji.fromUnicode(UnicodeConstants.TICK), (runner, channel, message) -> {
                if (runner.getIdLong() != context.getMember().getIdLong()) {
                    return;
                }
                message.getMessage().delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                groupConsumer.accept(group);
            }));

            firstPageRow.addComponent(CascadeButton.secondary(Emoji.fromUnicode(unicode + "\u20E3"), (runner, channel, message) -> {
                handleSwitchButtons(runner, message, groupEmbed.build(), container, groupPageRow, context);
            }));

            i++;
        }
        firstPageRow.addComponent(CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.RED_CROSS), ((runner, channel, message) -> {
            if (runner.getIdLong() != sender) {
                return;
            }
            message.getMessage().delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
        })));
        container.addRow(firstPageRow);

        groupsEmbed.setDescription(groupsBuilder.toString());
        context.getUiMessaging().sendComponentMessage(groupsEmbed.build(), container);
    }

    private static void handleSwitchButtons(Member member, InteractionMessage message, MessageEmbed embedToSwitchTo, ComponentContainer container, CascadeActionRow buttonsToSwitchTo, CommandContext context) {
        if (member.getIdLong() != context.getMember().getIdLong()) {
            return;
        }
        container.setRow(0, buttonsToSwitchTo);
        message.editMessage(embedToSwitchTo).override(true).queue();
        context.getData().addComponents(context.getChannel(), message.getMessage(), container);
    }

}
