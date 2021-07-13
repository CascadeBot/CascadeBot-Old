package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.interactions.components.Component

abstract class CascadeComponent(val id: String) {

    abstract val discordComponent : Component

}