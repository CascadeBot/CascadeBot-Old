package org.cascadebot.cascadebot.utils.buttons;

import org.cascadebot.cascadebot.UnicodeConstants;

public enum PersistentButton {
    TODO_Button_Check(new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel, message) -> {
        
    }));

    PersistentButton(Button button) {
    }
}
