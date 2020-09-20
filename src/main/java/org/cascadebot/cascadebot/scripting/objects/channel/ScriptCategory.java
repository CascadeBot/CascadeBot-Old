package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.Category;
import org.cascadebot.cascadebot.scripting.objects.ScriptContext;

public class ScriptCategory extends ScriptChannel {

    private Category internalCategory;

    public ScriptCategory(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public void setInternalCategory(Category category) {
        internalCategory = category;
        internalChannel = category;
        internalSnowflake = category;
    }

}
