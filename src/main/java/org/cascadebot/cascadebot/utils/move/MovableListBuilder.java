package org.cascadebot.cascadebot.utils.move;

import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class MovableListBuilder<T> {

    private List<T> baseItems;
    private Function<T, String> displayFunction;
    private Function<Long, Boolean> usageRestriction;

    private List<CascadeActionRow> extraRows = new ArrayList<>();

    private MovableListBuilder(Collection<T> items) {
        this.baseItems = new ArrayList<>(items);
        this.displayFunction = Object::toString;
    }

    public MovableListBuilder<T> setCustomDisplayMethod(Function<T, String> displayFunction) {
        this.displayFunction = displayFunction;
        return this;
    }

    public MovableListBuilder<T> setUsageRestrictionFunction(Function<Long, Boolean> usageRestriction) {
        this.usageRestriction = usageRestriction;
        return this;
    }

    public void addExtraRow(CascadeActionRow actionRow) {
        extraRows.add(actionRow);
    }

    private MovableList<T> build(TextChannel channel) {
        return null;
    }

}
