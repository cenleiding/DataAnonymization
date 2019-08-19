/* ******************************************************************************
 * Copyright (c) 2015 Fabian Prasser.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Fabian Prasser - initial API and implementation
 * ****************************************************************************
 */
package com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a builder for nodes in the source space.
 * 
 * @author Fabian Prasser
 *
 * @param <T>
 */
public class JHPLBuilder<T> {

    /** Vars for the builder pattern*/
    private int[]                   builderArray;
    /** Vars for the builder pattern*/
    private int                     builderIndex;
    /** The number of dimensions*/
    private final int               dimensions;
    /** The height of each dimension*/
    private final int[]             heights;
    /** Map from T to index per dimension*/
    private final Map<T, Integer>[] indices;

    /**
     * Creates a new instance
     * @param elements
     */
    @SuppressWarnings("unchecked")
    JHPLBuilder(int[] array, T[]... elements) {
        this.dimensions = elements.length;
        this.indices = new HashMap[elements.length];
        this.heights = new int[this.dimensions];
        for (int j = 0; j < elements.length; j++) {
            T[] dimension = elements[j];
            heights[j] = elements[j].length;
            this.indices[j] = new HashMap<T, Integer>(dimension.length);
            for (int i = 0; i < dimension.length; i++) {
                this.indices[j].put(dimension[i], i);
            }
        }
        if (array == null) { throw new NullPointerException("Array must not be null"); }
        if (array.length != dimensions) { throw new IllegalArgumentException("Array must have " + dimensions + " slots"); }
        this.builderArray = array;
    }
    /**
     * Creates a new instance
     * @param elements
     */
    @SafeVarargs
    JHPLBuilder(T[]... elements) {
        this(new int[elements.length], elements);
    }

    /**
     * Ends creation via the builder pattern.
     * @return
     */
    public int[] create() {
        if (builderIndex != dimensions) { throw new IllegalStateException("No node constructed"); }
        int[] result = builderArray;
        builderArray = null;
        builderIndex = 0;
        return result;
    }
    
    /**
     * Add a new dimension to the current construction using the builder pattern.
     * @param element
     * @return
     */
    public JHPLBuilder<T> next(T element) {
        if (element == null) { throw new NullPointerException("Elements must not contain null"); }
        if (builderIndex >= dimensions) { throw new IllegalStateException("Index out of bounds"); }
        Integer index = indices[builderIndex].get(element);
        if (index == null || index < 0 || index >= heights[builderIndex]) { throw new IllegalArgumentException("Unknown element: " + element); }
        builderArray[builderIndex] = index;
        builderIndex++;
        return this;
    }
}
