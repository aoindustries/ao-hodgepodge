/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013  AO Industries, Inc.
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

import static com.aoindustries.encoding.TextInXhtmlEncoder.encodeTextInXhtml;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * Encodes a URL into XHTML.  It uses HttpServletRequest.encodeURL to
 * rewrite the URL as needed.
 *
 * @author  AO Industries, Inc.
 */
public class UrlInXhtmlEncoder extends BufferedEncoder {

    private final HttpServletResponse response;

    public UrlInXhtmlEncoder(HttpServletResponse response) {
        super(128);
        this.response = response;
    }

    @Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.URL
            || inputType==MediaType.TEXT        // No validation required
        ;
    }

    @Override
    public MediaType getValidMediaOutputType() {
        return MediaType.XHTML;
    }

    @Override
    protected void writeSuffix(StringBuilder buffer, Appendable out) throws IOException {
        encodeTextInXhtml(
            response.encodeURL(
                NewEncodingUtils.encodeUrlPath(
                    buffer.toString()
                )
            ),
            out
        );
    }
}
