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
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.WrappedPrimitiveLongIterator;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLStack.IntegerStack;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLStack.LongStack;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.PredictiveProperty.Direction;

import java.util.*;

/**
 * This class implements a storage structure for information about elements in very large lattices. To avoid 
 * materialization, it supports "predictive" properties, i.e., properties which are inherited to predecessors 
 * or successors of an element in the lattice. Properties can be predictive either for all (direct and indirect) 
 * successors or for all (direct and indirect) predecessors or for both (direct and indirect) successors and
 * (direct and indirect) predecessors of a given element. In addition to predictive properties, data can 
 * be associated to individual nodes.<br>
 * 
 * @author Fabian Prasser
 *
 * @param <T> The type of values in the dimensions of the lattice
 * @param <U> The type of associated data
 */
public class Lattice<T, U> {

    /** Data */
    private final JHPLData<T, U>                            data;
    /** All materialized nodes */
    private final JHPLTrie                                  master;
    /** Nodes */
    private final JHPLNodes<T>                              nodes;
    /** Tries for properties */
    private final Map<PredictiveProperty, JHPLTrie>         propertiesDown;
    /** Tries for properties */
    private final Map<PredictiveProperty, JHPLTrie>         propertiesUp;
    /** Tries for properties */
    private final Map<PredictiveProperty, JHPLMap<Boolean>> propertiesNone;
    /** Space */
    private final JHPLSpace<T>                              space;
    /** Unsafe */
    private final JHPLUnsafe                                unsafe;
    /** Number of nodes */
    private final long                                      numNodes;
    /** Track modifications */
    private boolean                                         modified = false;
    /** Data */
    private final int[]                                     heights;
    /** Data */
    private final long[]                                    multiplier;

    /**
     * Internal constructor for superclass
     */
    Lattice() {
        this.numNodes = 0;
        this.nodes = null;
        this.space = null;
        this.data = null;
        this.propertiesUp = null;
        this.propertiesDown = null;
        this.propertiesNone = null;
        this.master = null;
        this.unsafe = null;
        this.heights = null;
        this.multiplier = null;
    }
        
    /**
     * Constructs a new lattice
     * 
     * @param elements One array of elements per dimension, ordered from the lowest to the highest element
     */
    @SuppressWarnings("unchecked")
    public Lattice(T[]... elements) {
        
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
        
        double dSize = 1d;
        long lSize = 1l;
        for (T[] dimension : elements) {
            dSize *= dimension.length;
            lSize *= dimension.length;
        }
        if (dSize > Long.MAX_VALUE) {
            throw new IllegalArgumentException("Lattice must not have more than Long.MAX_VALUE elements");
        }
        this.numNodes = lSize;
        
        this.nodes = new JHPLNodes<T>(this, elements);
        this.space = new JHPLSpace<T>(nodes, elements);
        this.data = new JHPLData<T, U>(space, elements);
        this.propertiesUp = new HashMap<PredictiveProperty, JHPLTrie>();
        this.propertiesDown = new HashMap<PredictiveProperty, JHPLTrie>();
        this.propertiesNone = new HashMap<PredictiveProperty, JHPLMap<Boolean>>();
        this.master = new JHPLTrieEQ(this);
        this.unsafe = new JHPLUnsafe(this);
        this.heights = nodes.getHeights();
        this.multiplier = nodes.getMultiplier();
    }
        
    /**
     * Returns whether this lattice stores any information about the given node.
     * 
     * @param node
     * @return
     */
    public boolean contains(int[] node) {
        return master.contains(node);
    }
    
    /**
     * Returns a pretty accurate estimation of the memory consumed by this lattice
     * 
     * @return
     */
    public long getByteSize() {
        long size = 0;
        size += this.data.getByteSize();
        size += this.master.getByteSize();
        for (JHPLTrie trie : this.propertiesUp.values()) {
            size += trie.getByteSize();
        }
        for (JHPLTrie trie : this.propertiesDown.values()) {
            size += trie.getByteSize();
        }
        for (JHPLMap<Boolean> map : this.propertiesNone.values()) {
            size += map.getByteSize();
        }
        return size;
    }
    
    /**
     * Returns the data associated with the given node, <code>null</code> if there is none.
     * 
     * @param node
     * @return
     */
    public U getData(int[] node) {
        return this.data.get(node);
    }
    

    /**
     * Returns whether the node has any property.
     * @param node
     * @return
     */
    public boolean hasProperty(int[] node) {
        return hasProperty(node, nodes.getLevel(node));
    }
    
    /**
     * Returns whether the node has any property.
     * @param node
     * @param level
     * @return
     */
    public boolean hasProperty(int[] node, int level) {
        
        for (PredictiveProperty property : this.propertiesUp.keySet()) {
            if (this.propertiesUp.get(property).contains(node, level)){
                return true;
            }
        }
        for (PredictiveProperty property : this.propertiesDown.keySet()) {
            if (this.propertiesDown.get(property).contains(node, level)){
                return true;
            }
        }
        long id = space().toId(node);
        for (PredictiveProperty property : this.propertiesNone.keySet()) {
            Boolean result = this.propertiesNone.get(property).get(id);
            result = result == null ? false : result;
            return result;
        }
        return false;
    }

    /**
     * Returns whether the given node has the given property.
     * 
     * @param node
     * @param level
     * @param property
     * @return
     */
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
            
            JHPLMap<Boolean> map = this.propertiesNone.get(property);
            if (map == null) {
                return false;
            } else {
                Boolean result = map.get(space.toId(node));
                return result == null ? false : result;
            }
        default: 
            throw new IllegalArgumentException("Property with unknown direction");
        }
    }
        
    /**
     * Returns whether the given node has the given property.
     * 
     * @param node
     * @param property
     * @return
     */
    public boolean hasProperty(int[] node, PredictiveProperty property) {
        return hasProperty(node, nodes.getLevel(node), property);
    }
    
    /**
     * Enumerates all nodes on the given level regardless of whether or not they are stored in the lattice.
     * @return
     */
    public LongIterator listAllNodesAsIdentifiersImpl(final int level) {
        if (level < this.numLevels() / 2) {
            JHPLLongList result = new JHPLLongList();
            listAllNodesAsIdentifiersImplBottomUp(result, 0L, 0, level, 0);
            return result.iterator();
        } else {
            JHPLLongList result = new JHPLLongList();
            listAllNodesAsIdentifiersImplTopDown(result, this.numNodes-1, nodes.getLevel(this.numNodes-1), level, 0);
            return result.reverseIterator();
        }
    }

    /** 
     * Enumerates all nodes stored in the lattice
     * @return
     */
    public Iterator<int[]> listNodes() {
        return new WrappedIntArrayIterator(this, this.master.iterator());
    }
    
    /**
     * Enumerates all nodes stored on the given level
     * @param level
     * @return
     */
    public Iterator<int[]> listNodes(int level) {
        return new WrappedIntArrayIterator(this, this.master.iterator(level));
    }

    /** 
     * Enumerates all nodes stored in the lattice
     * @return
     */
    public LongIterator listNodesAsIdentifiers() {
        return new WrappedPrimitiveLongIterator(this, this.master.iteratorLong(this.nodes.getMultiplier()));
    }

    /**
     * Returns a class for working with nodes
     * @return
     */
    public JHPLNodes<T> nodes() {
        return nodes;
    }

    /**
     * Returns the number of dimensions of this lattice
     * @return
     */
    public int numDimensions() {
        return this.nodes.getDimensions();
    }
    
    /**
     * Returns the number of levels in this lattice
     * @return
     */
    public int numLevels() {
        return master.getLevels();
    }

    /**
     * Returns the number of nodes in this lattice
     * @return
     */
    public long numNodes(){
        return numNodes;
    }
    
    /**
     * Associates the given node with the given data.
     *  
     * @param node
     * @param data
     */
    public void putData(int[] node, U data) {
        
        this.setModified();
        this.data.put(node, data);

        // Store in master trie
        this.master.put(node);
    }

    
    /**
     * Stores the given property for the given node. If the property is predictive in an upwards direction, it 
     * will also be stored for all successors of the given node. If the property is predictive in a downwards direction,
     * it will also be stored for all predecessors of the given node.
     * 
     * @param node
     * @param level
     * @param property
     */
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
            JHPLMap<Boolean> map = this.propertiesNone.get(property);
            if (map == null) {
                map = new JHPLMap<Boolean>();
                this.propertiesNone.put(property, map);
            }
            this.propertiesNone.get(property).put(space().toId(node), true);
            break;
        default:
            throw new IllegalArgumentException("Property with unknown direction");
        }
    }
    
    /**
     * Stores the given property for the given node. If the property is predictive in an upwards direction, it 
     * will also be stored for all successors of the given node. If the property is predictive in a downwards direction,
     * it will also be stored for all predecessors of the given node.
     * 
     * @param node
     * @param property
     */
    public void putProperty(int[] node, PredictiveProperty property) {
        putProperty(node, nodes.getLevel(node), property);
    }

    /**
     * Returns a class for mapping between spaces
     * @return
     */
    public JHPLSpace<T> space() {
        return space;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Lattice\n");
        if (!propertiesUp.isEmpty()) {
            builder.append("+-- Upwards-predictive properties\n");
            toString(builder, propertiesUp);
        }
        if (!propertiesDown.isEmpty()) {
            builder.append("+-- Downwards-predictive properties\n");
            toString(builder, propertiesDown);
        }
        if (!propertiesNone.isEmpty()) {
            builder.append("+-- Non-predictive properties\n");
            toStringNone(builder, propertiesNone);
        }
        builder.append("+-- Master\n");
        builder.append(master.toString("|   +-- ", "|       "));
        builder.append("+-- Memory: ").append(getByteSize()).append(" [bytes]\n");
        return builder.toString();
    }

    /**
     * Allows for accessing methods that may not safe to be used on very large lattices
     * @return
     */
    public JHPLUnsafe unsafe() {
        return this.unsafe;
    }

    /**
     * Enumerates all nodes regardless of whether or not they are stored in the lattice. Note: hasNext() is
     * not implemented. Simply iterate until <code>null</code> is returned.
     * @return
     */
    private LongIterator listAllNodesAsIdentifiersImpl(final long[] multiplier) {

        // Initialize
        final int[] heights = this.nodes.getHeights();
        final int dimensions = this.nodes.getDimensions();
        final LongStack identifiers = new LongStack(dimensions);
        final IntegerStack offsets = new IntegerStack(dimensions);
        offsets.push(0);
        identifiers.push(0L);
        
        // Return
        return new LongIterator() {
            
            @Override public boolean hasNext() { throw new UnsupportedOperationException(); }

            @Override
            public long next() {
                
                // Iterate
                while (true) {
                    
                    // End of node
                    while (offsets.peek() == heights[offsets.size() - 1]) {
                        offsets.pop();
                        identifiers.pop();
                        if (offsets.empty()) {
                            return -1;
                        }
                    }
                    
                    // Check and increment
                    offsets.inc();
                    
                    // Store
                    long identifier = identifiers.peek() + (offsets.peek() - 1) * multiplier[offsets.size() - 1];
                    
                    // Branch
                    if (offsets.size() < dimensions) {
                        identifiers.push(identifier);
                        offsets.push(0); // Inner node
                    } else {
                        return identifier; // Leaf node
                    }
                }
            }
        };
    }

    /**
     * Enumerates all nodes on the given level regardless of whether or not they are stored in the lattice.
     * @return
     */
    private void listAllNodesAsIdentifiersImplBottomUp(JHPLLongList result,
                                                       long currentId,
                                                       int currentLevel,
                                                       int targetLevel,
                                                       int currentDimension) {
        
        currentLevel--;
        currentId-=multiplier[currentDimension];
        for (int i = 0; i < heights[currentDimension]; i++) {

            currentLevel++;
            
            if (currentLevel > targetLevel) {
                return;
            }
            
            currentId +=multiplier[currentDimension];
            
            if (currentDimension == heights.length - 1) {
                if (currentLevel == targetLevel) {
                    result.add(currentId);
                }
            } else {
            
                listAllNodesAsIdentifiersImplBottomUp(result,
                                                      currentId,
                                                      currentLevel,
                                                      targetLevel,
                                                      currentDimension + 1);
            }
        }
    }

    /**
     * Enumerates all nodes on the given level regardless of whether or not they are stored in the lattice.
     * @return
     */
    private void listAllNodesAsIdentifiersImplTopDown(JHPLLongList result,
                                                      long currentId,
                                                      int currentLevel,
                                                      int targetLevel,
                                                      int currentDimension) {

        currentLevel++;
        currentId+=multiplier[currentDimension];
        for (int i = 0; i < heights[currentDimension]; i++) {
            
            currentLevel--;
                        
            if (currentLevel < targetLevel) {
                return;
            }
            
            currentId -= multiplier[currentDimension];
            
            if (currentDimension == heights.length - 1) {
                if (currentLevel == targetLevel) {
                    result.add(currentId);
                }
            } else {

                listAllNodesAsIdentifiersImplTopDown(result,
                                                     currentId,
                                                     currentLevel,
                                                     targetLevel,
                                                     currentDimension + 1);
            }
        }
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
     * Materializes the whole lattice
     * @param element
     * @param heights
     * @param dimension
     * @param trie
     */
    private void materialize(int[] element, int level, int[] heights, int dimension, JHPLTrie trie) {
        if (dimension == heights.length) {
            trie.put(element);
        } else {
            for (int i=0; i<heights[dimension]; i++) {
                element[dimension] = i;
                materialize(element, level + i, heights, dimension + 1, trie);
            }
        }
    }

    /**
     * To string
     * @param builder
     * @param properties
     */
    private void toString(StringBuilder builder , Map<PredictiveProperty, JHPLTrie> properties) {
        List<PredictiveProperty> list = new ArrayList<PredictiveProperty>();
        list.addAll(properties.keySet());
        for (int i=0; i<list.size()-1; i++) {
            PredictiveProperty property = list.get(i);
            builder.append("|   +-- ").append(property.getLabel()).append("\n");
            builder.append(properties.get(property).toString("|   |   +-- ", "|   |       "));
        }
        if (!list.isEmpty()) {
            PredictiveProperty property = list.get(list.size()-1);
            builder.append("|   +-- ").append(property.getLabel()).append("\n");
            builder.append(properties.get(property).toString("|       +-- ", "|           "));
        }
    }
    
    /**
     * To string
     * @param builder
     * @param properties
     */
    private void toStringNone(StringBuilder builder , Map<PredictiveProperty, JHPLMap<Boolean>> properties) {
        List<PredictiveProperty> list = new ArrayList<PredictiveProperty>();
        list.addAll(properties.keySet());
        for (int i=0; i<list.size()-1; i++) {
            PredictiveProperty property = list.get(i);
            builder.append("|   +-- ").append(property.getLabel()).append("\n");
            builder.append("|   |   +-- Not implemented");
        }
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
    
    /**
     * Enumerates all nodes regardless of whether or not they are stored in the lattice
     * @return
     */
    LongIterator listAllNodesAsIdentifiers() {
        return new WrappedPrimitiveLongIterator(null, this.listAllNodesAsIdentifiersImpl(nodes.getMultiplier()));
    }

    /**
     * Enumerates all nodes on the given level regardless of whether or not they are stored in the lattice
     * @return
     */
    LongIterator listAllNodesAsIdentifiers(int level) {
        return this.listAllNodesAsIdentifiersImpl(level);
    }

    /**
     * Materializes the whole lattice. This method is similar to calling put() for each node returned
     * by enumerateAllNodes(). It is here for your convenience, only. 
     */
    void materialize() {
        int[] element = new int[nodes.getDimensions()];
        int[] heights = nodes.getHeights();
        materialize(element, 0, heights, 0, master);
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