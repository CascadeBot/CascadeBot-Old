package org.cascadebot.cascadebot.utils.move;

public class MovedInfo<T> {

    private T movedItem;
    private int movedTo;

    public MovedInfo(T movedItem, int movedTo) {
        this.movedItem = movedItem;
        this.movedTo = movedTo;
    }

    public T getMovedItem() {
        return movedItem;
    }

    public int getMovedTo() {
        return movedTo;
    }
}
