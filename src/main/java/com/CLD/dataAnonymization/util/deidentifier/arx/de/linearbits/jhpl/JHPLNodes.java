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

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.ConditionalIntArrayIterator;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.IntArrayCondition;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.LongIterator;

/**
 * This class implements methods for working with nodes.
 * 
 * @author Fabian Prasser
 *
 * @param <T>
 */
public class JHPLNodes<T> {

    /** Bottom node */
    private final int[]         bottom;
    /** The number of dimensions */
    private final int           dimensions;
    /** Elements */
    private final T[][]         elements;
    /** The height of each dimension */
    private final int[]         heights;
    /** Top node */
    private final int[]         top;
    /** Lattice */
    private final Lattice<T, ?> lattice;
    /** Multiplier */
    private final long[]        multiplier;

    /**
     * Creates a new instance
     * @param elements
     */
    @SafeVarargs
    JHPLNodes(Lattice<T, ?> lattice, T[]... elements) {
        this.dimensions = elements.length;
        this.heights = new int[this.dimensions];
        for (int j = 0; j < elements.length; j++) {
            heights[j] = elements[j].length;
        }
        this.bottom = new int[elements.length];
        this.top = new int[elements.length];
        for (int i = 0; i < elements.length; i++) {
            top[i] = elements[i].length - 1;
        }
        this.elements = elements;
        this.lattice = lattice;
        this.multiplier = new long[this.dimensions];
        this.multiplier[elements.length - 1] = 1;
        for (int i = elements.length - 2; i >= 0; i--) {
            this.multiplier[i] = this.elements[i + 1].length * this.multiplier[i + 1];
        }
    }

    /**
     * Returns a builder for nodes from the source space
     * @return
     */
    public JHPLBuilder<T> build() {
        return new JHPLBuilder<T>(elements);
    }

    /**
     * Returns a builder for nodes from the source space
     * using the given array
     * @param result
     * @return
     */
    public JHPLBuilder<T> build(int[] result) {
        return new JHPLBuilder<T>(result, elements);
    }

    /**
     * Returns a representation of the bottom node.
     * @return
     */
    public int[] getBottom() {
        return bottom;
    }

    /**
     * Returns the level of the given node
     * @param element
     * @return
     */
    public int getLevel(int[] element) {
        int level = 0;
        for (int dimension : element) {
            level += dimension;
        }
        return level;
    }

    /**
     * Returns the level of the given node
     * @param id
     * @return
     */
    public int getLevel(long id) {
        int level = 0;
        for (int i = 0; i < dimensions; i++) {
            long mult = multiplier[i];
            level += (int)(id / mult);
            id %= mult;
        }
        return level;
    }

    /**
     * Multiplier
     * @return
     */
    public long[] getMultiplier() {
        return this.multiplier;
    }

    /**
     * Returns the top node
     * @return
     */
    public int[] getTop() {
        return top;
    }

    /**
     * Determines whether a direct parent-child relationship exists
     * @param parent
     * @param child
     * @return
     */
    public boolean isDirectParentChild(int[] parent, int[] child) {
        int diff = 0;
        for (int i=0; i<parent.length; i++) {
            if (parent[i] < child[i]) {
                return false;
            } else {
                diff += parent[i] - child[i];
            }
        }
        return diff == 1;
    }

    /**
     * Determines whether a parent-child relationship exists
     * @param parent
     * @param child
     * @return
     */
    public boolean isParentChild(int[] parent, int[] child) {
        int diff = 0;
        for (int i=0; i<parent.length; i++) {
            if (parent[i] < child[i]) {
                return false;
            } else {
                diff += parent[i] - child[i];
            }
        }
        return diff != 0;
    }
    
    /**
     * Determines whether the arrays represent the same node
     * @param parent
     * @param child
     * @return
     */
    public boolean isSame(int[] node1, int[] node2) {
        return Arrays.equals(node1, node2);
    }

    /**
     * Returns an iterator over all predecessors. Note: the iterator will always return the same array.
     * @param node
     * @return
     */
    public Iterator<int[]> listPredecessors(int[] node) {
        return listPredecessors(new int[dimensions], node);
    }

    /**
     * Returns an iterator over all predecessors
     * @param node
     * @return
     */
    public LongIterator listPredecessors(final long _id) {

        return new LongIterator() {
            
            // State
            long id = _id;
            // State
            int dimension = 0;
            // State
            long next = pull();
            
            @Override
            public boolean hasNext() {
                return next >= 0;
            }
            
            @Override
            public long next() {
                long result = next;
                next = pull();
                return result;
            }

            /**
             * Returns the id of the next element, returns a negative value if there is no such element
             * @return
             */
            private long pull() {

                long value, result = -1;
                
                while (dimension < dimensions) {
                    long mult = multiplier[dimension];
                    value = id / mult - 1;
                    id = id % mult;
                    result = _id - mult;
                    dimension++;
                    if (value >= 0) {
                        break;
                    } else {
                        result = -1;
                    }
                } 
                
                return result;
            }
        };
    }

    /**
     * Returns all predecessors based on multiple precomputed values
     * @param node
     * @param identifier
     * @return
     */
    public LongIterator listPredecessorsAsIdentifiers(final int[] node, final long identifier) {

        return new LongIterator() {
            
            // State
            int dimension = 0;
            // State
            long next = pull();
            
            @Override
            public boolean hasNext() {
                return next >= 0;
            }
            
            @Override
            public long next() {
                long result = next;
                next = pull();
                return result;
            }

            /**
             * Returns the id of the next element, returns a negative value if there is no such element
             * @return
             */
            private long pull() {
                long result = -1;
                while (dimension < dimensions) {
                    if (node[dimension] - 1 >= 0) {
                        result = identifier - multiplier[dimension];
                        dimension++;
                        break;
                    } else {
                        result = -1;
                        dimension++;
                    }
                } 
                return result;
            }
        };
    }

    /**
     * Lists all predecessors not stored in the lattice
     * @return
     */
    public Iterator<int[]> listPredecessorsNotStored(final int[] node) {
        return new ConditionalIntArrayIterator(listPredecessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.contains(array);
            }
        });
    }

    /**
     * Lists all predecessors stored in the lattice
     * @return
     */
    public Iterator<int[]> listPredecessorsStored(final int[] node) {
        return new ConditionalIntArrayIterator(listPredecessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.contains(array);
            }
        });
    }
    
    /**
     * Lists all predecessors without any property
     * @return
     */
    public Iterator<int[]> listPredecessorsWithoutProperty(final int[] node) {
        final int level = this.getLevel(node) - 1;
        return new ConditionalIntArrayIterator(listPredecessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, level);
            }
        });
    }

    /**
     * Lists all predecessors without the given property
     * @param property
     * @return
     */
    public Iterator<int[]> listPredecessorsWithoutProperty(final int[] node, final PredictiveProperty property) {
        final int level = this.getLevel(node) - 1;
        return new ConditionalIntArrayIterator(listPredecessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, level, property);
            }
        });
    }

    /**
     * Lists all predecessors without any property and which have not been stored in this lattice
     * @return
     */
    public Iterator<int[]> listPredecessorsWithoutPropertyAndNotStored(final int[] node) {
        final int level = this.getLevel(node) - 1;
        return new ConditionalIntArrayIterator(listPredecessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, level) && !lattice.contains(array);
            }
        });
    }

    /**
     * Lists all predecessors with any property
     * @return
     */
    public Iterator<int[]> listPredecessorsWithProperty(final int[] node) {
        final int level = this.getLevel(node) - 1;
        return new ConditionalIntArrayIterator(listPredecessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, level);
            }
        });
    }

    /**
     * Lists all predecessors with the given property
     * @param property
     * @return
     */
    public Iterator<int[]> listPredecessorsWithProperty(final int[] node, final PredictiveProperty property) {
        final int level = this.getLevel(node) - 1;
        return new ConditionalIntArrayIterator(listPredecessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, level, property);
            }
        });
    }

    /**
     * Lists all predecessors with any property or which have been stored in the lattice
     * @return
     */
    public Iterator<int[]> listPredecessorsWithPropertyOrStored(final int[] node) {
        final int level = this.getLevel(node) - 1;
        return new ConditionalIntArrayIterator(listPredecessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, level) || lattice.contains(array);
            }
        });
    }
    

    /**
     * Returns an iterator over all successors. Note: the iterator will always return the same array
     * @param node
     * @return
     */
    public Iterator<int[]> listSuccessors(int[] node) {
        return listSuccessors(new int[dimensions], node);
    }

    /**
     * Returns an iterator over all successors
     * @param node
     * @return
     */
    public LongIterator listSuccessors(final long _id) {
        
        return new LongIterator() {
            
            // State
            long id = _id;
            // State
            int dimension = 0;
            // State
            long next = pull();
            
            @Override
            public boolean hasNext() {
                return next >= 0;
            }
            
            @Override
            public long next() {
                long result = next;
                next = pull();
                return result;
            }

            /**
             * Returns the id of the next element, returns a negative value if there is no such element
             * @return
             */
            private long pull() {

                long value, result = -1;
                
                while (dimension < dimensions) {
                    long mult = multiplier[dimension];
                    value = id / mult + 1;
                    id = id % mult;
                    result = _id + mult;
                    dimension++;
                    if (value < heights[dimension - 1]) {
                        break;
                    } else { 
                        result = -1;
                    }
                } 
                
                return result;
            }
        };
    }

    /**
     * Returns all successors based on multiple precomputed values
     * @param node
     * @param identifier
     * @return
     */
    public LongIterator listSuccessorsAsIdentifiers(final int[] node, 
                                                    final long identifier) {

        return new LongIterator() {
            
            // State
            int dimension = 0;
            // State
            long next = pull();
            
            @Override
            public boolean hasNext() {
                return next >= 0;
            }
            
            @Override
            public long next() {
                long result = next;
                next = pull();
                return result;
            }

            /**
             * Returns the id of the next element, returns a negative value if there is no such element
             * @return
             */
            private long pull() {

                long result = -1;
                while (dimension < dimensions) {
                    if (node[dimension] + 1 < heights[dimension]) {
                        result = identifier + multiplier[dimension];
                        dimension++;
                        break;
                    } else { 
                        result = -1;
                        dimension++;
                    }
                } 
                return result;
            }
        };
    }

    /**
     * Lists all successors not stored in the lattice
     * @return
     */
    public Iterator<int[]> listSuccessorsNotStored(final int[] node) {
        return new ConditionalIntArrayIterator(listSuccessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.contains(array);
            }
        });
    }

    /**
     * Lists all successors stored in the lattice
     * @return
     */
    public Iterator<int[]> listSuccessorsStored(final int[] node) {
        return new ConditionalIntArrayIterator(listSuccessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.contains(array);
            }
        });
    }

    /**
     * Lists all successors without any property
     * @return
     */
    public Iterator<int[]> listSuccessorsWithoutProperty(final int[] node) {
        final int level = this.getLevel(node) + 1;
        return new ConditionalIntArrayIterator(listSuccessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, level);
            }
        });
    }

    /**
     * Lists all successors without the given property
     * @param property
     * @return
     */
    public Iterator<int[]> listSuccessorsWithoutProperty(final int[] node, final PredictiveProperty property) {
        final int level = this.getLevel(node) + 1;
        return new ConditionalIntArrayIterator(listSuccessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, level, property);
            }
        });
    }

    /**
     * Lists all successors without any property and which have not been stored in this lattice
     * @return
     */
    public Iterator<int[]> listSuccessorsWithoutPropertyAndNotStored(final int[] node) {
        final int level = this.getLevel(node) + 1;
        return new ConditionalIntArrayIterator(listSuccessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, level) && !lattice.contains(array);
            }
        });
    }   
    

    /**
     * Lists all successors with any property
     * @return
     */
    public Iterator<int[]> listSuccessorsWithProperty(final int[] node) {
        final int level = this.getLevel(node) + 1;
        return new ConditionalIntArrayIterator(listSuccessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, level);
            }
        });
    }
    

    /**
     * Lists all successors with the given property
     * @param property
     * @return
     */
    public Iterator<int[]> listSuccessorsWithProperty(final int[] node, final PredictiveProperty property) {
        final int level = this.getLevel(node) + 1;
        return new ConditionalIntArrayIterator(listSuccessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, level, property);
            }
        });
    }


    /**
     * Lists all successors with any property or which have been stored in the lattice
     * @return
     */
    public Iterator<int[]> listSuccessorsWithPropertyOrStored(final int[] node) {
        final int level = this.getLevel(node) + 1;
        return new ConditionalIntArrayIterator(listSuccessors(node), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, level) || lattice.contains(array);
            }
        });
    }

    /**
     * Returns an iterator over all predecessors. Note: the iterator will always return the same array. Reuses the given array.
     * @param result
     * @param node
     * @return
     */
    private Iterator<int[]> listPredecessors(final int[] result, final int[] node) {

        lattice.setUnmodified();
        
        if (Arrays.equals(node, bottom)) { 
            return new Iterator<int[]>() {
                @Override
                public boolean hasNext() {
                    return false;
                }
    
                @Override
                public int[] next() {
                    return null;
                }
    
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            }; 
        }

        for (int i = 0; i < node.length; i++) {
            result[i] = node[i];
        }

        return new Iterator<int[]>() {

            private int index    = -1;
            private int previous = -1;
            
            @Override
            public boolean hasNext() {
                if (lattice.isModified()) {
                    throw new ConcurrentModificationException();
                }
                for (int i = index + 1; i < dimensions; i++) {
                    if (node[i] > 0) { return true; }
                }
                return false;
            }

            @Override
            public int[] next() {
                if (lattice.isModified()) {
                    throw new ConcurrentModificationException();
                }
                if (previous != -1) {
                    result[previous] = node[previous];
                }
                
                while (index < result.length - 1) {
                    index++;
                    if (node[index] > 0) {
                        result[index] = node[index] - 1;
                        previous = index;
                        break;
                    }
                }
                return previous != index ? null : result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns an iterator over all successors. Note: the iterator will always return the same array. Reuses the given array.
     * @param result
     * @param node
     * @return
     */
    private Iterator<int[]> listSuccessors(final int[] result, final int[] node) {

        lattice.setUnmodified();
        
        if (Arrays.equals(node, top)) { 
            return new Iterator<int[]>() {
                @Override
                public boolean hasNext() {
                    return false;
                }
    
                @Override
                public int[] next() {
                    return null;
                }
    
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            }; 
        }

        for (int i = 0; i < node.length; i++) {
            result[i] = node[i];
        }

        return new Iterator<int[]>() {

            int index    = -1;
            int previous = -1;

            @Override
            public boolean hasNext() {
                if (lattice.isModified()) {
                    throw new ConcurrentModificationException();
                }
                for (int i = index + 1; i < dimensions; i++) {
                    if (node[i] < heights[i] - 1) { return true; }
                }
                return false;
            }

            @Override
            public int[] next() {
                if (lattice.isModified()) {
                    throw new ConcurrentModificationException();
                }
                if (previous != -1) {
                    result[previous] = node[previous];
                }
                while (index < result.length - 1) {
                    index++;
                    if (node[index] < heights[index] - 1) {
                        result[index] = node[index] + 1;
                        previous = index;
                        break;
                    }
                }
                return previous != index ? null : result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Dimensions
     * @return
     */
    int getDimensions() {
        return this.dimensions;
    }

    /**
     * Heights
     * @return
     */
    int[] getHeights() {
        return this.heights;
    }
}
