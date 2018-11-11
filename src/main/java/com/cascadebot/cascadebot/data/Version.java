/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Version implements Comparable<Version> {

    private int major;
    private int minor;
    private int patch;

    private Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static Version of(int major, int minor, int patch) {
        return new Version(major, minor, patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version) {
            Version that = (Version) obj;
            return (this.major == that.major) &&
                    (this.minor == that.minor) &&
                    (this.patch == that.minor);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }


    @Override
    public int compareTo(@NotNull Version that) {
        if (this.major > that.major) return 1;
        if (this.major < that.major) return -1;

        if (this.minor > that.minor) return 1;
        if (this.minor < that.minor) return -1;

        if (this.patch > that.patch) return 1;
        if (this.patch < that.patch) return -1;

        return 0;
    }

}
