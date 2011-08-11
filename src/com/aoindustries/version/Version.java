/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.version;

import com.aoindustries.lang.NullArgumentException;

/**
 * A software version consisting of four integer components.
 *
 * @author  AO Industries, Inc.
 */
final public class Version {

    /**
     * Gets a version number instance from its component parts.
     */
    public static Version getInstance(
        int major,
        int minor,
        int release,
        int build
    ) {
        return new Version(major, minor, release, build);
    }

    /**
     * Parses a version number from its string representation.
     *
     * @see  #toString()
     */
    public static Version valueOf(String version) throws IllegalArgumentException {
        NullArgumentException.checkNotNull(version, "version");
        int dot1Pos = version.indexOf('.');
        if(dot1Pos==-1) throw new IllegalArgumentException(version);
        int dot2Pos = version.indexOf('.', dot1Pos+1);
        if(dot2Pos==-1) throw new IllegalArgumentException(version);
        int dot3Pos = version.indexOf('.', dot2Pos+1);
        if(dot3Pos==-1) throw new IllegalArgumentException(version);
        return getInstance(
            Integer.parseInt(version.substring(0, dot1Pos)),
            Integer.parseInt(version.substring(dot1Pos+1, dot2Pos)),
            Integer.parseInt(version.substring(dot2Pos+1, dot3Pos)),
            Integer.parseInt(version.substring(dot3Pos+1))
        );
    }

    private int major;
    private int minor;
    private int release;
    private int build;

    private Version(
        int major,
        int minor,
        int release,
        int build
    ) {
        this.major = major;
        this.minor = minor;
        this.release = release;
        this.build = build;
    }

    /**
     * The toString representation is <code><i>major</i>.<i>minor</i>.<i>release</i>.<i>build</i></code>
     *
     * @see #valueOf(String)
     */
    @Override
    public String toString() {
        return major+"."+minor+"."+release+"."+build;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Version)) return false;
        Version other = (Version)obj;
        return
            build==other.build // Build changes most - check first
            && release==other.release
            && minor==other.minor
            && major==other.major
        ;
    }

    @Override
    public int hashCode() {
        int hash = major;
        hash = hash*31 + minor;
        hash = hash*31 + release;
        hash = hash*31 + build;
        return hash;
    }

    /**
     * Gets the major version number.
     * 
     * A change in the first number means a radically new code base, complete
     * uninstall/reinstall may be necessary.
     */
    public int getMajor() {
        return major;
    }

    /**
     * Gets the minor version number.
     *
     * A change in the second number means significant features have been added,
     * but are generally compatible with previous versions.
     * Update strongly recommended.
     */
    public int getMinor() {
        return minor;
    }
    
    /**
     * Gets the release number.
     * 
     * A change in the third number means fixes or improvements to existing
     * features, but maintaining compatibility with current version.
     * Update recommended.
     */
    public int getRelease() {
        return release;
    }

    /**
     * Gets the build number.
     *
     * A change in the build number only indicates minor fixes or improvements
     * to existing features.
     * Update not required or even suggested.
     */
    public int getBuild() {
        return build;
    }
}
