package org.cascadebot.cascadebot.messaging

import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.UnicodeConstants
import java.awt.Color
import java.time.LocalDateTime
import java.time.temporal.TemporalAccessor

class EmbedBuilder(val type: MessageType, val user: User? = null) {
    private var titleBuilder: TitleBuilder? = null
    private var authorBuilder: AuthorBuilder? = null
    private var footerBuilder: FooterBuilder? = null
    private var fieldBuilders: MutableList<FieldBuilder> = mutableListOf()

    var description: String? = null
    var imageUrl: String? = null
    var thumbnailUrl: String? = null
    var timestamp: TemporalAccessor? = null
    var color: Color? = null

    class TitleBuilder {
        var name: String? = null
        var url: String? = null

        fun build(): String {
            return "yeet"
        }
    }

    class AuthorBuilder {
        var name: String? = null
        var url: String? = null
        var iconUrl: String? = null
    }

    class FieldBuilder {
        var name: String? = null
        var value: String? = null
        var inline = false
    }

    class FooterBuilder {
        var text: String? = null
        var iconUrl: String? = null
    }

    fun title(init: TitleBuilder.() -> Unit) {
        titleBuilder = TitleBuilder().apply(init)
    }

    fun author(init: AuthorBuilder.() -> Unit) {
        authorBuilder = AuthorBuilder().apply(init)
    }

    fun field(init: FieldBuilder.() -> Unit) {
        fieldBuilders.add(FieldBuilder().apply(init))
    }

    fun footer(init: FooterBuilder.() -> Unit) {
        footerBuilder = FooterBuilder().apply(init)
    }

    fun blankField(inline: Boolean = false) {
        fieldBuilders.add(FieldBuilder().apply {
            name = UnicodeConstants.ZERO_WIDTH_SPACE
            value = UnicodeConstants.ZERO_WIDTH_SPACE
            this.inline = inline
        })
    }

    fun build(): net.dv8tion.jda.api.EmbedBuilder {
        val embedBuilder = user?.let { MessagingObjects.getMessageTypeEmbedBuilder(type, it) }
                ?: MessagingObjects.getMessageTypeEmbedBuilder(type)
        for (builder in fieldBuilders) {
            embedBuilder.addField(
                    builder.name,
                    builder.value,
                    builder.inline
            )
        }
        titleBuilder?.let { embedBuilder.setTitle(it.name, it.url) }
        authorBuilder?.let { embedBuilder.setAuthor(it.name, it.url, it.iconUrl) }
        footerBuilder?.let { embedBuilder.setFooter(it.text, it.iconUrl) }

        embedBuilder.setDescription(description)
        embedBuilder.setImage(imageUrl)
        embedBuilder.setThumbnail(thumbnailUrl)
        embedBuilder.setTimestamp(timestamp)
        embedBuilder.setColor(color)
        return embedBuilder
    }
}

fun embed(type: MessageType, user: User? = null, init: EmbedBuilder.() -> Unit): net.dv8tion.jda.api.EmbedBuilder = EmbedBuilder(type, user).apply(init).build()

/*
====================================
Full embed type-safe builder example
====================================
 */
fun getEmbed(): net.dv8tion.jda.api.EmbedBuilder {
    return embed(type = MessageType.INFO) {
        title {
            name = "title"
            url = "https://google.com"
        }
        author {
            name = "binary"
            url = "https://google.com"
            iconUrl = "https://google.com/favicon.ico"
        }
        footer {
            text = "footer"
            iconUrl = "https://google.com/favicon.ico"
        }
        field {
            name = "field1"
            value = "hello"
            inline = true
        }
        blankField(true)
        field {
            name = "field2"
            value = "hello"
            inline = true
        }
        description = "description"
        imageUrl = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"
        thumbnailUrl = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"
        timestamp = LocalDateTime.now()
        color = Color.CYAN
    }
}