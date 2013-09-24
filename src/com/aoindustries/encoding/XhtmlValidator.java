/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2012  AO Industries, Inc.
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
package com.aoindustries.encoding;

import java.io.IOException;
import java.io.Writer;

/**
 * Makes sure that all data going through this writer has the correct characters
 * for XHTML.
 *
 * {@link http://www.w3.org/TR/REC-xml/#charsets}
 *
 * @author  AO Industries, Inc.
 */
public class XhtmlValidator extends MediaValidator {

    /**
     * Checks one character, throws IOException if invalid.
     *
     * {@link http://www.w3.org/TR/REC-xml/#charsets}
     */
    public static void checkCharacter(char c) throws IOException {
        if(
            c!=0x9
            && c!=0xA
            && c!=0xD
            && (c<0x20 || c>0xD7FF)
            && (c<0xE000 || c>0xFFFD)
            && (c<0x10000 || c>0x10FFFF)
        ) throw new IOException(ApplicationResources.accessor.getMessage("XhtmlMediaValidator.invalidCharacter", Integer.toHexString(c)));
    }

    /**
     * Checks a set of characters, throws IOException if invalid
     */
    public static void checkCharacters(char[] cbuf, int off, int len) throws IOException {
        int end = off + len;
        while(off<end) checkCharacter(cbuf[off++]);
    }

    /**
     * Checks a set of characters, throws IOException if invalid
     */
    public static void checkCharacters(CharSequence str, int off, int end) throws IOException {
        while(off<end) checkCharacter(str.charAt(off++));
    }

    protected XhtmlValidator(Writer out) {
        super(out);
    }

    @Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.XHTML
            || inputType==MediaType.TEXT        // No validation required
        ;
    }

    @Override
    public MediaType getValidMediaOutputType() {
        return MediaType.XHTML;
    }

    @Override
    public void write(int c) throws IOException {
        checkCharacter((char)c);
        out.write(c);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        checkCharacters(cbuf, off, len);
        out.write(cbuf, off, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if(str==null) throw new IllegalArgumentException("str is null");
        checkCharacters(str, off, off + len);
        out.write(str, off, len);
    }

    @Override
    public XhtmlValidator append(CharSequence csq) throws IOException {
        checkCharacters(csq, 0, csq.length());
        out.append(csq);
        return this;
    }

    @Override
    public XhtmlValidator append(CharSequence csq, int start, int end) throws IOException {
        checkCharacters(csq, start, end);
        out.append(csq, start, end);
        return this;
    }

    @Override
    public XhtmlValidator append(char c) throws IOException {
        checkCharacter(c);
        out.append(c);
        return this;
    }
}
