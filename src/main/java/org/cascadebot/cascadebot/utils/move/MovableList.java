package org.cascadebot.cascadebot.utils.move;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow;
import org.cascadebot.cascadebot.utils.interactions.CascadeButton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MovableList<T> {

    private int targetPos = 0;
    private int selectedPos = 0;

    private T selectedItem;

    private List<T> backingItems;
    private Function<T, String> displayFunction;
    private Consumer<MovedInfo<T>> movedConsumer;

    private CascadeButton selectButton;
    private CascadeButton confirmButton;
    private CascadeButton cancelButton;

    private List<CascadeActionRow> extraRows = new ArrayList<>();

    private Function<Long, Boolean> usageRestriction = aLong -> true;

    private MovableList(List<T> items, Function<T, String> displayFunction) {
        this.backingItems = items;
        this.displayFunction = displayFunction;
    }

    public void setDisplayFunction(Function<T, String> displayFunction) {
        this.displayFunction = displayFunction;
    }

    public static <T> MovableList<T> wrap(Collection<T> items) {
        return new MovableList<>(new ArrayList<>(items) /* I'm doing this to make sure the list is mutable */, (t -> {
            Method method;
            try {
                method = t.getClass().getDeclaredMethod("getName"); // Try getName method before anything else. Most of what we want to pass into this will probably have that method
            } catch (NoSuchMethodException e) {
                return "";
            }
            try {
                return (String) method.invoke(t);
            } catch (IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        }));
    }

    public void setSelectedItem(T selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void setUsageRestriction(Function<Long, Boolean> usageRestriction) {
        this.usageRestriction = usageRestriction;
    }

    public void addExtraRow(CascadeActionRow cascadeActionRow) {
        this.extraRows.add(cascadeActionRow);
    }

    public void updateBaseList(Collection<T> items) {
        this.backingItems = new ArrayList<>(items);
    }

    public List<T> getBaseList() {
        return this.backingItems;
    }

    public T getSelectedItem() {
        return selectedItem;
    }

    public void send(TextChannel textChannel) {
        CascadeActionRow moveRow = new CascadeActionRow();
        selectButton = CascadeButton.success("Select", Emoji.fromUnicode(UnicodeConstants.TICK), (runner, channel, message) -> {
            if (!usageRestriction.apply(runner.getIdLong())) {
                return;
            }

        });

        confirmButton = CascadeButton.success("Move", Emoji.fromUnicode(UnicodeConstants.TICK), (runner, channel, message) -> {
            if (!usageRestriction.apply(runner.getIdLong())) {
                return;
            }

        });

        cancelButton = CascadeButton.danger("Cancel", Emoji.fromUnicode(UnicodeConstants.RED_CROSS), (runner, channel, message) -> {
            if (!usageRestriction.apply(runner.getIdLong())) {
                return;
            }

        });
        moveRow.addComponent(CascadeButton.primary(Emoji.fromUnicode(UnicodeConstants.ARROW_UP), (runner, channel, message) -> {
            if (!usageRestriction.apply(runner.getIdLong())) {
                return;
            }

        }));
        moveRow.addComponent(CascadeButton.primary(Emoji.fromUnicode(UnicodeConstants.ARROW_DOWN), (runner, channel, message) -> {
            if (!usageRestriction.apply(runner.getIdLong())) {
                return;
            }
        }));
    }

    private String getMessage() {
        return "";
    }

}
