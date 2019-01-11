/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.objects;

import com.cascadebot.cascadebot.Constants;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version implements Comparable<Version> {

    // https://regex101.com/r/26OhDd/2
    private static final Pattern VERSION_REGEX = Pattern.compile("^([0-9]+)(?:(?:\\.)([0-9]+))?(?:(?:\\.)([0-9]+))?(?:_(.+))?$");

    private int major;
    private int minor;
    private int patch;

    private String build;


    private Version(int major, int minor, int patch, String build) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.build = build == null ? null : build.toUpperCase();
    }

    @BsonCreator
    public static Version of(@BsonProperty("major") int major,
                             @BsonProperty("minor") int minor,
                             @BsonProperty("patch") int patch,
                             @BsonProperty("build") String build) {
        if (build != null) {
            if (!Constants.POSITIVE_INTEGER_REGEX.matcher(build).matches() || !build.equalsIgnoreCase("dev")) {
                throw new IllegalArgumentException("Build is in the wrong format! Must be a number or dev!");
            }
        }
        return new Version(Math.abs(major), Math.abs(minor), Math.abs(patch), build);
    }

    public static Version of(int major, int minor, int patch) {
        return new Version(Math.abs(major), Math.abs(minor), Math.abs(patch), null);
    }

    public static Version of(int major, int minor) {
        return new Version(Math.abs(major), Math.abs(minor), 0, null);
    }

    public static Version of(int major) {
        return new Version(Math.abs(major), 0, 0, null);
    }

    public static Version parseVer(String verToParse) {
        Matcher matcher = VERSION_REGEX.matcher(verToParse);
        if (matcher.matches()) {
            int major = matcher.group(1) == null ? 0 : Integer.parseInt(matcher.group(1));
            int minor = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
            int patch = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));
            return new Version(major, minor, patch, matcher.group(4));
        } else {
            throw new IllegalArgumentException("Version is in the wrong format! Expected: 1.2.3 Actual: " + verToParse);
        }
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch + (build == null ? "" : "_" + build);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version) {
            Version that = (Version) obj;
            return (this.major == that.major) &&
                    (this.minor == that.minor) &&
                    (this.patch == that.patch) &&
                    (Objects.equals(this.build, that.build));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, build);
    }


    @Override
    public int compareTo(Version that) {
        if (this.major > that.major) return 1;
        if (this.major < that.major) return -1;

        if (this.minor > that.minor) return 1;
        if (this.minor < that.minor) return -1;

        if (this.patch > that.patch) return 1;
        if (this.patch < that.patch) return -1;

        if (this.build == null || that.build == null) {
            return 0;
        } else if (this.build.equalsIgnoreCase("dev")) {
            return 1; // DEV is always latest
        } else if (Constants.POSITIVE_INTEGER_REGEX.matcher(this.build).matches() &&
                Constants.POSITIVE_INTEGER_REGEX.matcher(that.build).matches()) {
            return this.build.compareTo(that.build);
        }

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

    public String getBuild() {
        return build;
    }

}
