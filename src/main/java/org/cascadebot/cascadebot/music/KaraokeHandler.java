package org.cascadebot.cascadebot.music;

import net.dv8tion.jda.api.entities.TextChannel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.tools.ant.filters.StringInputStream;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class KaraokeHandler {

    public boolean karaokeStatus(long guildId) { return false; }

    public static void getSongLyrics(String trackId, TextChannel channel) throws ParserConfigurationException {
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

                try {
                    if (response.body().string().equals("")) {
                        Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), ""));
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    String body = response.body().string();
                    Document doc = builder.parse(new StringInputStream(body));
                    NodeList texts = doc.getElementsByTagName("text");
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < texts.getLength(); i++) {
                        result.append(texts.item(i).getNodeName()).append("\n");
                    }
                    System.out.println(result.toString());
                } catch (IOException | SAXException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
