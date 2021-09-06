package org.cascadebot.cascadebot.commandmeta

class CommandPath (var rootId: Long) {

    var path: List<String> = listOf()
        private set

    constructor(rootId: Long, path: List<String>) : this(rootId) {
        this.path = path;
    }

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is CommandPath -> {
                var matches = this.rootId == other.rootId
                if (this.path.size != other.path.size) {
                    matches = false
                }
                if (!matches) {
                    return false
                }
                if (this.path.indices.any { this.path[it] != other.path[it] }) {
                    matches = false
                }
                matches
            }
            else -> {
                false;
            }
        }
    }

    override fun hashCode(): Int {
        var result = rootId.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }

}