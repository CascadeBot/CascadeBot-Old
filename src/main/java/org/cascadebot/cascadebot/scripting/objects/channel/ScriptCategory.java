package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.Category;

public class ScriptCategory extends ScriptChannel {

    public ScriptCategory() {
        super();
    }

    private Category internalCategory;

    public void setInternalCategory(Category category) {
        internalCategory = category;
        internalChannel = category;
        internalSnowflake = category;
    }

}
