package org.cascadebot.cascadebot.utils.interactions

class ComponentContainer {

    private val components: MutableList<CascadeActionRow> =
        mutableListOf() // TODO make this able to contain more then just action rows (will probably do when discord allows more then just action row)

    var persistent = false
        private set

    fun addRow(actionRow: CascadeActionRow) {
        doChecks(actionRow)
        components.add(actionRow)
    }

    fun setRow(pos: Int, actionRow: CascadeActionRow) {
        doChecks(actionRow)
        components[pos] = actionRow
    }

    private fun doChecks(actionRow: CascadeActionRow) {
        if (components.size == 0) {
            persistent = actionRow.isPersistent()
        } else {
            if (persistent && !actionRow.isPersistent()) {
                throw UnsupportedOperationException("Cannot add non-persistent rows to persistent containers")
            }
        }

        if (components.size >= 5) {
            throw UnsupportedOperationException("Cannot have more then 5 action rows!")
        }
    }

    fun getComponents(): List<CascadeActionRow> {
        return components.toList()
    }

}