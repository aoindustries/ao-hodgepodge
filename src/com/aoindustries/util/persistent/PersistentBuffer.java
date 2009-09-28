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

/**
 * <p>
 * A persistent buffer retains its data between uses.  They should not be used by
 * multiple virtual machines or even multiple instances objects within the same
 * virtual machine.  They are meant for persistence only, not interprocess
 * communication.
 * </p>
 * <p>
 * The ensure the data integrity of higher-level data structures, the write order
 * should be maintained and all data modified since the last <code>sync</code> should
 * be flushed and synced when <code>sync</code> is called.  In the event <code>sync</code>
 * is never called, the information should still be persisted in order and in a timely
 * manner.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public interface PersistentBuffer {

    /**
     * Checks if this buffer is closed.
     */
    boolean isClosed();

    /**
     * Closes this buffer.
     */
    void close() throws IOException;

    /**
     * Gets the read-only flag for this buffer.
     */
    boolean isReadOnly();

    /**
     * Gets the length of this buffer.
     */
    long length() throws IOException;

    /**
     * Sets the length of this buffer.  If the buffer is increased in size, the
     * new space will be zero-filled.
     */
    void setLength(long newLength) throws IOException;

    /**
     * Reads to the provided <code>byte[]</code>, starting at the provided
     * position and for the designated number of bytes.
     *
     * @exception  EOFException on end of file
     * @exception  IOException
     */
    void readFully(long position, byte[] buff, int off, int len) throws IOException;

    /**
     * Reads to the provided <code>byte[]</code>, may read fewer than <code>len</code>
     * bytes, but will always reads at least one byte.  Blocks if no data is
     * available.
     *
     * @exception  EOFException on end of file
     * @exception  IOException
     */
    int read(long position, byte[] buff, int off, int len) throws IOException;

    /**
     * Reads a boolean at the provided position, zero is considered <code>false</code>
     * and any non-zero value is <code>true</code>.
     */
    boolean readBoolean(long position) throws IOException;

    /**
     * Reads a byte at the provided position.
     */
    byte readByte(long position) throws IOException;

    /**
     * Reads an integer at the provided position.
     */
    int readInt(long position) throws IOException;

    /**
     * Reads a long at the provided position.
     */
    long readLong(long position) throws IOException;

    /**
     * Writes the bytes to the provided position.  The buffer will not be expanded
     * automatically.
     *
     * @exception  EOFException on end of file
     */
    void write(long position, byte[] buff, int off, int len) throws IOException;

    /**
     * Writes an integer at the provided position.  The buffer will not be expanded
     * automatically.
     *
     * @exception  EOFException on end of file
     */
    void writeInt(long position, int value) throws IOException;

    /**
     * Writes a long at the provided position.  The buffer will not be expanded
     * automatically.
     *
     * @exception  EOFException on end of file
     */
    void writeLong(long position, long value) throws IOException;

    /**
     * Syncs any unwritten data to the underlying storage.
     */
    void sync() throws IOException;
}
