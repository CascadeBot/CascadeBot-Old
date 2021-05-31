package org.cascadebot.cascadebot.utils.diff;

import java.util.HashMap;
import java.util.Map;

public class Difference {

    protected Map<String, Object> added = new HashMap<>();
    protected Map<String, Object> removed = new HashMap<>();
    protected Map<String, Diff> changed = new HashMap<>();

    /**
     * Get all the objects that have been newly added.
     *
     * @return A map containing the new added objects with the key being the path to the object separated by dots and the value being the newly added object
     */
    public Map<String, Object> getAdded() {
        return added;
    }

    /**
     * Get all the objects that have been removed.
     *
     * @return A map containing the new added objects with the key being the path to the object separated by dots and the value being the removed object
     */
    public Map<String, Object> getRemoved() {
        return removed;
    }

    /**
     * Get all the objects that have been changed.
     *
     * @return A map containing the new added objects with the key being the path to the object separated by dots and the value being a {@link Diff} that represents the change
     */
    public Map<String, Diff> getChanged() {
        return changed;
    }

}
