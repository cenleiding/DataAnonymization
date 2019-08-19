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

import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.LongIterator;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.WrappedIntArrayIterator;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLStack.IntegerStack;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.PredictiveProperty.Direction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class implements an extension of the basic lattice without any restrictions on the number of nodes.
 * 
 * @author Fabian Prasser
 *
 * @param <T> The type of values in the dimensions of the lattice
 * @param <U> The type of associated data
 */
public class LatticeHighdimensional<T, U> extends Lattice<T, U> {

    /**
     * Int array wrapper
     * @author Fabian Prasser
     */
    static class IntArrayWrapper {
        
        /** Array*/
        final int[] array;
        /** Hashcode*/
        private final int hashcode;
        /** 
         * Creates a new instance
         * @param array
         */
        protected IntArrayWrapper(int[] array) {
            this.array = array;
            this.hashcode = Arrays.hashCode(array);
        }
        
        @Override
        public boolean equals(Object other) {
            return Arrays.equals(this.array, ((IntArrayWrapper)other).array);
        }
        
        @Override
        public int hashCode() {
            return hashcode;
        }
    }

    /** All materialized nodes */
    private final JHPLTrie                                               master;
    /** Nodes */
    private final JHPLNodes<T>                                           nodes;
    /** Tries for properties */
    private final Map<PredictiveProperty, JHPLTrie>                      propertiesDown;
    /** Tries for properties */
    private final Map<PredictiveProperty, JHPLTrie>                      propertiesUp;
    /** Tries for properties */
    private final Map<PredictiveProperty, Map<IntArrayWrapper, Boolean>> propertiesNone;
    /** Track modifications */
    private boolean                                                      modified = false;

    /**
     * Constructs a new lattice
     * 
     * @param elements One array of elements per dimension, ordered from the lowest to the highest element
     */
    @SuppressWarnings("unchecked")
    public LatticeHighdimensional(T[]... elements) {
        super();
        if (elements == null) {
            throw new NullPointerException("Elements must not be null");
        }
        if (elements.length == 0) {
            throw new IllegalArgumentException("Elements must not be of size zero");
        }
        for (int i=0; i<elements.length; i++) {
            if (elements[i] == null) {
                throw new NullPointerException("Elements must not be null");
            }
            if (elements[i].length == 0) {
                throw new IllegalArgumentException("Elements must not be of size zero");
            }
            for (T t : elements[i]) {
                if (t == null) {
                    throw new NullPointerException("Elements must not contain null");
                }
            }
        }
        
        this.nodes = new JHPLNodes<T>(this, elements);
        this.propertiesUp = new HashMap<PredictiveProperty, JHPLTrie>();
        this.propertiesDown = new HashMap<PredictiveProperty, JHPLTrie>();
        this.propertiesNone = new HashMap<PredictiveProperty, Map<IntArrayWrapper, Boolean>>();
        this.master = new JHPLTrieEQ(this);
    }
    
    @Override
    public boolean contains(int[] node) {
        return master.contains(node);
    }
    
    @Override
    public long getByteSize() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public U getData(int[] node) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean hasProperty(int[] node) {
        return hasProperty(node, nodes.getLevel(node));
    }
    
    @Override
    public boolean hasProperty(int[] node, int level) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean hasProperty(int[] node, int level, PredictiveProperty property) {
        
        switch (property.getDirection()) {
        case UP:
            JHPLTrie trie = this.propertiesUp.get(property);
            return trie == null ? false : trie.contains(node, level);
        case DOWN:
            trie = this.propertiesDown.get(property);
            return trie == null ? false : trie.contains(node, level);
        case BOTH:
            trie = this.propertiesUp.get(property);
            if (trie != null && trie.contains(node, level)) {
                return true;
            }
            trie = this.propertiesDown.get(property);
            if (trie != null && trie.contains(node, level)) {
                return true;
            } else {
                return false;
            }
        case NONE:
            
            Map<IntArrayWrapper, Boolean> map = this.propertiesNone.get(property);
            if (map == null) {
                return false;
            } else {
                Boolean result = map.get(new IntArrayWrapper(node));
                return result == null ? false : result;
            }
        default: 
            throw new IllegalArgumentException("Property with unknown direction");
        }
    }

    @Override
    public boolean hasProperty(int[] node, PredictiveProperty property) {
        return hasProperty(node, nodes.getLevel(node), property);
    }

    @Override
    public LongIterator listAllNodesAsIdentifiersImpl(final int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<int[]> listNodes() {
        return new WrappedIntArrayIterator(this, this.master.iterator());
    }

    @Override
    public Iterator<int[]> listNodes(int level) {
        return new WrappedIntArrayIterator(this, this.master.iterator(level));
    }

    @Override
    public LongIterator listNodesAsIdentifiers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JHPLNodes<T> nodes() {
        return nodes;
    }

    @Override
    public int numDimensions() {
        return this.nodes.getDimensions();
    }

    @Override
    public int numLevels() {
        return master.getLevels();
    }

    @Override
    public long numNodes(){
        throw new UnsupportedOperationException();
    }

    @Override
    public void putData(int[] node, U data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putProperty(int[] node, int level, PredictiveProperty property) {

        this.setModified();
        
        // Store in master trie
        this.master.put(node);
        
        // Reduce the amount of information stored in the trie
        if (hasProperty(node, level, property)) {
            return;
        }
        
        switch (property.getDirection()) {
        // Make sure that this method can be inlined by keeping
        // its size under 325 bytes
        case BOTH:
        case DOWN:
            JHPLTrie trie = this.propertiesDown.get(property);
            if (trie == null) {
                trie = new JHPLTrieGEQ(this);
                this.propertiesDown.put(property, trie);
            }
            trie.clear(node);
            trie.put(node, level);
            if (property.getDirection() == Direction.DOWN) {
                break;
            }
        case UP:
            trie = this.propertiesUp.get(property);
            if (trie == null) {
                trie = new JHPLTrieLEQ(this);
                this.propertiesUp.put(property, trie);
            }
            trie.clear(node);
            trie.put(node, level);
            break;
        case NONE:
            Map<IntArrayWrapper, Boolean> map = this.propertiesNone.get(property);
            if (map == null) {
                map = new HashMap<IntArrayWrapper, Boolean>();
                this.propertiesNone.put(property, map);
            }
            this.propertiesNone.get(property).put(new IntArrayWrapper(node), true);
            break;
        default:
            throw new IllegalArgumentException("Property with unknown direction");
        }
    }

    @Override
    public void putProperty(int[] node, PredictiveProperty property) {
        putProperty(node, nodes.getLevel(node), property);
    }

    @Override
    public JHPLSpace<T> space() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public JHPLUnsafe unsafe() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Enumerates all nodes regardless of whether or not they are stored in the lattice. Note: hasNext() is
     * not implemented. Simply iterate until <code>null</code> is returned.
     * @return
     */
    private Iterator<int[]> listAllNodesImpl() {

        // Initialize
        final int[] heights = this.nodes.getHeights();
        final int dimensions = this.nodes.getDimensions();
        final int[] element = new int[dimensions];
        final IntegerStack offsets = new IntegerStack(dimensions);
        offsets.push(0);
        element[0] = 0;
        
        // Return
        return new Iterator<int[]>() {
            
            @Override public boolean hasNext() { throw new UnsupportedOperationException(); }

            @Override
            public int[] next() {
                
                // Iterate
                while (true) {
                    
                    // End of node
                    while (offsets.peek() == heights[offsets.size() - 1]) {
                        offsets.pop();
                        if (offsets.empty()) {
                            return null;
                        }
                    }
                    
                    // Check and increment
                    offsets.inc();
                    
                    // Store
                    element[offsets.size() - 1] = offsets.peek() - 1;
                    
                    // Branch
                    if (offsets.size() < dimensions) {
                        offsets.push(0); // Inner node
                    } else {
                        return element; // Leaf node
                    }
                }
            }
            @Override public void remove() { throw new UnsupportedOperationException(); }
        };
    }

    /**
     * Enumerates all nodes on the given level regardless of whether or not they are stored in the lattice. Note: hasNext() is
     * not implemented. Simply iterate until <code>null</code> is returned.
     * @return
     */
    private Iterator<int[]> listAllNodesImpl(final int level) {

        // Initialize
        final int[] heights = this.nodes.getHeights();
        final int dimensions = this.nodes.getDimensions();
        final int[] element = new int[dimensions];
        final IntegerStack offsets = new IntegerStack(dimensions);
        final int[] mins = new int[dimensions];
        
        // Determine minimal indices
        // TODO: These may be determined on-demand with more accuracy
        for (int i = 0; i < mins.length; i++) {
            int diff = numLevels() - heights[i];
            mins[i] = level - diff;
            mins[i] = mins[i] < 0 ? 0 : mins[i];
        }
        offsets.push(0);
        element[0] = 0;
        
        // Return
        return new Iterator<int[]>() {

            /** Current level*/
            int current = 0;
            
            @Override public boolean hasNext() { throw new UnsupportedOperationException(); }

            @Override
            public int[] next() {
                
                // Iterate
                while (true) {
                    
                    // End of node
                    while (offsets.peek() == heights[offsets.size() - 1] || current > level) {
                        int idx = offsets.size() - 1;
                        current -= element[idx];
                        element[idx] = 0;
                        offsets.pop();
                        if (offsets.empty()) {
                            return null;
                        }
                    }
                    
                    // Check and increment
                    offsets.inc();
                    
                    // Store
                    int val = offsets.peek() - 1;
                    int idx = offsets.size() - 1;
                    current = current - element[idx] + val;
                    element[idx] = val;
                    
                    // Branch
                    if (offsets.size() < dimensions) {
                        offsets.push(mins[offsets.size()]); // Inner node
                    } else if (current == level) {
                        return element; // Leaf node on required level
                    }
                }
            }
            @Override public void remove() { throw new UnsupportedOperationException(); }
        };
    }

    /**
     * For checking for concurrent modifications
     */
    boolean isModified() {
        return this.modified;
    }

    /**
     * Enumerates all nodes regardless of whether or not they are stored in the lattice
     * @return
     */
    Iterator<int[]> listAllNodes() {
        return new WrappedIntArrayIterator(null, this.listAllNodesImpl());
    }

    /**
     * Enumerates all nodes on the given level regardless of whether or not they are stored in the lattice
     * @return
     */
    Iterator<int[]> listAllNodes(int level) {
        return new WrappedIntArrayIterator(null, this.listAllNodesImpl(level));
    }
    
    @Override
    LongIterator listAllNodesAsIdentifiers() {
        throw new UnsupportedOperationException();
    }

    @Override
    LongIterator listAllNodesAsIdentifiers(int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    void materialize() {
        throw new UnsupportedOperationException();
    }

    /**
     * For checking for concurrent modifications
     */
    void setModified() {
        this.modified = true;
    }
    
    /**
     * For checking for concurrent modifications
     */
    void setUnmodified() {
        this.modified = false;
    }
}