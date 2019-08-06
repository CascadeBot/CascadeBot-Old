/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import net.dv8tion.jda.internal.utils.Checks;

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
        Checks.notNull(headings, "headings");
        Checks.notNull(body, "body");
        this.headings = Collections.unmodifiableList(headings);
        this.body = Collections.unmodifiableList(body);
        this.footer = footer;
    }

    /**
     * Creates a new table with the provided column headings and body with no footer.
     *
     * @param headings The column headings for this table.
     * @param body     A list of rows for the table.
     * @return An immutable Table with the respective column headings and body.
     */
    public Table of(List<String> headings, List<List<String>> body) {
        return new Table(headings, body, null);
    }

    /**
     * Creates a new table with the provided column headings and body with a supplied footer.
     *
     * @param headings The column headings for this table.
     * @param body     A list of rows for the table.
     * @param footer   The footer to display.
     * @return An immutable Table with the respective column headings, body and footer.
     */
    public Table of(List<String> headings, List<List<String>> body, String footer) {
        return new Table(headings, body, footer);
    }

    /**
     * Returns an immutable list containing the column headings.
     *
     * @return The non-null immutable list containing the column headings.
     */
    public List<String> getHeadings() {
        return headings;
    }

    /**
     * Returns an immutable list containing the rows of the table.
     * Each row is a list of strings containing a row element.
     *
     * @return The immutable non-null list of rows.
     */
    public List<List<String>> getBody() {
        return body;
    }

    /**
     * Returns the footer used for the table.
     *
     * @return The footer for this table. Can return {@code null} if there is no footer present.
     */
    public String getFooter() {
        return footer;
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

    public TableBuilder edit() {
        TableBuilder tableBuilder = new TableBuilder();
        tableBuilder.headings = new ArrayList<>(headings);
        tableBuilder.body = new ArrayList<>(body);
        tableBuilder.footer = footer;
        return tableBuilder;
    }

    public static class TableBuilder {

        private List<String> headings = new ArrayList<>();
        private List<List<String>> body = new ArrayList<>();
        private String footer = null;

        /**
         * Creates a completely empty table builder.
         */
        public TableBuilder() {}


        /**
         * Creates a empty table with the specified column headings.
         *
         * @param headings The column headings to initially add to the table.
         */
        public TableBuilder(String... headings) {
            this.headings.addAll(Arrays.asList(headings));
        }

        /**
         * Adds a column heading to the table. Can only be done before a body has been added to the table.
         *
         * @param heading The column heading to add to the table
         * @return TableBuilder for chaining.
         * @throws IllegalStateException If the body is not empty.
         */
        public TableBuilder addHeading(String heading) {
            if (!this.body.isEmpty()) throw new IllegalStateException("Cannot add headings with a non-empty body!");
            this.headings.add(heading);
            return this;
        }

        /**
         * Adds a row to the table. The number of elements in the row needs to match
         * the number of column headings.
         *
         * @param row The row elements to add to the table.
         * @return TableBuilder for chaining.
         * @throws IllegalArgumentException If the number of row elements does not equal the number of column headings.
         */
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

        public TableBuilder addRow(List<String> row) {
            if (row.size() != this.headings.size()) {
                throw new IllegalArgumentException(String.format(
                        "The number of row elements added was invalid! Expected: %d Actual: %d",
                        this.headings.size(),
                        row.size()
                ));
            }
            body.add(row);
            return this;
        }

        /**
         * Sets the footer for this table.
         *
         * @param footer The footer to set for the table.
         * @return TableBuilder for chaining.
         */
        public TableBuilder setFooter(String footer) {
            this.footer = footer;
            return this;
        }

        public List<String> getHeadings() {
            return headings;
        }

        public List<List<String>> getBody() {
            return body;
        }

        public String getFooter() {
            return footer;
        }

        /**
         * Builds an immutable Table object.
         *
         * @return The built immutable Table.
         */
        public Table build() {
            return new Table(headings, body, footer);
        }

    }

}
