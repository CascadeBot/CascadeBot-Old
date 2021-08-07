package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.Component
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu

class CascadeSelectBox(id: String, val consumer: ISelectionRunnable) : CascadeComponent(id) {

    private val builder: SelectionMenu.Builder = SelectionMenu.create(id)

    private var minSelect = 1
        set(value) {
            if (value < 1 || value > maxSelect) {
                throw UnsupportedOperationException("Minimum selection number cannot be less then 1 or greater then the max select value")
            }
            field = value
        }
    private var maxSelect = 1
        set(value) {
            if (value > 25 || value < minSelect) {
                throw UnsupportedOperationException("Maximum selection number cannot be greater then 25 or less then the min select value")
            }
            field = value
        }

    private val defaults: MutableList<String> = mutableListOf()

    override val discordComponent: Component
        get() {
            builder.maxValues = maxSelect;
            builder.minValues = minSelect;
            builder.setDefaultValues(defaults)
            return builder.build()
        }


    fun addOption(label: String, default: Boolean = false) {
        builder.addOption(label, label)
        handleDefault(label, default)
    }

    fun addOption(label: String, description: String, default: Boolean = false) {
        builder.addOption(label, label, description)
        handleDefault(label, default)
    }

    fun addOption(label: String, emoji: Emoji, default: Boolean = false) {
        val value = "$label-" + if (emoji.isUnicode) {
            emoji.name
        } else {
            emoji.id
        }
        builder.addOption(label, value, emoji)
        handleDefault(value, default)
    }

    fun addOption(label: String, description: String, emoji: Emoji, default: Boolean = false) {
        val value = "$label-" + if (emoji.isUnicode) {
            emoji.name
        } else {
            emoji.id
        }
        builder.addOption(label, value, description, emoji)
        handleDefault(value, default)
    }

    private fun handleDefault(value: String, default: Boolean) {
        if (default) {
            if (defaults.size >= maxSelect) {
                throw UnsupportedOperationException("Cannot add more defaults then the user can select")
            }
            defaults.add(value)
        }
    }

    fun setPlaceholder(placeHolder: String) {
        builder.placeholder = placeHolder
    }

    fun addDefault(value: String) {
        handleDefault(value, true)
    }

    fun clearDefaults() {
        defaults.clear()
    }

}