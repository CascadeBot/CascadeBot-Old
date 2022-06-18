package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Component

class CascadeActionRow {

    var componentType: Component.Type? = null
    private val components: MutableList<CascadeComponent> = mutableListOf()

    val persistent
        get() = components.any { it.persistent }

    fun addComponent(component: CascadeComponent) {
        doComponentChecks(component)
        components.add(component)
    }

    fun addComponent(index: Int, component: CascadeComponent) {
        doComponentChecks(component)
        components.add(index, component)
    }

    fun setComponent(pos: Int, component: CascadeComponent) {
        doComponentChecks(component)
        components[pos] = component
    }

    fun deleteComponent(pos: Int) {
        components.removeAt(pos)
    }

    private fun doComponentChecks(component: CascadeComponent) {
        if (componentType == null) {
            componentType = component.componentType
            return
        }

        require(persistent == component.persistent) { "Cannot mix non-persistent items and persistent items in a row" }
        require(components.none { it.uniqueId == component.uniqueId }) { "The component with unique id ${component.uniqueId} already exists in this row"}

        // TODO if this gets too much to handle this way add getCompatibleComponents and getMaxComponents methods to cascade component to simplify this. Maybe do it anyways for future proofing.
        if (componentType == Component.Type.SELECTION_MENU) {
            throw UnsupportedOperationException("Only one section box is allowed per action row and selection boxes and buttons aren't allowed together")
        } else if (componentType == Component.Type.BUTTON) {
            if (component.componentType != Component.Type.BUTTON) {
                throw UnsupportedOperationException("Selection boxes and buttons aren't allowed on the same action row")
            }
            /*if (components.size >= 5) {
                throw UnsupportedOperationException("Can only Have 5 buttons per action row")
            }*/
        }
    }

    fun toDiscordActionRow(): ActionRow {
        return ActionRow.of(components.map { it.discordComponent })
    }

    fun getComponents(): List<CascadeComponent> {
        return components.toList()
    }

}