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
package com.aoindustries.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the filesystem iterator.
 *
 * @author  AO Industries, Inc.
 */
public class CompressedIntTest extends TestCase {

    public CompressedIntTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CompressedIntTest.class);
        return suite;
    }

    public void testRandomInts() throws IOException {
        Random random = new SecureRandom();
        List<Integer> values = new ArrayList<Integer>();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            for(int c=0;c<10000;c++) {
                for(int power=1; power<=30; power++) {
                    int value = random.nextInt(1<<power)-(1<<(power-1));
                    values.add(value);
                    CompressedDataOutputStream.writeCompressedInt(value, bout);
                }
            }
        } finally {
            bout.close();
        }
        // Read back and make sure matches
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        try {
            for(int value : values) {
                assertEquals(value, CompressedDataInputStream.readCompressedInt(bin));
            }
        } finally {
            bin.close();
        }
    }
}
