package com.aoindustries.io;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;

/**
 * Provides direct access to the internal <code>byte[]</code>
 *
 * @author  AO Industries, Inc.
 */
public class BetterByteArrayInputStream extends ByteArrayInputStream {

    public BetterByteArrayInputStream(byte[] buf) {
        super(buf);
    }

    public byte[] getInternalByteArray() {
        return this.buf;
    }

    public void readFrom(RandomAccessFile raf) throws IOException {
        synchronized(this) {
            raf.readFully(buf);
            mark=0;
            pos=0;
            count=buf.length;
        }
    }
}
