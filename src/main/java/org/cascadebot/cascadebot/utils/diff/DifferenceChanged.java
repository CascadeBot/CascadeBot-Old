package org.cascadebot.cascadebot.utils.diff;

public class DifferenceChanged<T> implements Diff {

    private final T oldObj;
    private final T newObj;

    public DifferenceChanged(T oldObj, T newObj) {
        this.oldObj = oldObj;
        this.newObj = newObj;
    }

    public T getOldObj() {
        return oldObj;
    }

    public T getNewObj() {
        return newObj;
    }

}
