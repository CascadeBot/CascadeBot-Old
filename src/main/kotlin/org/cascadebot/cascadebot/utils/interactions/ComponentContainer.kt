package org.cascadebot.cascadebot.utils.interactions

class ComponentContainer {

    private val components: MutableList<CascadeActionRow> =
        mutableListOf() // TODO make this able to contain more then just action rows (will probably do when discord allows more then just action rows)

    fun addRow(actionRow: CascadeActionRow) {
        if (components.size >= 5) {
            throw UnsupportedOperationException("Cannot have more then 5 action rows!")
        }

        components.add(actionRow)
    }

    fun getComponents(): List<CascadeActionRow> {
        return components.toList()
    }

}