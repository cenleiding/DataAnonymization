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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.LongIterator;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.TrieIterator;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLStack.IntegerStack;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLStack.LongStack;

/**
 * This class implements a simple trie for integers that is materialized in a backing integer array
 * @author Fabian Prasser
 */
abstract class JHPLTrie {

    /** Constant */
    protected static final double COMPACTION_THRESHOLD = 0.2d;

    /** The buffer */
    protected final JHPLBuffer    buffer;
    /** The number of dimensions */
    protected final int           dimensions;
    /** The height of each dimension */
    protected final int[]         heights;
    /** The Lattice */
    protected final Lattice<?, ?> lattice;
    /** The number of levels */
    protected final int           levels;
    /** The number of used memory units */
    protected int                 used;
    /** Are we including level counters */
    protected boolean             pruning;
    /** Default bound for pruning */
    protected final int           bound;

    /**
     * Constructs a new trie
     * @param lattice
     * @param pruning
     */
    JHPLTrie(Lattice<?, ?> lattice, boolean pruning, int bound) {
        
        // Initialize. Root node will be at offset 0
        this.dimensions = lattice.nodes().getDimensions();
        this.heights = lattice.nodes().getHeights();
        this.buffer = new JHPLBuffer();
        this.buffer.allocate(heights[0] + (pruning ? 1 : 0));
        this.bound = bound;
        if (pruning) {
            this.buffer.memory[0] = (bound == Integer.MAX_VALUE) ? Integer.MAX_VALUE - 1 : Integer.MIN_VALUE + 1;
        }
        this.used = heights[0] + (pruning ? 1 : 0);
        this.lattice = lattice;
        this.pruning = pruning;
        int sum = 0;
        for (int i = 0; i < this.heights.length; i++) {
            sum += this.heights[i] - 1;
        }
        this.levels = sum + 1;
    }
    
    /**
     * Returns an iterator over all elements in the trie. Note: hasNext() is not implemented. Simply iterate until
     * <code>null</code> is returned.
     * @return
     */
    private TrieIterator _iterator() {
        
        // Initialize
        final int[] element = new int[this.dimensions];
        final IntegerStack offsets = new IntegerStack(this.dimensions);
        final IntegerStack pointers = new IntegerStack(this.dimensions);
        final int offset = pruning ? 1 : 0;
        offsets.push(0);
        pointers.push(0);
        element[0] = 0;
        
        // Return
        return new TrieIterator() {
            
            int level = 0;
                
            @Override
            public int level() {
                return level;
            }
            
            @Override
            public int[] next() {
                
                // Iteratively traverse the trie
                while (true) {
                    
                    // End of node
                    while (offsets.peek() == heights[offsets.size() - 1]) {
                        offsets.pop();
                        pointers.pop();
                        if (offsets.empty()) {
                            return null;
                        }
                    }
                    
                    // Check and increment
                    int mem = buffer.memory[pointers.peek() + offsets.peek() + offset];
                    offsets.inc();
                
                    // If available
                    if (mem != JHPLBuffer.FLAG_NOT_AVAILABLE) {
            
                        int dimension = offsets.size() - 1;
                        level -= element[dimension];
                        element[dimension] = offsets.peek() - 1;
                        level += element[dimension];
                        if (offsets.size() < dimensions) {
                            // Inner node
                            offsets.push(0);
                            pointers.push(mem);
                        } else {
                            // Leaf node
                            return element;
                        }
                    }
                }
            }
        };
    }

    /**
     * Helper for converting the trie to a string
     * @param prefix
     * @param isTail
     * @param offset
     * @param dimension
     * @return
     */
    private StringBuilder toString(String prefix, boolean isTail, int offset, int dimension) {
        StringBuilder builder = new StringBuilder();
        List<Integer> children = new ArrayList<Integer>();
        for (int i = offset; i<offset + heights[dimension]; i++) {
            if (buffer.memory[i + (pruning ? 1 : 0)] != JHPLBuffer.FLAG_NOT_AVAILABLE) {
                children.add(i);
            }
        }
        int level = buffer.memory[offset];
        for (int j = 0; j < children.size() - 1; j++) {
            int i = children.get(j);
            builder.append(prefix).append(isTail ? "+-- " : "+-- ").append("[").append(i - offset).append(pruning ? "] lvl {"+level+"}" : "]").append("\n");
            if (dimension != dimensions - 1) {
                builder.append(toString(prefix + (isTail ? "    " : "|   "), false, buffer.memory[i + (pruning ? 1 : 0)], dimension + 1));
            }
        }
        if (children.size() > 0) {
            int i = children.get(children.size() - 1);
            builder.append(prefix).append(isTail ? "+-- " : "+-- ").append("[").append(i - offset).append(pruning ? "] lvl {"+level+"}" : "]").append("\n");
            if (dimension != dimensions - 1) {
                builder.append(toString(prefix + (isTail ? "    " : "|   "), true, buffer.memory[i + (pruning ? 1 : 0)], dimension + 1));
            }
        }
        return builder;
    }
    
    /**
     * Clears all above/below this element
     * @param element
     */
    void clear(int[] element) {
        this.clear(element, 0, 0);
        
        // Compaction
        double utilization = (double)used / (double)buffer.memory.length;
        if (utilization < COMPACTION_THRESHOLD) {
            compactify();
        }
    }

    abstract boolean clear(int[] element, int dimension, int offset);
        
    /**
     * Compaction method on the trie
     */
    void compactify() {
        
        TrieIterator iterator = this._iterator();
        JHPLTrie other = newInstance();
        int[] element = iterator.next();
        int level = iterator.level();
        while (element != null) {
            other.put(element, level);
            element = iterator.next();
            level = iterator.level();
        }
        this.buffer.replace(other.buffer);
    }
    /**
     * Queries this trie for the given element
     * @param node
     * @return
     */
    boolean contains(int[] node) {
        return contains(node, bound, 0, 0);
    }
    
//    abstract void check(int[] element, int offset, int dimension);

    /**
     * Queries this trie for the given element
     * @param node
     * @param level
     * @return
     */
    boolean contains(int[] node, int level) {
        return contains(node, level, 0, 0);
    }

    /**
     * Queries this trie for the given element
     * 
     * @param element
     * @param level
     * @param dimension
     * @param offset
     */
    abstract boolean contains(int[] element, int level, int dimension, int offset);

    /**
     * Returns the memory consumption in bytes
     * @return
     */
    long getByteSize() {
        return this.buffer.memory.length * 4;
    }
    
    /**
     * Returns the number of levels
     * @return
     */
    int getLevels() {
        return this.levels;
    }

    /**
     * Returns an iterator over all elements in the trie. Note: hasNext() is not implemented. Simply iterate until
     * <code>null</code> is returned.
     * @return
     */
    Iterator<int[]> iterator() {
        
        // Initialize
        final int[] element = new int[this.dimensions];
        final IntegerStack offsets = new IntegerStack(this.dimensions);
        final IntegerStack pointers = new IntegerStack(this.dimensions);
        final int offset = pruning ? 1 : 0;
        offsets.push(0);
        pointers.push(0);
        element[0] = 0;
        
        // Return
        return new Iterator<int[]>() {
            
            @Override public boolean hasNext() { throw new UnsupportedOperationException(); }

            @Override
            public int[] next() {
                
                // Iteratively traverse the trie
                while (true) {
                    
                    // End of node
                    while (offsets.peek() == heights[offsets.size() - 1]) {
                        offsets.pop();
                        pointers.pop();
                        if (offsets.empty()) {
                            return null;
                        }
                    }
                    
                    // Check and increment
                    int mem = buffer.memory[pointers.peek() + offsets.peek() + offset];
                    offsets.inc();
                
                    // If available
                    if (mem != JHPLBuffer.FLAG_NOT_AVAILABLE) {
                        
                        element[offsets.size() - 1] = offsets.peek() - 1;
                        if (offsets.size() < dimensions) {
                            // Inner node
                            offsets.push(0);
                            pointers.push(mem);
                        } else {
                            // Leaf node
                            return element;
                        }
                    }
                }
            }
            @Override public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    /**
     * Returns an iterator over all elements on the given level stored in the trie. 
     * Note: hasNext() is not implemented. Simply iterate until <code>null</code> is returned.
     * @param level
     * @return
     */
    Iterator<int[]> iterator(final int level) {
        
        // Initialize
        final int[] element = new int[this.dimensions];
        final IntegerStack offsets = new IntegerStack(this.dimensions);
        final IntegerStack pointers = new IntegerStack(this.dimensions);
        final int[] mins = new int[this.dimensions];
        final int offset = pruning ? 1 : 0;
        offsets.push(0);
        pointers.push(0);
        element[0] = 0;
        
        // Determine minimal indices
        for (int i = 0; i < mins.length; i++) {
            int diff = levels - heights[i];
            mins[i] = level - diff;
            mins[i] = mins[i] < 0 ? 0 : mins[i];
        }
        
        // Return
        return new Iterator<int[]>() {
            
            /** Current level*/
            int current = 0;
            
            @Override public boolean hasNext() { throw new UnsupportedOperationException(); }

            @Override
            public int[] next() {
                
                // Iteratively traverse the trie
                while (true) {
                    
                    // (1) End of node, or  
                    // (2) already on a higher level as requested
                    while (offsets.peek() == heights[offsets.size() - 1]  || current > level) {
                        int idx = offsets.size() - 1;
                        current -= element[idx];
                        element[idx] = 0;
                        offsets.pop();
                        pointers.pop();
                        if (offsets.empty()) {
                            return null;
                        }
                    }
                    
                    // Check and increment
                    int mem = buffer.memory[pointers.peek() + offsets.peek() + offset];
                    offsets.inc();
                    
                    // Available
                    if (mem != JHPLBuffer.FLAG_NOT_AVAILABLE) {
                        int val = offsets.peek() - 1;
                        int idx = offsets.size() - 1;
                        current = current - element[idx] + val;
                        element[idx] = val;

                        // Inner node
                        if (offsets.size() < dimensions) {
                            
                            // Initialize with minimal level
                            int min = mins[offsets.size()];
                            offsets.push(min);
                            pointers.push(mem + min);
                            
                        // Leaf node on the requested level
                        } else if (current == level) {
                            return element; 
                        }
                    }
                }
            }
            @Override public void remove() { throw new UnsupportedOperationException(); }
        };
    }


    /**
     * Returns an iterator over all elements in the trie. Note: hasNext() is not implemented. Simply iterate until
     * <code>null</code> is returned.
     * @return
     */
    LongIterator iteratorLong(final long[] multiplier) {

        // Initialize
        final LongStack identifiers = new LongStack(this.dimensions);
        final IntegerStack offsets = new IntegerStack(this.dimensions);
        final IntegerStack pointers = new IntegerStack(this.dimensions);
        final int offset = pruning ? 1 : 0;
        offsets.push(0);
        pointers.push(0);
        identifiers.push(0L);
        
        // Return
        return new LongIterator() {
            
            @Override public boolean hasNext() { throw new UnsupportedOperationException(); }

            @Override
            public long next() {
                
                // Iteratively traverse the trie
                while (true) {
                    
                    // End of node
                    while (offsets.peek() == heights[offsets.size() - 1]) {
                        
                        offsets.pop();
                        pointers.pop();
                        identifiers.pop();
                        if (offsets.empty()) {
                            return -1;
                        }
                    }
                    
                    // Check and increment
                    int mem = buffer.memory[pointers.peek() + offsets.peek() + offset];
                    offsets.inc();
                
                    // If available
                    if (mem != JHPLBuffer.FLAG_NOT_AVAILABLE) {
                        
                        long element = identifiers.peek() + ((long) (offsets.peek() - 1) * multiplier[offsets.size() - 1]); 
                        
                        if (offsets.size() < dimensions) {
                            // Inner node
                            offsets.push(0);
                            pointers.push(mem);
                            identifiers.push(element);
                        } else {
                            // Leaf node
                            return element;
                        }
                    }
                }
            }
        };
    }

    abstract JHPLTrie newInstance();
    
    /**
     * Puts an element into this trie
     * @param element
     */
    void put(int[] element) {
        put(element, bound);
    }
    
    /**
     * Puts an element into this trie
     * @param element
     */
    abstract void put(int[] element, int level);

    /**
     * To string method
     * @param prefix
     * @return
     */
    String toString(String prefix1, String prefix2) {
        
        int allocated = buffer.memory.length * 4;
        int used = this.used * 4;
        double relative = (double)used / (double)allocated * 100d;
        DecimalFormat format = new DecimalFormat("##0.00000");
        
        StringBuilder builder = new StringBuilder();
        builder.append(prefix1).append("Trie\n");
        builder.append(prefix2).append("+-- Memory statistics\n");
        builder.append(prefix2).append("|   +-- Allocated: ").append(allocated).append(" [bytes]\n");
        builder.append(prefix2).append("|   +-- Used: ").append(used).append(" [bytes]\n");
        builder.append(prefix2).append("|   +-- Relative: ").append(format.format(relative)).append(" [%]\n");
        builder.append(prefix2).append("+-- Buffer\n");
        builder.append(prefix2).append("|   +-- ").append(Arrays.toString(buffer.memory)).append("\n");
        builder.append(prefix2).append("+-- Tree\n");
        builder.append(toString(prefix2 + "    ", false, 0, 0));
        builder.append(prefix2).append("    +-- [EOT]\n");
        return builder.toString();
    }
}