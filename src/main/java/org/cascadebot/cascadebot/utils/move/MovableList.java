package org.cascadebot.cascadebot.utils.move;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;
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

    // Currently targeting pos
    private int targetPos = 0;
    // Currently selected item, is -1 is no item is selected
    private int selectedPos = -1;

    // The itesm that back this list
    private List<T> backingItems;
    // Function used to get whatever display is used for this item, usually is the name of the item
    private Function<T, String> displayFunction;
    // Consumer for when an item is moved so it can be updated in the database
    private Consumer<MovedInfo<T>> movedConsumer;

    private CascadeButton selectButton;
    private CascadeButton confirmButton;
    private CascadeButton cancelButton;

    private List<CascadeActionRow> extraRows = new ArrayList<>();

    private final int SHOW_MOVE_ELEMENTS = 10;

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
        int pos = backingItems.indexOf(selectedItem);
        if (pos == -1) {
            throw new IllegalArgumentException("That item isn't in the list");
        }
        this.selectedPos = pos;
    }

    public void setUsageRestriction(Function<Long, Boolean> usageRestriction) {
        this.usageRestriction = usageRestriction;
    }

    public void addExtraRow(CascadeActionRow cascadeActionRow) {
        this.extraRows.add(cascadeActionRow);
    }

    public void setMovedConsumer(Consumer<MovedInfo<T>> movedConsumer) {
        this.movedConsumer = movedConsumer;
    }

    public void updateBaseList(Collection<T> items) {
        List<T> listItems = new ArrayList<>(items);
        int moved = listItems.indexOf(backingItems.get(targetPos)) - targetPos;
        targetPos += moved;
        if (selectedPos != -1) {
            int selectedMoved = listItems.indexOf(backingItems.get(selectedPos)) - selectedPos;
            selectedPos += selectedMoved;
        }
        this.backingItems = new ArrayList<>(items);
    }

    public List<T> getBaseList() {
        return this.backingItems;
    }

    public void send(TextChannel textChannel) {
        CascadeActionRow moveRow = new CascadeActionRow();
        selectButton = CascadeButton.success("Select", Emoji.fromUnicode(UnicodeConstants.TICK), (runner, channel, message) -> {
            if (!usageRestriction.apply(runner.getIdLong())) {
                return;
            }

            selectedPos = targetPos;
            moveRow.setComponent(0, confirmButton);
            moveRow.addComponent(1, cancelButton);
            message.editMessage(getMessage()).queue();
        });

        confirmButton = CascadeButton.success("Move", Emoji.fromUnicode(UnicodeConstants.TICK), (runner, channel, message) -> {
            if (!usageRestriction.apply(runner.getIdLong())) {
                return;
            }

            MovedInfo<T> movedInfo = new MovedInfo<>(backingItems.get(selectedPos), targetPos);
            boolean positive = targetPos - selectedPos >= 0;
            T temp = backingItems.get(selectedPos);
            if (positive) {
                for (int i = selectedPos + 1; i <= targetPos; i++) {
                    // Move everything in this range down one
                    backingItems.set(i - 1, backingItems.get(i));
                }
            } else {
                for (int i = targetPos; i < selectedPos; i++) {
                    // Move everything in this range up one
                    backingItems.set(i + 1, backingItems.get(i));
                }
            }
            backingItems.set(targetPos, temp);
            selectedPos = -1;
            movedConsumer.accept(movedInfo);

            moveRow.setComponent(0, selectButton);
            moveRow.deleteComponent(1);

            message.editMessage(getMessage()).queue();
        });

        cancelButton = CascadeButton.danger("Cancel", Emoji.fromUnicode(UnicodeConstants.RED_CROSS), (runner, channel, message) -> {
            if (!usageRestriction.apply(runner.getIdLong())) {
                return;
            }
            selectedPos = -1;

            message.editMessage(getMessage()).queue();
        });
        moveRow.addComponent(CascadeButton.primary(Emoji.fromUnicode(UnicodeConstants.ARROW_UP), (runner, channel, message) -> {
            if (!usageRestriction.apply(runner.getIdLong())) {
                return;
            }

            targetPos++;
            if (targetPos > backingItems.size() - 1) {
                targetPos = backingItems.size() - 1;
            }

            message.editMessage(getMessage()).queue();
        }));
        moveRow.addComponent(CascadeButton.primary(Emoji.fromUnicode(UnicodeConstants.ARROW_DOWN), (runner, channel, message) -> {
            if (!usageRestriction.apply(runner.getIdLong())) {
                return;
            }

            targetPos--;
            if (targetPos < 0) {
                targetPos = 0;
            }

            message.editMessage(getMessage()).queue();
        }));
        Messaging.sendMessage(MessageType.INFO, textChannel, getMessage());
    }

    private String getMessage() {
        boolean isMoving = selectedPos != -1;
        /*
         * Show either (SHOW_MOVE_ELEMENTS / 2) elements or the number of elements before the target on top, whichever
         * is smallest.
         *
         * e.g. If target position is index 2, there will be 2 items on top. However, if target position is 10 then there
         * will be 5 elements on top.
         */
        int topItems = Math.min(targetPos, SHOW_MOVE_ELEMENTS / 2);
        // The bottom items is whatever is left over. This will be max of SHOW_MOVE_ELEMENTS and min of (SHOW_MOVE_ELEMENTS / 2)
        int bottomItems = SHOW_MOVE_ELEMENTS - topItems;

        // Show SHOW_MOVE_ELEMENTS elements either side of the target position
        int min = Math.max(0, targetPos - topItems);
        int max = Math.min(backingItems.size() - 1, targetPos + bottomItems);

        // Hollow diamond
        String miscChar = "\u25c7";
        // Filled diamond
        String currentPositionChar = "\u25c6";
        // Filled right arrow | hollow filled arrow
        String targetPositionChar = isMoving ? "\u2b95" : "\u21e8";

        StringBuilder stringBuilder = new StringBuilder();

        // If items on screen doesn't start with the first element, show a continuation indicator
        if (min > 0) {
            stringBuilder.append("...\n");
        }

        for (int i = min; i <= max; i++) {
            // All lines that are not current or target position are default a hollow diamond
            String lineChar = miscChar;
            // If the selected position and target position is the same, stack both icons
            if (i == selectedPos && i == targetPos) {
                lineChar = targetPositionChar + currentPositionChar;
            } else if (i == selectedPos) {
                lineChar = currentPositionChar;
            } else if (i == targetPos) {
                lineChar = targetPositionChar;
            }
            // Item character (◇|◆|➜|⇨) followed by space, group name and newline
            stringBuilder.append(lineChar).append(" ").append(displayFunction.apply(backingItems.get(i))).append('\n');
        }

        // If items on screen doesn't end with the last element, show a continuation indicator
        if (max < (backingItems.size() - 1)) {
            stringBuilder.append("...");
        }

        return stringBuilder.toString().trim();
    }

}
