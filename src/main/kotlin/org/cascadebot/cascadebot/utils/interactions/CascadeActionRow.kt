package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.interactions.components.Component

class CascadeActionRow {

    private var componentType: Component.Type? = null
    private val components: MutableList<CascadeComponent> = mutableListOf()

    fun addComponent(component: CascadeComponent) {
        if (componentType == null) {
            components.add(component)
            componentType = getComponentType(component)
        } else { // TODO if this gets too much to handle this way add getCompatibleComponents and getMaxComponents methods to cascade component to simplify this. Maybe do it anyways for future proofing.
            if (componentType == Component.Type.SELECTION_MENU) {
                throw UnsupportedOperationException("Only one section box is allowed per action row and selection boxes and buttons aren't allowed together")
            } else if (componentType == Component.Type.BUTTON) {
                if (getComponentType(component) != Component.Type.BUTTON) {
                    throw UnsupportedOperationException("Selection boxes and buttons aren't allowed on the same action row")
                }
                if (components.size >= 5) {
                    throw UnsupportedOperationException("Can only Have 5 buttons per action row")
                }
                components.add(component)
            }
        }
    }

    private fun getComponentType(component: CascadeComponent) : Component.Type {
        return when (component) {
            is CascadeButton -> Component.Type.BUTTON
            is CascadeSelectBox -> Component.Type.SELECTION_MENU
            else -> Component.Type.UNKNOWN
        }
    }

}