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
 * A simple long array list
 * 
 * @author Fabian Prasser
 */
class JHPLLongList {

    /** Constant */
    private static final double GROWTH_FACTOR    = 1.5d;
    /** Constant */
    private static final int    INITIAL_CAPACITY = 2;

    /** The size of the array nodes */
    private int                 size;
    /** The array with nodes */
    long[]                      memory;

    /**
     * Creates a new instance
     */
    JHPLLongList(){
        this.memory = new long[INITIAL_CAPACITY];
        this.size = 0;
    }
    
    /**
     * Returns an iterator
     * @return
     */
    public JHPLIterator.LongIterator iterator() {
        return new JHPLIterator.LongIterator() {
            
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public long next() {
                return memory[index++];
            }
        };
    }
    
    /**
     * Returns a reverse iterator
     * @return
     */
    public JHPLIterator.LongIterator reverseIterator() {
        return new JHPLIterator.LongIterator() {
            
            int index = size - 1;
            
            @Override
            public boolean hasNext() {
                return index >= 0;
            }
            
            @Override
            public long next() {
                return memory[index--];
            }
        };
    }
    
    /**
     * Adds the given element
     * @param element
     */
    void add(long element) {

        // Store
        size ++;

        // Check, if we need to allocate more memory
        if (size > memory.length) {
            
            // Compute new length
            int length = memory.length;
            while (length < size) {
                length = (int)((double)length * GROWTH_FACTOR);
            }
            
            // Grow
            long[] newBuffer = new long[length];
            System.arraycopy(memory, 0, newBuffer, 0, size - 1);
            memory = newBuffer;
        }
        
        // New memory is in range offset to offset + _size
        // No need to initialize, JVM will initialize with 0x0
        memory[size-1] = element;
    }
}
