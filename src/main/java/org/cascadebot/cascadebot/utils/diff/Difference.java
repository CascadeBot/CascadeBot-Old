package org.cascadebot.cascadebot.utils.diff;

import java.util.HashMap;
import java.util.Map;

public class Difference {

    Map<String, Object> added = new HashMap<>();
    Map<String, Object> removed = new HashMap<>();

    Map<String, Diff> changed = new HashMap<>();

    public Map<String, Object> getAdded() {
        return added;
    }

    public Map<String, Object> getRemoved() {
        return removed;
    }

    public Map<String, Diff> getChanged() {
        return changed;
    }

}
