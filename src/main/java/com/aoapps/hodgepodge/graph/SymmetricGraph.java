/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2016, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.graph;

import java.util.Set;

/**
 * A graph where each edge has an edge in the opposite direction.
 *
 * @param  <Ex>  An arbitrary exception type that may be thrown
 *
 * @author  AO Industries, Inc.
 */
// TODO: Ex extends Throwable
public interface SymmetricGraph<V, E extends Edge<V>, Ex extends Exception> extends Graph<V, E, Ex>, SymmetricMultiGraph<V, E, Ex> {

  /**
   * {@inheritDoc}
   *
   * A graph only allows unique combinations of from and to vertices.
   */
  @Override
  Set<E> getEdgesTo(V to) throws Ex;
}
