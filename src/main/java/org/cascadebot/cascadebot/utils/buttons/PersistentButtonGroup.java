package org.cascadebot.cascadebot.utils.buttons;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PersistentButtonGroup extends ButtonGroup {

    private List<PersistentButton> persistentButtons = new ArrayList<>();

    private PersistentButtonGroup() {
        super(-1, -1, -1);
    }

    public PersistentButtonGroup(long ownerId, long channelId, long guildId) {
        super(ownerId, channelId, guildId);
    }

    @Override
    public List<Button> getButtons() {
        return persistentButtons.stream().map(PersistentButton::getButton).collect(Collectors.toList());
    }

    @Override
    public void addButton(Button button) {
    }

    @Override
    public void removeButton(Button button) {
    }

    public void addPersistentButton(PersistentButton persistentButton) {
        persistentButtons.add(persistentButton);
        super.addButton(persistentButton.getButton());
    }

    public void removePersistentButton(PersistentButton persistentButton) {
        persistentButtons.remove(persistentButton);
        super.removeButton(persistentButton.getButton());
    }

}
