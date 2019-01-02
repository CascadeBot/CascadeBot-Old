/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.objects;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version implements Comparable<Version> {

    // https://regex101.com/r/26OhDd/1
    private static final Pattern VERSION_REGEX = Pattern.compile("^([0-9]+)(?:(?:\\.)([0-9]+))?(?:(?:\\.)([0-9]+))?$");

    private int major;
    private int minor;
    private int patch;

    private Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @BsonCreator
    public static Version of(@BsonProperty("major") int major,
                             @BsonProperty("minor") int minor,
                             @BsonProperty("patch") int patch) {
        return new Version(Math.abs(major), Math.abs(minor), Math.abs(patch));
    }

    public static Version of(int major, int minor) {
        return new Version(Math.abs(major), Math.abs(minor), 0);
    }

    public static Version of(int major) {
        return new Version(Math.abs(major), 0, 0);
    }

    public static Version parseVer(String verToParse) {
        Matcher matcher = VERSION_REGEX.matcher(verToParse);
        if (matcher.matches()) {
            int major = matcher.group(1) == null ? 0 : Integer.parseInt(matcher.group(1));
            int minor = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
            int patch = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));
            return new Version(major, minor, patch);
        } else {
            throw new IllegalArgumentException("Version is in the wrong format! Expected: 1.2.3 Actual: " + verToParse);
        }
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof com.cascadebot.shared.Version) {
            Version that = (Version) obj;
            return (this.major == that.major) &&
                    (this.minor == that.minor) &&
                    (this.patch == that.patch);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }


    @Override
    public int compareTo(Version that) {
        if (this.major > that.major) return 1;
        if (this.major < that.major) return -1;

        if (this.minor > that.minor) return 1;
        if (this.minor < that.minor) return -1;

        if (this.patch > that.patch) return 1;
        if (this.patch < that.patch) return -1;

        return 0;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

}
