/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.Table;
import org.cascadebot.cascadebot.utils.interactions.InteractionMessage;

public class PageObjects {

    public static class EmbedPage implements Page {

        private final EmbedBuilder embed;
        private final boolean numbersInEmbed;
        private String title = null;

        public EmbedPage(EmbedBuilder embed) {
            this.embed = new EmbedBuilder(embed);
            this.numbersInEmbed = true;
        }

        public EmbedPage(EmbedBuilder embed, String title) {
            this.embed = embed;
            this.numbersInEmbed = true;
            this.title = title;
        }

        public EmbedPage(EmbedBuilder embed, boolean numbersInEmbed) {
            this.embed = new EmbedBuilder(embed);
            this.numbersInEmbed = numbersInEmbed;
        }

        public EmbedPage(EmbedBuilder embed, String title, boolean numbersInEmbed) {
            this.embed = embed;
            this.numbersInEmbed = numbersInEmbed;
            this.title = title;
        }

        @Override
        public void pageShow(InteractionMessage message, int page, int total) {
            GuildData data = GuildDataManager.getGuildData(message.getMessage().getTextChannel().getGuild().getIdLong());
            if (data.getCore().getUseEmbedForMessages()) {
                if (numbersInEmbed) {
                    if (total > 1) {
                        embed.setFooter(Language.i18n(data.getLocale(), "page_objects.page_footer", page, total), message.getMessage().getAuthor().getAvatarUrl());
                    }
                    message.editMessage(embed.build()).queue();
                } else {
                    var messageBuilder = new MessageBuilder().setEmbed(embed.build());
                    if (total > 1) {
                        messageBuilder.append(Language.i18n(data.getLocale(), "page_objects.page_footer", page, total));
                    }
                    message.editMessage(messageBuilder.build()).queue();

                }
            } else {
                if (total > 1) {
                    embed.setFooter(Language.i18n(data.getLocale(), "page_objects.page_footer", page, total), message.getMessage().getAuthor().getAvatarUrl());
                }
                String content = FormatUtils.formatEmbed(embed.build());
                message.editMessage(content).queue();
            }
        }

        @Override
        public String getTitle() {
            if (title == null) {
                return embed.build().getTitle();
            } else {
                return title;
            }
        }

    }

    @Getter
    @Setter
    public static class StringPage implements Page {

        private String content;
        private String title = null;

        public StringPage(String content) {
            this.content = content;
        }

        public StringPage(String content, String title) {
            this.content = content;
            this.title = title;
        }

        @Override
        public void pageShow(InteractionMessage message, int page, int total) {
            String content = this.content;
            if (total > 1) {
                content += "\n\n" + Language.i18n(message.getMessage().getGuild().getIdLong(), "page_objects.page_footer", page, total);
            }
            message.editMessage(content).queue();
        }

        @Override
        public String getTitle() {
            return title;
        }

    }

    public static class TablePage implements Page {

        private final Table table;
        private boolean numbersInTable = true;
        private String title = null;

        public TablePage(Table table) {
            this.table = table;
        }

        public TablePage(Table table, String title) {
            this.table = table;
            this.title = title;
        }

        public TablePage(Table table, boolean numbersInTable) {
            this.table = table;
            this.numbersInTable = numbersInTable;
        }

        public TablePage(Table table, String title, boolean numbersInTable) {
            this.table = table;
            this.title = title;
            this.numbersInTable = numbersInTable;
        }

        @Override
        public void pageShow(InteractionMessage message, int page, int total) {
            if (numbersInTable && total > 1) {
                Table.TableBuilder builder = this.table.edit();
                builder.setFooter(Language.i18n(message.getMessage().getGuild().getIdLong(), "page_objects.page_footer", page, total));
                message.editMessage(builder.build().toString()).queue();
            } else {
                String table = this.table.toString();
                table += "\n\n" + Language.i18n(message.getMessage().getGuild().getIdLong(), "page_objects.page_footer", page, total);
                message.editMessage(table).queue();
            }
        }

        @Override
        public String getTitle() {
            return title;
        }

    }

}
