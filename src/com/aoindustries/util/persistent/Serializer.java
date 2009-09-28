/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
package com.aoindustries.util.persistent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Converts an object to and from <code>byte[]</code> representations.
 * <code>null</code> values will not be passed-in.
 * All <code>Serializers</code> should be considered not thread-safe.
 *
 * @author  AO Industries, Inc.
 */
public interface Serializer<E> {

    /**
     * <p>
     * Determines the size of the object after serialization.
     * This allows some optimizations avoiding unnecessary copying of data.
     * </p>
     * The common pattern is:
     * <ol>
     *   <li>Get size from <code>getSerializedSize</code></li>
     *   <li>Allocate appropriate space</li>
     *   <li>Write serialized object with <code>serialize</code></li>
     * </ol>
     * It may be best to remember the most recently used object between calls
     * to <code>getSerializedSize</code> and <code>serialize</code> when it can
     * reduce processing time.
     *
     * @return  the exact number of bytes the object will take to serialize
     */
    int getSerializedSize(E value) throws IOException;

    /**
     * Writes the object to the <code>OutputStream</code>.  <code>null</code> will
     * not be passed in.
     */
    void serialize(E value, OutputStream out) throws IOException;

    /**
     * Restores an object from an <code>InputStream</code>.
     */
    E deserialize(InputStream in) throws IOException;
}
