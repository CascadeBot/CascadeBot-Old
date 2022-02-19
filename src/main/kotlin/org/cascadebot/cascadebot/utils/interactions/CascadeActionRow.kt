package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Component

class CascadeActionRow {

    var componentType: Component.Type? = null
    private val components: MutableList<CascadeComponent> = mutableListOf()

    private var persistent = false;

    fun addComponent(component: CascadeComponent) {
        doComponentChecks(component)
        components.add(component)
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
            persistent = PersistentComponent.values().map { it.component }.contains(component)
            componentType = getComponentType(component)
        } else { // TODO if this gets too much to handle this way add getCompatibleComponents and getMaxComponents methods to cascade component to simplify this. Maybe do it anyways for future proofing.
            if (persistent && !PersistentComponent.values().map { it.component }.contains(component)) {
                throw UnsupportedOperationException("Cannot add non-persistent items to persistent rows")
            }
            if (componentType == Component.Type.SELECTION_MENU) {
                throw UnsupportedOperationException("Only one section box is allowed per action row and selection boxes and buttons aren't allowed together")
            } else if (componentType == Component.Type.BUTTON) {
                if (getComponentType(component) != Component.Type.BUTTON) {
                    throw UnsupportedOperationException("Selection boxes and buttons aren't allowed on the same action row")
                }
                /*if (components.size >= 5) {
                    throw UnsupportedOperationException("Can only Have 5 buttons per action row")
                }*/
            }
        }
    }

    fun isPersistent(): Boolean {
        return persistent
    }

    private fun getComponentType(component: CascadeComponent): Component.Type {
        return when (component) {
            is CascadeButton -> Component.Type.BUTTON
            is CascadeSelectBox -> Component.Type.SELECTION_MENU
            else -> Component.Type.UNKNOWN
        }
    }

    fun toDiscordActionRow(): ActionRow {
        return ActionRow.of(components.map { it.discordComponent })
    }

    fun getComponents(): List<CascadeComponent> {
        return components.toList()
    }

}