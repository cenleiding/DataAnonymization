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

import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * This class provides various iterators needed for enumerating nodes
 * 
 * @author Fabian Prasser
 */
public class JHPLIterator {

    /**
     * Long iterator
     * @author prasser
     *
     */
    public static interface LongIterator {
        public boolean hasNext();
        public long next();
    };

    /**
     * Long iterator
     * @author prasser
     */
    public static class WrappedPrimitiveLongIterator implements LongIterator {

        /** Iterator */
        private LongIterator        iter;
        /** Next element */
        private long                next   = -1;
        /** Have we already pulled the next element */
        private boolean             pulled = true;
        /** Lattice */
        private final Lattice<?, ?> lattice;

        /**
         * Constructs a new instance
         * @param iterator
         */
        WrappedPrimitiveLongIterator(Lattice<?, ?> lattice, LongIterator iterator){
            this.iter = iterator;
            this.next = iter.next();
            this.lattice = lattice;
            if (this.lattice != null) {
                this.lattice.setUnmodified();
            }
        }
        
        @Override 
        public boolean hasNext() {
            if (lattice != null && lattice.isModified()) {
                throw new ConcurrentModificationException();
            }
            if (!pulled) {
                next = iter.next();
                pulled = true;
            }
            return next != -1;
        }
        
        @Override
        public long next() {
            if (lattice != null && lattice.isModified()) {
                throw new ConcurrentModificationException();
            }
            if (!pulled) {
                next = iter.next();
            }
            pulled = false;
            return next;
        }
    };

    /**
     * An iterator that wraps another iterator and only returns elements for which the
     * given condition holds.
     * 
     * @author Fabian Prasser
     */
    static class ConditionalIntArrayIterator implements Iterator<int[]> {

        /** Iterator */
        private final Iterator<int[]>   iter;
        /** Next element */
        private int[]                   next   = null;
        /** Have we already pulled the next element */
        private boolean                 pulled = true;
        /** Condition */
        private final IntArrayCondition condition;
        
        /**
         * Constructs a new instance
         * @param iterator
         */
        ConditionalIntArrayIterator(Iterator<int[]> iterator, IntArrayCondition condition){
            this.iter = iterator;
            this.condition = condition;
            this.pull();
        }
        
        @Override 
        public boolean hasNext() {
            if (!pulled) {
                pull();
            }
            return next != null;
        }

        @Override
        public int[] next() {
            if (!pulled) {
                pull();
            }
            pulled = false;
            return next;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        /**
         * Pulls the next element
         */
        private void pull() {
            int[] element = iter.next();
            while (element != null && !condition.holds(element)) {
                element = iter.next();
            }
            this.next = element;
            this.pulled = true;
        }
    }
    
    /**
     * A condition on an int[]
     * @author Fabian Prasser
     */
    static interface IntArrayCondition {
        public boolean holds(int[] array);
    }

    /**
     * Long iterator
     * @author prasser
     *
     */
    static interface TrieIterator {
        public int level();
        public int[] next();
    }
    
    /**
     * An iterator that wraps another iterator, which does not support hasNext() but requires
     * to iterate until <code>null</code> is returned.
     * 
     * @author Fabian Prasser
     */
    static class WrappedIntArrayIterator implements Iterator<int[]> {

        /** Iterator */
        private Iterator<int[]>     iter;
        /** Next element */
        private int[]               next   = null;
        /** Have we already pulled the next element */
        private boolean             pulled = true;
        /** Lattice*/
        private final Lattice<?, ?> lattice;
        
        /**
         * Constructs a new instance
         * @param iterator
         */
        WrappedIntArrayIterator(Lattice<?, ?> lattice, Iterator<int[]> iterator){
            this.iter = iterator;
            this.next = iter.next();
            this.lattice = lattice;
            if (this.lattice != null) {
                this.lattice.setUnmodified();
            }
        }
        
        @Override 
        public boolean hasNext() {
            if (lattice != null && lattice.isModified()) {
                throw new ConcurrentModificationException();
            }
            if (!pulled) {
                next = iter.next();
                pulled = true;
            }
            return next != null;
        }
        
        @Override
        public int[] next() {
            if (lattice != null && lattice.isModified()) {
                throw new ConcurrentModificationException();
            }
            if (!pulled) {
                next = iter.next();
            }
            pulled = false;
            return next;
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * An iterator that wraps another iterator, which does not support hasNext() but requires
     * to iterate until <code>null</code> is returned.
     * 
     * @author Fabian Prasser
     */
    static class WrappedLongIterator implements Iterator<Long> {

        /** Iterator */
        private Iterator<Long>     iter;
        /** Next element */
        private Long               next   = null;
        /** Have we already pulled the next element */
        private boolean             pulled = true;
        /** Lattice*/
        private final Lattice<?, ?> lattice;
        
        /**
         * Constructs a new instance
         * @param iterator
         */
        WrappedLongIterator(Lattice<?, ?> lattice, Iterator<Long> iterator){
            this.iter = iterator;
            this.next = iter.next();
            this.lattice = lattice;
            if (this.lattice != null) {
                this.lattice.setUnmodified();
            }
        }
        
        @Override 
        public boolean hasNext() {
            if (lattice != null && lattice.isModified()) {
                throw new ConcurrentModificationException();
            }
            if (!pulled) {
                next = iter.next();
                pulled = true;
            }
            return next != null;
        }
        
        @Override
        public Long next() {
            if (lattice != null && lattice.isModified()) {
                throw new ConcurrentModificationException();
            }
            if (!pulled) {
                next = iter.next();
            }
            pulled = false;
            return next;
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
