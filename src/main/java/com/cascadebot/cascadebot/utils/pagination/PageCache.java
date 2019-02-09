/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils.pagination;

import java.util.HashMap;
import java.util.List;

public class PageCache extends HashMap<Long, PageCache.Pages> {

    public void put(List<Page> pages, long messageId) {
        this.put(messageId, new Pages(pages));
    }

    public class Pages {

        List<Page> pages;

        int currentPage;

        Pages(List<Page> pages) {
            this.pages = pages;
            currentPage = 1;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public Page getPage(int page) {
            return pages.get(page - 1);
        }

        public int getPages() {
            return pages.size();
        }

    }

}
