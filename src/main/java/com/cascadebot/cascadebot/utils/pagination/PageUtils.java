/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils.pagination;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.utils.Checks;

import java.util.ArrayList;
import java.util.List;

public class PageUtils {

    /**
     * Splits a string to be used for pages.
     * Always splits to a length that is <= the length you provide.
     *
     * @param string The string to split
     * @param length The length you want the final pages. Always splits to a length that is <= the length you provide. Cannot be > 1800 as that's the discord limit (including page numbers)
     * @param c The character you want to split it at (this should be something like a space or a new line character)
     * @return A list of String pages
     */
    public static List<Page> splitStringToStringPages(String string, int length, char c) {
        List<String> strings = splitString(string, length, c);
        List<Page> pages = new ArrayList<>();
        for(String pageString : strings) {
            pages.add(new PageObjects.StringPage(pageString));
        }

        return pages;
    }

    /**
     * Splits a string to be used for pages.
     * Always splits to a length that is <= the length you provide.
     *
     * @param string The string to split
     * @param length The length you want the final pages. Always splits to a length that is <= the length you provide. Cannot be > 1800 as that's the discord limit (including page numbers)
     * @param c The character you want to split it at (this should be something like a space or a new line character)
     * @return A list of Embed pages
     */
    public static List<Page> splitStringToEmbedPages(String string, int length, char c) {
        List<String> strings = splitString(string, length, c);
        List<Page> pages = new ArrayList<>();
        for(String pageString : strings) {
            pages.add(new PageObjects.EmbedPage(new EmbedBuilder().setDescription(pageString)));
        }

        return pages;

    }

    /**
     * Splits a string to the given length at a given character.
     *
     * @param string The string to split
     * @param length The length you want the final pages. Always splits to a length that is <= the length you provide. Cannot be > 1800 as that's the discord limit (including page numbers)
     * @param c The character you want to split it at (this should be something like a space or a new line character)
     * @return A list of the split strings (you need to put into pages yourself)
     */
    public static List<String> splitString(String string, int length, char c) {
        Checks.notNegative(length, "length");
        Checks.check(length <= 1800, "length");
        Checks.notEmpty(string, "string");

        int amount = (int) ((double)string.length() / (double) length);

        List<String> strings = new ArrayList<>();
        String toAdd = "";

        for(int i = 0; i <= amount; i++) {
            int start = length * i;
            int end = Math.min(start + (length - 1), string.length());

            String temp = toAdd + string.substring(start, end == string.length() ? end : end - toAdd.length());
            int last;
            if(end != string.length()) {
                last = temp.lastIndexOf(c);

                toAdd = temp.substring(last + 1);
            } else {
                last = temp.length();
            }

            strings.add(temp.substring(0, last));
        }

        return strings;
    }

    /**
     * Splits table data out into a give number of rows.
     * I don't suggest using this as you can possibly exceed the discord message limit
     *
     * @param header The header data
     * @param body The body data
     * @param rows The amount of rows to split to
     * @return A list of pages
     */
    public static List<Page> splitTableDataToPages(List<String> header, List<List<String>> body, int rows) {
        int i = 0;
        List<Page> pages = new ArrayList<>();
        List<List<String>> pageContent = new ArrayList<>();
        for(List<String> row : body) {
            i++;
            pageContent.add(row);

            if(i == rows) {
                pages.add(new PageObjects.TablePage(header, pageContent));
                pageContent = new ArrayList<>();
                i = 0;
            }
        }
        pages.add(new PageObjects.TablePage(header, pageContent));
        return pages;
    }

    /**
     * Splits table data out into a pages with content length less then the length provides
     *
     * @param header The header data
     * @param body The body data
     * @param length The max content length you're trying to achieve
     * @return A list of pages
     */
    public static List<Page> splitTableDataToPagesWithCharLength(List<String> header, List<List<String>> body, int length) {
        int maxLength = 0;
        for(List<String> row : body) {
            StringBuilder stringBuilder = new StringBuilder("| ");
            for(String content : row) {
                stringBuilder.append(content).append(" | ");
            }
            stringBuilder.append(" |");
            String rowContent = stringBuilder.toString();
            if(rowContent.length() > maxLength) {
                maxLength = rowContent.length();
            }
        }

        int rows = (int) ((double) length / (double) maxLength);

        return splitTableDataToPages(header, body, rows);
    }

}
