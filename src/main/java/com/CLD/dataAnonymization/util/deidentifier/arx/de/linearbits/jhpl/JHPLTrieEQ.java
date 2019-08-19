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
 * This class implements a simple trie for integers that is materialized in a backing integer array
 * @author Fabian Prasser
 */
class JHPLTrieEQ extends JHPLTrie{

    /**
     * Constructs a new trie
     * @param lattice
     */
    JHPLTrieEQ(Lattice<?, ?> lattice) {
        super(lattice, false, 0);
    }

    @Override
    boolean clear(int[] element, int dimension, int offset) {

        // Init
        int elementOffset = offset + element[dimension];

        // Terminate
        if (dimension == dimensions - 1) {
            buffer.memory[elementOffset] = JHPLBuffer.FLAG_NOT_AVAILABLE;
            // Recursion
        } else {
            int pointer = buffer.memory[elementOffset];
            if (pointer != JHPLBuffer.FLAG_NOT_AVAILABLE) {
                if (!clear(element, dimension + 1, pointer)) {
                    buffer.memory[elementOffset] = JHPLBuffer.FLAG_NOT_AVAILABLE;
                    used -= heights[dimension + 1];
                }
            }
        }

        // Return
        for (int i = offset; i < offset + heights[dimension]; i++) {
            if (buffer.memory[i] != JHPLBuffer.FLAG_NOT_AVAILABLE) { return true; }
        }
        return false;
    }

    @Override
    boolean contains(int[] element, int level, int dimension, int offset) {
        
        // Init
        offset = 0;
        
        // Foreach
        for (dimension = 0; dimension < element.length; dimension++) {
    
            // Increment
            offset += element[dimension];
    
            // Find
            int pointer = buffer.memory[offset];
    
            // Terminate
            if (pointer == JHPLBuffer.FLAG_NOT_AVAILABLE) {
                return false;
                
            // Next
            } else {
                offset = pointer;
            }
        }
        
        // Terminate
        return true;
    }

    @Override
    JHPLTrie newInstance() {
        return new JHPLTrieEQ(this.lattice);
    }

    @Override
    void put(int[] element, int level) {
        
        int offset = 0;
        for (int dimension = 0; dimension < dimensions - 1; dimension++) {
            offset += element[dimension];
            if (buffer.memory[offset] == JHPLBuffer.FLAG_NOT_AVAILABLE){
                int space = heights[dimension + 1];
                int pointer = buffer.allocate(space);
                used += space;
                buffer.memory[offset] = pointer;
            }    
            offset = buffer.memory[offset];
        }
        offset += element[dimensions - 1];
        buffer.memory[offset] = JHPLBuffer.FLAG_AVAILABLE;
    }
}