/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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
package com.aoindustries.graph;

import java.util.List;

/**
 * Thrown when a cycle has been detected in an acyclic graph.
 *
 * @author  AO Industries, Inc.
 */
public class GraphCycleException extends GraphException {

    private static String getMessage(List<? extends BackConnectedDirectedGraphVertex<?,?>> vertices) {
        StringBuilder SB = new StringBuilder();
        SB.append("Cycle exists:\n");
        for(BackConnectedDirectedGraphVertex<?,?> v : vertices) {
            SB.append("    ").append(v.getClass().getName()).append("(\"").append(v.toString()).append("\")\n");
        }
        return SB.toString();
    }

    private final List<? extends BackConnectedDirectedGraphVertex<?,?>> vertices;

    GraphCycleException(List<? extends BackConnectedDirectedGraphVertex<?,?>> vertices) {
        super(getMessage(vertices));
        this.vertices = vertices;
    }

    /**
     * Gets all vertices that are part of the cycle in the order they create the cycle.
     */
    public List<? extends BackConnectedDirectedGraphVertex<?,?>> getVertices() {
        return vertices;
    }
}
