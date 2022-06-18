package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.interactions.components.ActionRow

class CascadeActionRow {

    private var components: MutableList<CascadeComponent> = mutableListOf()

    val persistent
        get() = components.any { it.persistent }

    fun addComponent(component: CascadeComponent) {
        // Add to copy so that list isn't changed until checks have been done
        doComponentChecks(components.toMutableList().apply { add(component) })
        components.add(component)
    }

    fun addComponent(index: Int, component: CascadeComponent) {
        // Add to copy so that list isn't changed until checks have been done
        doComponentChecks(components.toMutableList().apply { add(index, component) })
        components.add(index, component)
    }

    fun setComponent(index: Int, component: CascadeComponent) {
        // Set in copy so that list isn't changed until checks have been done
        doComponentChecks(components.toMutableList().apply { this[index] = component })
        components[index] = component
    }

    fun deleteComponent(index: Int) {
        components.removeAt(index)
    }

    private fun doComponentChecks(components: List<CascadeComponent>) {

        if (components.isEmpty()) return

        // Check that all values are only true or only false, not a mix
        require(components.groupBy { it.persistent }.size > 1) {
            "Cannot mix non-persistent items and persistent items in a row"
        }

        // Check that all component unique IDs are distinct
        require(components.distinctBy { it.uniqueId }.size == components.size) {
            "The row contains a duplicate component ID"
        }

        require(components.groupBy { it.componentType }.size == 1) {
            "Cannot mix different types of components on a single row"
        }

        // Since all component types are the same, we can use the first one to determine the max per row
        require(components.size <= components[0].componentType.maxPerRow) {
            "Cannot add component to row as the maximum number of components (${components[0].componentType.maxPerRow}) has been reached"
        }
    }

    fun toDiscordActionRow(): ActionRow {
        return ActionRow.of(components.map { it.discordComponent })
    }

    fun getComponents(): List<CascadeComponent> {
        return components.toList()
    }

}