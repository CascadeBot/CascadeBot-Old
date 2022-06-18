package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.interactions.components.ActionRow
import org.cascadebot.cascadebot.utils.ChannelId

class ComponentContainer {

    private var components: MutableList<CascadeActionRow> =
        mutableListOf() // TODO make this able to contain more then just action rows (will probably do when discord allows more then just action row)

    private val persistent
        get() = components.any { it.persistent }

    fun addRow(actionRow: CascadeActionRow) {
        doChecks(actionRow)
        components.add(actionRow)
    }

    fun setRow(pos: Int, actionRow: CascadeActionRow) {
        doChecks(actionRow)
        components[pos] = actionRow
    }

    fun getRow(pos: Int): CascadeActionRow {
        return components[pos]
    }

    private fun doChecks(actionRow: CascadeActionRow) {
        if (persistent && !actionRow.persistent) {
            throw UnsupportedOperationException("Cannot add non-persistent rows to persistent containers")
        }

        if (components.size >= 5) {
            throw UnsupportedOperationException("Cannot have more then 5 action rows!")
        }
    }

    fun getComponents(): List<CascadeActionRow> {
        return components.toList()
    }

    companion object {
        fun fromDiscordObjects(channelId: ChannelId, actionRows: List<ActionRow>): ComponentContainer {
            val componentContainer = ComponentContainer()
            componentContainer.components = actionRows.map { CascadeActionRow.fromDiscordActionRow(channelId, it) }.toMutableList()
            return componentContainer
        }
    }

}