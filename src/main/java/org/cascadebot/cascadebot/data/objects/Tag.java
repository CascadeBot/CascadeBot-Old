/*
  * Copyright (c) 2019 CascadeBot. All rights reserved.
  * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

public class Tag {

    private String content;
    private String category;

    public Tag(String content, String category) {
        this.content = content;
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}