package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.interactions.components.Component
import java.util.UUID

abstract class CascadeComponent(val uniqueId: String, persistent: Boolean) {

    val id = "${if (persistent) "persistent" else "cached"}-${UUID.randomUUID()}"
    var persistent: Boolean = persistent
        protected set

    abstract val discordComponent : Component
    abstract val componentType: Component.Type

}