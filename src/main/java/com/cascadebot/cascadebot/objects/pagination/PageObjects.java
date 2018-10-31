/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.objects.pagination;

import com.cascadebot.cascadebot.utils.FormatUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

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
            if(numbersInEmbed) {
                embed.setFooter("Page " + page + "/" + total, message.getAuthor().getAvatarUrl());
                message.editMessage(embed.build()).override(true).queue();
            } else {
                message.editMessage(new MessageBuilder().setEmbed(embed.build()).setContent("\u200B").append("Page ").append(String.valueOf(page)).append("/").append(String.valueOf(total)).build()).override(true).queue();
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

        List<String> header;
        List<List<String>> body;
        String footer = "";

        boolean numbersInTable = true;

        public TablePage(List<String> header, List<List<String>> body) {
            this.header = header;
            this.body = body;
        }

        public TablePage(List<String> header, List<List<String>> body, boolean numbersInTable) {
            this.header = header;
            this.body = body;
            this.numbersInTable = numbersInTable;
        }

        public TablePage(List<String> header, List<List<String>> body, String footer) {
            this.header = header;
            this.body = body;
            this.footer = footer;
            this.numbersInTable = false;
        }

        @Override
        public void pageShow(Message message, int page, int total) {
            if(numbersInTable) {
                footer = "Page " + page + "/" + total;
            }
            String table = FormatUtils.makeAsciiTable(header, body, footer);
            if(!numbersInTable) table += "\n\nPage " + page + "/" + total;
            message.editMessage(table).override(true).queue();
        }
    }
}
