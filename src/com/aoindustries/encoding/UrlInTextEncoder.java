/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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
 * Writes a URL into TEXT and validates its input as a URL.  The URL is
 * not encoded using HttpServletRequest.encodeURL.
 *
 * @author  AO Industries, Inc.
 */
public class UrlInTextEncoder extends MediaEncoder {

    protected UrlInTextEncoder(Writer out) {
        super(out);
    }

    @Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.URL
            || inputType==MediaType.JAVASCRIPT  // No validation required
            || inputType==MediaType.TEXT        // No validation required
        ;
    }

    private boolean foundQuestionMark = false;

    @Override
    public MediaType getValidMediaOutputType() {
        return MediaType.TEXT;
    }

    @Override
    public void write(int c) throws IOException {
        foundQuestionMark = UrlValidator.checkCharacter(c, foundQuestionMark);
        out.write(c);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        foundQuestionMark = UrlValidator.checkCharacters(cbuf, off, len, foundQuestionMark);
        out.write(cbuf, off, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if(str==null) throw new IllegalArgumentException("str is null");
        foundQuestionMark = UrlValidator.checkCharacters(str, off, off + len, foundQuestionMark);
        out.write(str, off, len);
    }

    @Override
    public UrlInTextEncoder append(CharSequence csq) throws IOException {
        foundQuestionMark = UrlValidator.checkCharacters(csq, 0, csq.length(), foundQuestionMark);
        out.append(csq);
        return this;
    }

    @Override
    public UrlInTextEncoder append(CharSequence csq, int start, int end) throws IOException {
        foundQuestionMark = UrlValidator.checkCharacters(csq, start, end, foundQuestionMark);
        out.append(csq, start, end);
        return this;
    }

    @Override
    public UrlInTextEncoder append(char c) throws IOException {
        foundQuestionMark = UrlValidator.checkCharacter(c, foundQuestionMark);
        out.append(c);
        return this;
    }
}
