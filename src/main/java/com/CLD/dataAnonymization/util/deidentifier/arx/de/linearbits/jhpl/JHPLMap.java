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

/**
 * A hash map of <code>long</code> to <code>T</code>, implemented using open addressing with quadratic collision resolution.
 * 
 * This class is adopted from the HPPC project and was originally released under LGPL.
 */
class JHPLMap<T> {

    /**  A marker for an assigned slot in {@link #keys}, stored in {@link #states}.  */
    public final static byte  ASSIGNED            = 2;

    /** Default capacity. */
    public final static int   DEFAULT_CAPACITY    = 16;

    /** Default load factor. */
    public final static float DEFAULT_LOAD_FACTOR = 0.75f;

    /**  A marker for a deleted slot in {@link #keys}, stored in {@link #states}.  */
    public final static byte  DELETED             = 1;

    /** A marker for an empty slot in {@link #keys}, stored in {@link #states}.  */
    public final static byte  EMPTY               = 0;

    /** Minimum capacity for the map. */
    public final static int   MIN_CAPACITY        = 4;

    /** Cached number of assigned slots in {@link #states}. */
    public int                assigned;

    /** Cached number of deleted slots in {@link #states}. */
    public int                deleted;

    /** Hash-indexed array holding all keys.*/
    public long[]             keys;

    /** The load factor for this map (fraction of allocated or deleted slots before the buffers must be rehashed or reallocated). */
    public final float        loadFactor;

    /**  Each entry (slot) in the {@link #values} table has an associated state information ({@link #EMPTY}, {@link #ASSIGNED} or {@link #DELETED}). */
    public byte[]             states;

    /** Hash-indexed array holding all values associated to the keys stored in {@link #keys}. */
    public T[]            values;

    /** Cached capacity threshold at which we must resize the buffers.  */
    private int               resizeThreshold;

    /**
     * Constructs a new instance
     */
    JHPLMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.allocateBuffers(roundCapacity(DEFAULT_CAPACITY));
    }

    /**
     * Allocate internal buffers for a given capacity.
     * 
     * @param capacity New capacity (must be a power of two).
     */
    @SuppressWarnings("unchecked")
    private void allocateBuffers(int capacity) {
        this.keys = new long[capacity];
        this.values = (T[]) new Object[capacity];
        this.states = new byte[capacity];
        this.resizeThreshold = (int) (capacity * loadFactor);
    }

    /**
     * Expand the internal storage buffers (capacity) or rehash current
     * keys and values if there are a lot of deleted slots.
     */
    private void expandAndRehash() {
        final long[] oldKeys = this.keys;
        final T[] oldValues = this.values;
        final byte[] oldStates = this.states;

        if (assigned >= resizeThreshold) {
            allocateBuffers(nextCapacity(keys.length));
        } else {
            allocateBuffers(this.values.length);
        }

        /*
         * Rehash all assigned slots from the old hash table. Deleted
         * slots are discarded.
         */
        for (int i = 0; i < oldStates.length; i++) {
            if (oldStates[i] == ASSIGNED) {
                final int slot = slotFor(oldKeys[i]);
                keys[slot] = oldKeys[i];
                values[slot] = oldValues[i];
                states[slot] = ASSIGNED;

                /* Nullify while accessing these memory regions to use cpu cache. */
                oldValues[i] = null;
            }
        }

        /*
         * The number of assigned items does not change, the number of deleted
         * items is zero since we have resized.
         */
        deleted = 0;
    }

    /** Hashes an 8-byte sequence (Java long). */
    private int hash(long v) {
        final int M = 0x5bd1e995;
        final int R = 24;
        final int SEED = 0xdeadbeef;

        int k = (int) (v >>> 32);
        k *= M;
        k ^= k >>> R;
        k *= M;

        int h = SEED * M;
        h ^= k;

        k = (int) v;
        k *= M;
        k ^= k >>> R;
        k *= M;
        h *= M;
        h ^= k;

        h ^= h >>> 13;
        h *= M;
        h ^= h >>> 15;

        return h;
    }

    /**
     * Return the next possible capacity, counting from the current buffers'
     * size.
     */
    private int nextCapacity(int current) {
        assert current > 0 && Long.bitCount(current) == 1 : "Capacity must be a power of two.";
        assert ((current << 1) > 0) : "Maximum capacity exceeded (" + (0x80000000 >>> 1) + ").";
        if (current < MIN_CAPACITY / 2) current = MIN_CAPACITY / 2;
        return current << 1;
    }

    /** returns the next highest power of two, or the current value if it's already a power of two or zero*/
    private int nextHighestPowerOfTwo(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }

    /**
     * Round the capacity to the next allowed value. 
     */
    private int roundCapacity(int requestedCapacity) {
        // Maximum positive integer that is a power of two.
        if (requestedCapacity > (0x80000000 >>> 1)) return (0x80000000 >>> 1);
        return Math.max(MIN_CAPACITY, nextHighestPowerOfTwo(requestedCapacity));
    }

    /**
     * Lookup the slot index for <code>key</code> inside
     * {@link JHPLMap#values}. This method implements quadratic slot
     * lookup under the assumption that the number of slots (
     * <code>{@link JHPLMap#values}.length</code>) is a power of two.
     * Given this, the following formula yields a sequence of numbers with distinct values
     * between [0, slots - 1]. For a hash <code>h(k)</code> and the <code>i</code>-th
     * probe, where <code>i</code> is in <code>[0, slots - 1]</code>:
     * <pre>
     * h(k, i) = h(k) + (i + i^2) / 2   (mod slots)
     * </pre>
     * which can be rewritten recursively as:
     * <pre>
     * h(k, 0) = h(k),                (mod slots)
     * h(k, i + 1) = h(k, i) + i.     (mod slots)
     * </pre>
     * 
     * @see "http://en.wikipedia.org/wiki/Quadratic_probing"
     */
    private int slotFor(long key) {
        final int slots = states.length;
        final int bucketMask = (slots - 1);

        int slot = hash(key) & bucketMask;
        int i = 0;
        int deletedSlot = -1;

        while (true) {
            final int state = states[slot];

            if (state == JHPLMap.EMPTY) return deletedSlot != -1 ? deletedSlot : slot;
            if (state == JHPLMap.ASSIGNED && (keys[slot] == key)) { return slot; }
            if (state == JHPLMap.DELETED && deletedSlot < 0) deletedSlot = slot;

            slot = (slot + (++i)) & bucketMask;
        }
    }

    void clear() {
        assigned = deleted = 0;
        Arrays.fill(states, EMPTY);
        Arrays.fill(values, null); // Help the GC.
    }

    /**
     * Get
     * @param key
     * @return
     */
    T get(long key) {
        return values[slotFor(key)];
    }

    /**
     * Returns the memory consumption in bytes
     * @return
     */
    long getByteSize() {
        return this.keys.length * 8 + this.values.length * 8 + this.states.length;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Note that an empty container may still contain many deleted keys (that occupy buffer
     * space). Adding even a single element to such a container may cause rehashing.</p>
     */
    boolean isEmpty() {
        return size() == 0;
    }

    T put(long key, T value) {
        if (assigned + deleted >= resizeThreshold) expandAndRehash();

        final int slot = slotFor(key);
        final byte state = states[slot];

        if (state != ASSIGNED) assigned++;
        if (state == DELETED) deleted--;

        final T oldValue = values[slot];
        keys[slot] = key;
        values[slot] = value;
        states[slot] = ASSIGNED;

        return oldValue;
    }

    /**
     * Size
     * @return
     */
    int size() {
        return assigned;
    }
}
