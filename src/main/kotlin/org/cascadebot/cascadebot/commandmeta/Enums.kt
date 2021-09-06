package org.cascadebot.cascadebot.commandmeta

enum class ParentCommand(
    val command: String,
    val module: Module,
    val permission: String,
    val description: String
) {

}

enum class SubCommandGroup(
    val groupName: String,
    val permission: String,
    val description: String
) {

}