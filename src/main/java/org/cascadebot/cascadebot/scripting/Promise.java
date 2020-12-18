package org.cascadebot.cascadebot.scripting;

import org.graalvm.polyglot.Value;

public interface Promise {

    void then(Value success, Value error);

}
