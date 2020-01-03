package org.cascadebot.cascadebot.music;



import io.netty.util.AttributeMap;
import net.dv8tion.jda.api.entities.TextChannel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.MDCException;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.messaging.Messaging;
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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class KaraokeHandler {

    public static void getSongLyrics(String trackId, TextChannel channel, Long guildId) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Request request = new Request.Builder().url("https://video.google.com/timedtext?lang=en&v=" + trackId).build();
        CascadeBot.INS.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Messaging.sendExceptionMessage(channel, Language.i18n(channel.getGuild().getIdLong(), ""), e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {

                if (response.body() == null) {
                    Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), ""));
                    return;
                }

                if (response.code() != 200) {
                    Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), ""));
                    return;
                }

                String body = "";
                try {
                    body = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (body.equals("")) {
                    Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), ""));
                    return;
                }

                try {
                    Document doc = builder.parse(new StringInputStream(body));
                    NodeList texts = doc.getElementsByTagName("text");
                    StringBuilder lyrics = new StringBuilder();
                    Captions captions = new Captions();
                    for (int i = 0; i < texts.getLength(); i++) {
                        NamedNodeMap attrs = texts.item(i).getAttributes();
                        Node dur = attrs.getNamedItem("dur");
                        Node start = attrs.getNamedItem("start");

                        if (dur != null && start != null) {
                            captions.addCaption(StringEscapeUtils.unescapeHtml(texts.item(i).getTextContent()), Double.parseDouble(start.getNodeValue()), Double.parseDouble(dur.getNodeValue()));
                        }

//                        for (int j = 0; j < attrs.getLength(); j++) {
//
//                            lyrics.append("Name:").append(attrs.item(j).getNodeName()).append("\nValue: ").append(attrs.item(j).getNodeValue()).append("\n");
//                        }
//                        lyrics.append("\n");
                    }
                    System.out.println(captions.getCaptions(120).stream().collect(Collectors.joining("\n")));
                } catch (IOException | SAXException | MDCException e) {
                    e.printStackTrace();
                }

                System.out.println("Position: " + (CascadeBot.INS.getMusicHandler().getPlayer(guildId).getPlayer().getPlayingTrack().getPosition() / 1000));
            }
        });
    }

}
