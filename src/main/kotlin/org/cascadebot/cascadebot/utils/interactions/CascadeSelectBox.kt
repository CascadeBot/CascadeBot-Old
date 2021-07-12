package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.Component
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu
import org.cascadebot.cascadebot.utils.buttons.ISelectionRunnable

class CascadeSelectBox : CascadeComponent {
    private val id : String
    private val consumer: ISelectionRunnable
    private val builder: SelectionMenu.Builder

    private var minSelect = 1;
    private var maxSelect = 1;

    private val defaults: MutableList<String> = mutableListOf()

    constructor(id: String, consumer: ISelectionRunnable) {
        this.id = id;
        builder = SelectionMenu.create(id)
        this.consumer = consumer
    }

    fun setMinSelect(num: Int) {
        if (num < 1 || num > maxSelect) {
            throw UnsupportedOperationException("Minimum selection number cannot be less then 1 or greater then the max select value")
        }
        minSelect = num;
    }

    fun setMaxSelect(num: Int) {
        if (num > 25 || num < minSelect) {
            throw UnsupportedOperationException("Maximum selection number cannot be greater then 25 or less then the min select value")
        }
        maxSelect = num;
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

    override fun getDiscordComponent(): Component {
        builder.maxValues = maxSelect;
        builder.minValues = minSelect;
        builder.setDefaultValues(defaults)
        return builder.build()
    }

    override fun getId(): String {
        return id
    }

}