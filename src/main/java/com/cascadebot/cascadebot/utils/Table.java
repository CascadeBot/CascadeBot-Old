/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Immutable class representing an Ascii Table.
 * Once built, this table is fully immutable and thread-safe.
 */
public final class Table {

    private final List<String> headings;
    private final List<List<String>> body;
    private final String footer;

    private Table(List<String> headings, List<List<String>> body, String footer) {
        this.headings = Collections.unmodifiableList(headings);
        this.body = Collections.unmodifiableList(body);
        this.footer = footer;
    }

    /**
     * Creates a new table with the provided headings and body with no footer.
     *
     * @param headings The headings for this table.
     * @param body A list of rows for the table.
     * @return An immutable Table with the respective headings and body.
     */
    public Table of(List<String> headings, List<List<String>> body) {
        return new Table(headings, body, null);
    }

    /**
     * Creates a new table with the provided headings and body with a supplied footer.
     *
     * @param headings The headings for this table.
     * @param body A list of rows for the table.
     * @param footer The footer to display.
     * @return An immutable Table with the respective headings, body and footer.
     */
    public Table of(List<String> headings, List<List<String>> body, String footer) {
        return new Table(headings, body, footer);
    }

    /**
     * Creates a string representations of the table using {@link FormatUtils#makeAsciiTable(List, List, String)}.
     *
     * @return A string representation of the table.
     */
    @Override
    public String toString() {
        return FormatUtils.makeAsciiTable(headings, body, footer);
    }

    public static class TableBuilder {

        private List<String> headings = new ArrayList<>();
        private List<List<String>> body = new ArrayList<>();
        private String footer = null;

        public TableBuilder(String... headings) {
            this.headings.addAll(Arrays.asList(headings));
        }

        public TableBuilder addHeading(String heading) {
            if (!this.body.isEmpty()) throw new IllegalStateException("Cannot add headings with a non-empty body!");
            this.headings.add(heading);
            return this;
        }

        public TableBuilder addRow(String... row) {
            if (row.length != this.headings.size()) {
                throw new IllegalArgumentException(String.format(
                        "The number of row elements added was invalid! Expected: %d Actual: %d",
                        this.headings.size(),
                        row.length
                ));
            }
            body.add(Arrays.asList(row));
            return this;
        }

        public TableBuilder setFooter(String footer) {
            this.footer = footer;
            return this;
        }

        public Table build() {
            return new Table(headings, body, footer);
        }

    }

}
