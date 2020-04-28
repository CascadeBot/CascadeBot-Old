package org.cascadebot.cascadebot.utils.buttons

import java.util.ArrayList
import java.util.stream.Collectors

open class PersistentButtonGroup : ButtonGroup {
    private val persistentButtons: MutableList<PersistentButton> = ArrayList()

    private constructor() : super(-1, -1, -1) {}
    constructor(ownerId: Long, channelId: Long, guildId: Long) : super(ownerId, channelId, guildId) {}

    val buttons: List<Button>
        get() = persistentButtons.stream().map { it.button }.collect(Collectors.toList())

    override fun addButton(button: Button) {
        throw UnsupportedOperationException("Cannot add normal buttons to a persistent group!")
    }

    override fun removeButton(button: Button) {
        throw UnsupportedOperationException("Cannot remove normal buttons from a persistent group!")
    }

    fun addPersistentButton(persistentButton: PersistentButton) {
        persistentButtons.add(persistentButton)
        super.addButton(persistentButton.button)
    }

    fun removePersistentButton(persistentButton: PersistentButton) {
        persistentButtons.remove(persistentButton)
        super.removeButton(persistentButton.button)
    }
}
