/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.pagination;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.Table;

public class PageObjects {

    public static class EmbedPage implements Page {

        EmbedBuilder embed;
        boolean numbersInEmbed;

        public EmbedPage(EmbedBuilder embed) {
            this.embed = embed;
            numbersInEmbed = true;
        }

        public EmbedPage(EmbedBuilder embed, boolean numbersInEmbed) {
            this.embed = embed;
            this.numbersInEmbed = numbersInEmbed;
        }

        @Override
        public void pageShow(Message message, int page, int total) {
            if (GuildDataManager.getGuildData(message.getTextChannel().getGuild().getIdLong()).getSettings().useEmbedForMessages()) {
                if (numbersInEmbed) {
                    embed.setFooter("Page " + page + "/" + total, message.getAuthor().getAvatarUrl());
                    message.editMessage(embed.build()).override(true).queue();
                } else {
                    message.editMessage(new MessageBuilder().setEmbed(embed.build()).append("Page ").append(String.valueOf(page)).append("/").append(String.valueOf(total)).build()).override(true).queue();

                }
            } else {
                embed.setFooter("Page " + page + "/" + total, message.getAuthor().getAvatarUrl());
                String content = FormatUtils.formatEmbed(embed.build());
                message.editMessage(content).override(true).queue();
            }
        }

    }

    public static class StringPage implements Page {

        String content;

        public StringPage(String context) {
            this.content = context;
        }

        @Override
        public void pageShow(Message message, int page, int total) {
            message.editMessage(content + "\n\nPage " + page + "/" + total).override(true).queue();
        }

    }

    public static class TablePage implements Page {

        private Table table;
        boolean numbersInTable;

        public TablePage(Table table) {
            this(table, false);
        }

        public TablePage(Table table, boolean numbersInTable) {
            this.table = table;
            this.numbersInTable = numbersInTable;
        }

        @Override
        public void pageShow(Message message, int page, int total) {
            if (numbersInTable) {
                Table.TableBuilder builder = this.table.edit();
                builder.setFooter("Page " + page + "/" + total);
                message.editMessage(builder.build().toString()).override(true).queue();
            } else {
                String table = this.table.toString();
                table += "\n\nPage " + page + "/" + total;
                message.editMessage(table).override(true).queue();
            }
        }

    }

}
