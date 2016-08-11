/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2015, 2016  AO Industries, Inc.
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

import java.io.IOException;
import java.io.Writer;

/**
 * Encodes data as it is written to the provided output.
 *
 * @author  AO Industries, Inc.
 */
public interface Encoder {

	/**
	 * This is called before any data is written.
	 */
	void writePrefixTo(Appendable out) throws IOException;

	void write(int c, Writer out) throws IOException;

	void write(char cbuf[], Writer out) throws IOException;

	void write(char cbuf[], int off, int len, Writer out) throws IOException;

	void write(String str, Writer out) throws IOException;

	void write(String str, int off, int len, Writer out) throws IOException;

	Encoder append(char c, Appendable out) throws IOException;

	Encoder append(CharSequence csq, Appendable out) throws IOException;

	Encoder append(CharSequence csq, int start, int end, Appendable out) throws IOException;

	/**
	 * This is called when no more data will be written.
	 * This should also flush any internal buffers to <code>out</code>.  It
	 * should not, however, call flush on <code>out</code> itself.  This is
	 * to not interfere with any output buffering of <code>out</code>.
	 */
	void writeSuffixTo(Appendable out) throws IOException;
}
