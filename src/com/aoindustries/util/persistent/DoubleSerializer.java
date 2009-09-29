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
 * Serializes <code>Double</code> objects.
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class DoubleSerializer implements Serializer<Double> {

    public boolean isFixedSerializedSize() {
        return true;
    }

    public long getSerializedSize(Double value) {
        return 8;
    }

    private final byte[] buffer = new byte[8];

    public void serialize(Double value, OutputStream out) throws IOException {
        PersistentCollections.longToBuffer(Double.doubleToRawLongBits(value), buffer, 0);
        out.write(buffer, 0, 8);
    }

    public Double deserialize(InputStream in) throws IOException {
        PersistentCollections.readFully(in, buffer, 0, 8);
        return Double.longBitsToDouble(PersistentCollections.bufferToLong(buffer, 0));
    }
}
