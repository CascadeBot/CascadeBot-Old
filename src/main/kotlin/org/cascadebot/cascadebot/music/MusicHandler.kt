package org.cascadebot.cascadebot.music

import com.google.gson.JsonParser
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import lavalink.client.io.jda.JdaLavalink
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.data.objects.Flag
import org.cascadebot.cascadebot.events.PlayerListener
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.Messaging
import org.cascadebot.cascadebot.messaging.Messaging.sendExceptionMessage
import org.cascadebot.cascadebot.utils.PasteUtils
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.function.Consumer
import java.util.function.Function
import java.util.regex.Pattern

class MusicHandler {
    val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    val typePattern = Pattern.compile("youtube#([A-z]+)")

    val youtubeSourceName: String
    val twitchSourceName: String

    val players: MutableMap<Long, CascadePlayer> = HashMap()
    var musicJsonParser = JsonParser()
    var lavaLink: JdaLavalink? = null
        private set

    var lavalinkEnabled = false

    init {
        AudioSourceManagers.registerRemoteSources(playerManager)

        val youtubeAudioSourceManager = YoutubeAudioSourceManager(false)
        youtubeAudioSourceManager.configureRequests { RequestConfig.copy(it).setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(5000).build() }
        youtubeSourceName = youtubeAudioSourceManager.sourceName
        playerManager.registerSourceManager(youtubeAudioSourceManager)

        val twitchAudioManager = TwitchStreamAudioSourceManager()
        twitchSourceName = twitchAudioManager.sourceName
        playerManager.registerSourceManager(twitchAudioManager)
        playerManager.registerSourceManager(BeamAudioSourceManager())
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault())
        playerManager.registerSourceManager(BandcampAudioSourceManager())
        if (Config.INS.musicNodes.size > 0) {
            lavaLink = JdaLavalink(Config.INS.botId.toString(), Config.INS.shardNum) { CascadeBot.INS.shardManager.getShardById(it!!) }
            for (musicNode in Config.INS.musicNodes) {
                lavaLink!!.addNode(musicNode.uri, musicNode.password) //TODO give nodes a name
            }
            lavalinkEnabled = true
        } else {
            lavalinkEnabled = false
        }
    }

    fun getPlayer(guildId: Long): CascadePlayer? {
        if (players.containsKey(guildId)) {
            return players[guildId]
        }
        val guild = CascadeBot.INS.shardManager.getGuildById(guildId)
        return if (guild != null) createPlayer(guild) else null
    }

    private fun createPlayer(guild: Guild): CascadePlayer {
        val player: CascadePlayer
        if (lavalinkEnabled) {
            val link = lavaLink!!.getLink(guild)
            player = CascadeLavalinkPlayer(link.player)
        } else {
            val aPlayer = createLavaplayerPlayer()
            player = CascadeLavaplayerPlayer(aPlayer)
            guild.audioManager.sendingHandler = LavaPlayerAudioSendHandler(aPlayer)
        }
        player.guild = guild
        player.addListener(PlayerListener(player)) //TODO dispose of players after a while
        return player
    }

    fun removePlayer(guildId: Long): Boolean {
        return players.remove(guildId) != null
    }

    fun purgeDisconnectedPlayers() {
        // Removes all players that are not connected to a channel unless they have supported us on Patreon
        players.entries.removeIf { it.value.connectedChannel == null && !GuildDataManager.getGuildData(it.key).isFlagEnabled(Flag.MUSIC_SERVICES) }
    }

    /**
     * Searches for a list of 5 tracks and if it errors send the error to the specified channel
     *
     * @param search  The string to search
     * @param channel The [TextChannel] to send any errors to
     * @return The list of tracks that where found
     */
    fun searchTracks(search: String?, channel: TextChannel, searchResultConsumer: Consumer<List<SearchResult>?>) {
        if (StringUtils.isBlank(Config.INS.youtubeKey)) {
            // TODO: Some way to disable searching?
            return
        }
        val request = Request.Builder().url("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + URLEncoder.encode(search, StandardCharsets.UTF_8) + "&key=" + URLEncoder.encode(Config.INS.youtubeKey, StandardCharsets.UTF_8) + "&maxResults=5&type=video,playlist").build()
        CascadeBot.INS.httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                sendExceptionMessage(channel, Language.i18n(channel.guild.idLong, "music.handler.error_searching"), e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.body == null) {
                    Messaging.sendMessage(MessageType.DANGER, channel, Language.i18n(channel.guild.idLong, "music.handler.no_data_returned"))
                    return
                }
                try {
                    if (!response.isSuccessful) {
                        Messaging.sendMessage(MessageType.DANGER, channel, Language.i18n(channel.guild.idLong, "music.handler.query_unsuccessful", PasteUtils.paste(response.body!!.string())))
                        return
                    }
                    val searchResults: MutableList<SearchResult> = ArrayList()
                    val json = musicJsonParser.parse(response.body!!.string()).asJsonObject
                    val items = json.getAsJsonArray("items")
                    var i = 0
                    for (elm in items) {
                        i++
                        if (i > 5) {
                            CascadeBot.LOGGER.warn(Language.i18n(channel.guild.idLong, "music.handler.more_than_five"))
                            break
                        }
                        val item = elm.asJsonObject
                        val idElm = item.getAsJsonObject("id")
                        var type = idElm["kind"].asString
                        val matcher = typePattern.matcher(type)
                        if (!matcher.matches()) {
                            break
                        }
                        type = matcher.group(1)
                        var url = ""
                        var searchResultType: SearchResultType? = null
                        when (type) {
                            "playlist" -> {
                                searchResultType = SearchResultType.PLAYLIST
                                url = "https://www.youtube.com/playlist?list=" + URLEncoder.encode(idElm["playlistId"].asString, StandardCharsets.UTF_8)
                            }
                            "video" -> {
                                searchResultType = SearchResultType.VIDEO
                                url = "https://www.youtube.com/watch?v=" + URLEncoder.encode(idElm["videoId"].asString, StandardCharsets.UTF_8)
                            }
                        }
                        val snippetElm = item.getAsJsonObject("snippet")
                        val title = snippetElm["title"].asString
                        searchResults.add(SearchResult(searchResultType!!, url, title))
                    }
                    searchResultConsumer.accept(searchResults)
                } catch (e: IOException) {
                    sendExceptionMessage(channel, Language.i18n(channel.guild.idLong, "music.handler.error_reading"), e)
                } catch (e: Exception) {
                    sendExceptionMessage(channel, Language.i18n(channel.guild.idLong, "music.handler.error_processing"), e)
                }
            }
        })
    }

    private fun createLavaplayerPlayer(): AudioPlayer {
        return playerManager.createPlayer()
    }

    class MusicNode(val uri: URI, val password: String)

    class SearchResult(val type: SearchResultType, val url: String, val title: String)

    enum class SearchResultType {
        VIDEO, PLAYLIST
    }

}
