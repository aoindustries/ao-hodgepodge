/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2016  AO Industries, Inc.
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
package com.aoindustries.util.graph;

import java.util.Collection;

/**
 * A multi graph where each edge has an edge in the opposite direction.
 *
 * @author  AO Industries, Inc.
 */
public interface SymmetricMultiGraph<V,E extends Edge<V>,EX extends Exception> extends MultiGraph<V,E,EX> {

	/**
	 * Gets the edges to the provided vertex.  The vertex must be part of this
	 * graph, and the results are undefined if it is not.
	 */
	Collection<E> getEdgesTo(V to) throws EX;
}
