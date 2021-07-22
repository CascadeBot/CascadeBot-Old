package org.cascadebot.cascadebot.utils.diff;

/**
 * Represents a change from an old object to a new object.
 *
 * @param <T> The type of object.
 */
public class DifferenceChanged<T> implements Diff {

    private final T oldObj;
    private final T newObj;

    public DifferenceChanged(T oldObj, T newObj) {
        this.oldObj = oldObj;
        this.newObj = newObj;
    }

    /**
     * Get the old object
     *
     * @return the old object
     */
    public T getOldObj() {
        return oldObj;
    }

    /**
     * Get the new object
     *
     * @return the new object
     */
    public T getNewObj() {
        return newObj;
    }

}
