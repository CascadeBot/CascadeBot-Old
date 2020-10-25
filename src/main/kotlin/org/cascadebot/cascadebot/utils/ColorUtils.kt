package org.cascadebot.cascadebot.utils

import com.google.gson.JsonPrimitive
import lombok.AllArgsConstructor
import lombok.Getter
import net.dv8tion.jda.api.entities.MessageEmbed
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.objects.ColorErrorType
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.MessagingObjects
import java.awt.Color
import java.util.regex.Matcher
import java.util.regex.Pattern

object ColorUtils {

    private val HEX_COLOR = Pattern.compile("#([A-Fa-f0-9]+)")
    private val DECIMAL_COLOR = Pattern.compile("([0-9]{1,8})")
    private val RGB_COLOR = Pattern.compile("(\\d{1,3})[, ](\\d{1,3})[, ](\\d{1,3})")
    private val BINARY_COLOR = Pattern.compile("([0-1]+)")

    fun getHex(r: Int, g: Int, b: Int): String {
        return String.format("%02x%02x%02x", r, g, b)
    }

    @JvmStatic
    @Throws(ColorException::class)
    fun getColor(text: String, context: CommandContext): Color {
        var matcher: Matcher
        val name = getColorNameFromLocale(context.locale, text)
        val color = getCssColor(name)
        if (color != null) {
            return color
        }
        if (RGB_COLOR.matcher(text).also { matcher = it }.find()) {
            return try {
                Color(matcher.group(1).toInt(), matcher.group(2).toInt(), matcher.group(3).toInt())
            } catch (e: IllegalArgumentException) {
                throw ColorException(ColorErrorType.RGB)
            }
        }
        if (HEX_COLOR.matcher(text).also { matcher = it }.matches()) {
            return try {
                Color.decode(matcher.group())
            } catch (e: NumberFormatException) {
                throw ColorException(ColorErrorType.HEX)
            }
        }
        if (DECIMAL_COLOR.matcher(text).also { matcher = it }.matches()) {
            return try {
                Color.decode(matcher.group())
            } catch (e: NumberFormatException) {
                throw ColorException(ColorErrorType.DECIMAL)
            }
        }
        if (BINARY_COLOR.matcher(text).also { matcher = it }.matches()) {
            return try {
                Color.decode(Integer.parseUnsignedInt(matcher.group(), 2).toString())
            } catch (e: NumberFormatException) {
                throw ColorException(ColorErrorType.BINARY)
            }
        }
        throw ColorException(ColorErrorType.UNRECOGNISED)
    }


    class ColorException(val type: ColorErrorType) : Exception() {

        fun getI18nMessage(locale: Locale): String? {
            return when (type) {
                ColorErrorType.RGB -> Language.i18n(locale, "utils.color.invalid_rgb")
                ColorErrorType.BINARY -> Language.i18n(locale, "utils.color.invalid_binary")
                ColorErrorType.HEX -> Language.i18n(locale, "utils.color.invalid_hex")
                ColorErrorType.DECIMAL -> Language.i18n(locale, "utils.color.invalid_decimal")
                ColorErrorType.UNRECOGNISED -> Language.i18n(locale, "utils.color.color_not_recognised")
            }
        }
    }

    @JvmStatic
    fun getColorEmbed(color: Color, context: CommandContext): MessageEmbed {
        val rgbValues = "${color.red},${color.green},${color.blue}"
        val hex = "#" + getHex(color.red, color.green, color.blue)
        val decimalColor = color.rgb
        return MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, context.user, context.locale).apply {
            setTitle(context.i18n("utils.color.embed_title", CSSColor.getLocalNameOrDefault(context.locale, color, hex)))
            setColor(color)
            addField(context.i18n("utils.color.hex"), hex, true) // Hex Value
            addField(context.i18n("utils.color.rgb"), rgbValues, true) // RGB Values
            addField(context.i18n("utils.color.decimal"), Integer.toUnsignedString(decimalColor), true) // Decimal Value
            addField(context.i18n("utils.color.binary"), Integer.toBinaryString(decimalColor), true)  // Binary Value
        }.build()
    }

    // Gets a color name based on current locale, if not found return original input
    // This method needs a serious overhaul since I don't know how JSONConfig works...
    private fun getColorNameFromLocale(locale: Locale, name: String): String {
        // Get language config
        val lang = Language.getLanguage(locale)
        // Get sub config that defines the colors
        val sub = lang!!.getSubConfig("utils.color.colors")
        // If the sub config is not present return the name
        if (sub.isEmpty) {
            return name
        }

        // Get the sub config
        val colorsLangConfig = sub.get()
        // Get all the values from the sub config
        val colors = colorsLangConfig.getValues(false)

        // Loop over all values
        for ((key, value) in colors) {
            // Check if the value (color translation) is equal to the name provided
            if ((value as JsonPrimitive).asString.equals(name, ignoreCase = true)) {
                return key
            }
        }
        // Return input when nothing has been found
        return name
    }

    fun getCssColor(name: String): Color? {
        return try {
            CSSColor.valueOf(name.toUpperCase()).color
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
