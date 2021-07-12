package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.interactions.components.Component

interface CascadeComponent {

    fun getDiscordComponent() : Component

    fun getId() : String

}