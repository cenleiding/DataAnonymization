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

/**
 * A simple buffer based on an int array
 * 
 * @author Fabian Prasser
 */
class JHPLBuffer {

    /** Constant*/
    private static final double GROWTH_FACTOR      = 1.5d;
    /** Constant*/
    private static final int    INITIAL_CAPACITY   = 2;

    /** Flags */
    static final int            FLAG_AVAILABLE     = -0x1;
    /** Flags */
    static final int            FLAG_NOT_AVAILABLE = 0x0;

    /** The size of the array nodes */
    private int                 size;
    /** The array with nodes */
    int[]                       memory;

    /**
     * Creates a new instance
     */
    JHPLBuffer(){
        this.memory = new int[INITIAL_CAPACITY];
        this.size = 0;
    }
    
    /**
     * Allocates a chunk of the given size
     * @param _size
     * @return
     */
    int allocate(int _size) {

        // Store
        int offset = size;
        size += _size;

        // Check, if we need to allocate more memory
        if (size > memory.length) {
            
            // Compute new length
            int length = memory.length;
            while (length < size) {
                length = (int)((double)length * GROWTH_FACTOR);
            }
            
            // Grow
            int[] newBuffer = new int[length];
            System.arraycopy(memory, 0, newBuffer, 0, offset);
            memory = newBuffer;
        }
        
        // New memory is in range offset to offset + _size
        // No need to initialize, JVM will initialize with 0x0, which == FLAG_NOT_AVAILABLE
        
        // Return
        return offset;
    }
    
    /**
     * Ensures the given capacity
     * @param size
     */
    void ensure(int size) {
        if (memory.length < size) {
            allocate(size - this.size);
        }
    }
    
    /**
     * Replaces this buffer with the given buffer
     * @param other
     */
    void replace(JHPLBuffer other) {
        this.memory = other.memory;
        this.size = other.size;
    }
}
