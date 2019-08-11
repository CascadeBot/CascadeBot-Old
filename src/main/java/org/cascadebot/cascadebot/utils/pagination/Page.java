/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.pagination;

import net.dv8tion.jda.api.entities.Message;

public interface Page {

    /**
     * Method that's called when the page is shown
     *
     * @param message The message to edit with the page
     */
    void pageShow(Message message, int page, int total);

    //TODO expand this to allow more with more page options

}
