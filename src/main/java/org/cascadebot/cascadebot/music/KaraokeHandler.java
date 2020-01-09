/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.MDCException;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.messaging.MessagingTyped;
import org.cascadebot.cascadebot.tasks.Task;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class KaraokeHandler {

    public static int CAPTION_BUFFER_TIME = 15;

    private static Set<Long> karaokeStatus = new HashSet<>();

    public static boolean isKaraoke(long guildId) {
        return Task.getTasks().containsKey("captions-" + guildId);
    }

    public static void disableKaraoke(long guildId) {
        Task.cancelTask("captions-" + guildId);
    }

    public static void getSongLyrics(String trackId, TextChannel channel, long guildId, Message message, CommandContext context) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "commands.karaoke.cannot_find"));
            CascadeBot.LOGGER.error("Error in karaoke handler", e);
        }
        Request request = new Request.Builder().url("https://video.google.com/timedtext?lang=en&v=" + trackId).build();
        DocumentBuilder finalBuilder = builder;

        CascadeBot.INS.getHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Messaging.sendExceptionMessage(channel, Language.i18n(channel.getGuild().getIdLong(), ""), e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.body() == null || response.code() != 200) {
                    Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "commands.karaoke.cannot_find"));
                    return;
                }

                String body = null;
                try {
                    body = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (body.equals("")) {
                    Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "commands.karaoke.cannot_find"));
                    return;
                }

                try {
                    Document doc = finalBuilder.parse(new StringInputStream(body));
                    NodeList texts = doc.getElementsByTagName("text");
                    Captions captions = new Captions();
                    for (int i = 0; i < texts.getLength(); i++) {
                        NamedNodeMap attrs = texts.item(i).getAttributes();
                        Node dur = attrs.getNamedItem("dur");
                        Node start = attrs.getNamedItem("start");

                        if (dur != null && start != null) {
                            captions.addCaption(StringEscapeUtils.unescapeHtml(texts.item(i).getTextContent()), Double.parseDouble(start.getNodeValue()), Double.parseDouble(dur.getNodeValue()));
                        }
                    }

                    new CaptionsTask(guildId, channel.getIdLong(), message.getIdLong(), captions).start(0, CAPTION_BUFFER_TIME * 1000);

                } catch (IOException | SAXException e) {
                    context.getTypedMessaging().replyException(context.i18n("commands.karaoke.cannot_find"), e);
                }
            }

        });
    }

}
