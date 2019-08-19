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

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class provides methods for mapping between three different spaces for nodes:
 * <ul>
 * <li>The source space: this space is meant to provide natural representations of elements, e.g., (A, B, C)</li>
 * <li>The index space:  this space represents elements by indices that correspond to the position of each element in the
 *                       source space. Assuming the whole alphabet as a source space for all dimensions, the index representation
 *                       of (A, B, C) is (0, 1, 2). All interactions with this library are performed in the index space.</li>
 * <li>The id space:     in this space, each element is represented by an identifier, which is a positive long value. You may use
 *                       this representation to store larger sets of elements or to use them as keys in maps.</li>
 * </ul> 
 * @author Fabian Prasser
 */
public class JHPLSpace<T> {

    /**
     * Translation iterator
     * @author Fabian Prasser
     */
    private class IdToIndexIterator extends TranslatedIterator<Long, int[]> {
        IdToIndexIterator(Iterator<Long> iter) { super(iter, new int[dimensions]); }
        @Override protected int[] translate(Long element, int[] result) { return toIndex(result, element); }
    }
    /**
     * Translation iterator
     * @author Fabian Prasser
     */
    private class IdToSourceIterator extends TranslatedIterator<Long, T[]> {
        @SuppressWarnings("unchecked")
        IdToSourceIterator(Iterator<Long> iter) { super(iter, (T[]) Array.newInstance(elements[0][0].getClass(), elements.length)); }
        @Override protected T[] translate(Long element, T[] result) { return toSource(result, element); }
    }
    /**
     * Translation iterator
     * @author Fabian Prasser
     */
    private class IndexToIdIterator extends TranslatedIterator<int[], Long> {
        IndexToIdIterator(Iterator<int[]> iter) { super(iter, 0l); }
        @Override protected Long translate(int[] element, Long result) { return toId(element); }
    }
    /**
     * Translation iterator
     * @author Fabian Prasser
     */
    private class IndexToSourceIterator extends TranslatedIterator<int[], T[]> {
        @SuppressWarnings("unchecked")
        IndexToSourceIterator(Iterator<int[]> iter) { super(iter, (T[]) Array.newInstance(elements[0][0].getClass(), elements.length)); }
        @Override protected T[] translate(int[] element, T[] result) { return toSource(result, element); }
    }
    /**
     * Translation iterator
     * @author Fabian Prasser
     */
    private class SourceToIdIterator extends TranslatedIterator<T[], Long> {
        SourceToIdIterator(Iterator<T[]> iter) { super(iter, 0l); }
        @Override protected Long translate(T[] element, Long result) { return toId(element); }
    }
    /**
     * Translation iterator
     * @author Fabian Prasser
     */
    private class SourceToIndexIterator extends TranslatedIterator<T[], int[]> {
        SourceToIndexIterator(Iterator<T[]> iter) { super(iter, new int[dimensions]); }
        @Override protected int[] translate(T[] element, int[] result) { return toIndex(result, element); }
    }
    
    /** 
     * An iterator that translates between different spaces
     * @author Fabian Prasser
     *
     * @param <U>
     * @param <V>
     */
    private abstract class TranslatedIterator<U, V> implements Iterator<V> {
        
        /** Backing iterator */
        private final Iterator<U> iter;
        /** Element */
        private final V           element;

        /**
         * Creates a new instance
         * @param iter
         * @param element
         */
        TranslatedIterator(Iterator<U> iter, V element) {
            this.iter = iter;
            this.element = element;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public V next() {
            U next = iter.next();
            return next == null ? null : translate(next, element);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Translation
         * @param element
         * @param result
         * @return
         */
        protected abstract V translate(U element, V result);        
    }
    
    /** The number of dimensions */
    private final int               dimensions;

    /** T[] per dimension */
    private final T[][]             elements;

    /** Map from T to index per dimension */
    private final Map<T, Integer>[] indices;

    /** The offsets for each dimension */
    private final long[]            multiplier;

    /** The number of nodes */
    private final long              numNodes;

    /**
     * Creates a new instance
     * @param nodes
     * @param elements
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    JHPLSpace(JHPLNodes<T> nodes, T[]... elements){
        this.elements = elements;
        this.dimensions = elements.length;
        this.multiplier = new long[this.dimensions];
        this.multiplier[elements.length - 1] = 1;
        for (int i = elements.length - 2; i >= 0; i--) {
            this.multiplier[i] = this.elements[i + 1].length * this.multiplier[i + 1];
        }
        this.indices = new HashMap[elements.length];
        long nnodes = 1;
        double ndouble = 1d;
        for (int j = 0; j < elements.length; j++) {
            T[] dimension = elements[j];
            nnodes *= (long)dimension.length;
            ndouble *= (double)dimension.length;
            this.indices[j] = new HashMap<T, Integer>(dimension.length);
            for (int i = 0; i < dimension.length; i++) {
                this.indices[j].put(dimension[i], i);
            }
        }
        if (ndouble > Long.MAX_VALUE) {
            throw new IllegalArgumentException("A JHPL Lattice must not have more than 2^63-1 nodes");
        }
        this.numNodes = nnodes;
    }

    /**
     * Constructs an iterator of indexed elements representing the given node in the id space
     * @param iter
     * @return
     */
    public Iterator<int[]> idIteratorToIndexIterator(Iterator<Long> iter) {
        return new IdToIndexIterator(iter);
    }

    /**
     * Constructs an iterator of source elements representing the given node in the id space
     * @param iter
     * @return
     */
    public Iterator<T[]> idIteratorToSourceIterator(Iterator<Long> iter) {
        return new IdToSourceIterator(iter);
    }

    
    /**
     * Constructs an iterator over longs representing the given node in the id space
     * @param iter
     * @return
     */
    public Iterator<Long> indexIteratorToIdIterator(Iterator<int[]> iter) {
        return new IndexToIdIterator(iter);
    }

    /**
     * Constructs an iterator of source elements representing the given node in the id space
     * @param iter
     * @return
     */
    public Iterator<T[]> indexIteratorToSourceIterator(Iterator<int[]> iter) {
        return new IndexToSourceIterator(iter);
    }
    
    /**
     * Constructs an iterator over longs representing the given node in the id space
     * @param iter
     * @return
     */
    public Iterator<Long> sourceIteratorToIdIterator(Iterator<T[]> iter) {
        return new SourceToIdIterator(iter);
    }

    /**
     * Constructs an iterator of indexed elements representing the given node in the id space
     * @param iter
     * @return
     */
    public Iterator<int[]> sourceIteratorToIndexIterator(Iterator<T[]> iter) {
        return new SourceToIndexIterator(iter);
    }

    /**
     * Constructs a long representing the given node in the id space
     * @param node
     * @return
     */
    public long toId(int[] node) {
        long id = 0;
        for (int i = 0; i < dimensions; i++) {
            id += (long) node[i] * multiplier[i];
        }
        return id;
    }

    /**
     * Constructs a long representing the given node in the id space
     * @param node
     * @return
     */
    public long toId(T[] node) {
        long id = 0;
        for (int i = 0; i < dimensions; i++) {
            id += (long) indices[i].get(node[i]) * multiplier[i];
        }
        return id;
    }

    

    /**
     * Constructs an array representing the given node in the index space. Reuses the given array.
     * @param result
     * @param node
     * @return
     */
    public int[] toIndex(int[] result, long id) {
        checkId(id);
        for (int i = 0; i < dimensions; i++) {
            result[i] = (int)(id / multiplier[i]);
            id %= multiplier[i];
        }
        return result;
    }

    /**
     * Constructs an array representing the given node in the index space. Reuses the given array.
     * @param result
     * @param node
     * @return
     */
    public int[] toIndex(int[] result, T[] node) {
        for (int i = 0; i < dimensions; i++) {
            result[i] = indices[i].get(node[i]);
        }
        return result;
    }

    /**
     * Constructs an array representing the given node in the index space.
     * @param node
     * @return
     */
    public int[] toIndex(long id) {
        int[] result = new int[dimensions];
        for (int i = 0; i < dimensions; i++) {
            long mult = multiplier[i];
            result[i] = (int)(id / mult);
            id %= mult;
        }
        return result;
    }

    /**
     * Constructs an array representing the given node in the index space.
     * @param node
     * @return
     */
    public int[] toIndex(T[] node) {
        return toIndex(new int[dimensions], node);
    }


    /**
     * Constructs an array representing the given node in the source space.
     * @param node
     * @return
     */
    @SuppressWarnings("unchecked")
    public T[] toSource(int[] node) {
        return toSource((T[]) Array.newInstance(this.elements[0][0].getClass(), node.length), node);
    }

    /**
     * Constructs an array representing the given node in the source space. Reuses the given array.
     * @param result
     * @param node
     * @return
     */
    @SuppressWarnings("unchecked")
    public T[] toSource(long id) {
        checkId(id);
        return toSource((T[]) Array.newInstance(this.elements[0][0].getClass(), dimensions), id);
    }
    

    /**
     * Constructs an array representing the given node in the source space. Reuses the given array.
     * @param result
     * @param node
     * @return
     */
    public T[] toSource(T[] result, int[] node) {
        for (int i = 0; i < dimensions; i++) {
            result[i] = this.elements[i][node[i]];
        }
        return result;
    }
    
    /**
     * Constructs an array representing the given node in the source space. Reuses the given array.
     * @param result
     * @param node
     * @return
     */
    public T[] toSource(T[] result, long id) {
        checkId(id);
        for (int i = 0; i < dimensions; i++) {
            int idx = (int)(id / multiplier[i]);
            if (idx < 0 || idx >= this.elements[i].length) {
                throw new IllegalArgumentException("Invalid node id");
            }
            result[i] = this.elements[i][idx];
            id %= multiplier[i];
        }
        return result;
    }

    /**
     * Checks the id
     * @param id
     */
    private void checkId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Invalid id: must not be negative");
        } else if (id >= numNodes) { 
            throw new IllegalArgumentException("Invalid id: must not be larger than " + (numNodes - 1)); 
        }
    }
}
