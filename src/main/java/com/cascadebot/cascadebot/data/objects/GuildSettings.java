/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.objects;

import com.cascadebot.cascadebot.data.Config;

public class GuildSettings {

    //region Boolean flags
    private boolean mentionPrefix = false; // Whether the bot will respond to a mention as a prefix
    private boolean deleteCommand = true;
    private boolean useEmbedForMessages = true;
    private boolean showPermErrors = true; // Whether commands will silently fail on no permissions
    private boolean showModuleErrors = false;
    //endregion

    public boolean isMentionPrefix() {
        return mentionPrefix;
    }

    public void setMentionPrefix(boolean mentionPrefix) {
        this.mentionPrefix = mentionPrefix;
    }

    public boolean willDeleteCommand() {
        return deleteCommand;
    }

    public void setDeleteCommand(boolean deleteCommand) {
        this.deleteCommand = deleteCommand;
    }

    public boolean useEmbedForMessages() {
        return useEmbedForMessages;
    }

    public void setUseEmbedForMessages(boolean useEmbedForMessages) {
        this.useEmbedForMessages = useEmbedForMessages;
    }

    public boolean willShowPermErrors() {
        return showPermErrors;
    }

    public void setShowPermErrors(boolean showPermErrors) {
        this.showPermErrors = showPermErrors;
    }

    public boolean willDisplayModuleErrors() {
        return showModuleErrors;
    }

    public void setShowModuleErrors(boolean showModuleErrors) {
        this.showModuleErrors = showModuleErrors;
    }

}
