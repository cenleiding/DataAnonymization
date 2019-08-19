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
 * This class implements a map from elements to data
 * 
 * @author Fabian Prasser
 *
 * @param <T>
 * @param <U>
 */
class JHPLData<T, U> {

    /** Map holding data objects */
    private final JHPLMap<U>   map;
    /** Space */
    private final JHPLSpace<T> space;

    /**
     * Constructs a new instance
     * @param elements
     */
    @SuppressWarnings("unchecked")
    JHPLData(JHPLSpace<T> space, T[]... elements) {
        this.map = new JHPLMap<U>();
        this.space = space;
    }
    
    /**
     * Retrieve data
     * @param node
     * @return
     */
    U get(int[] node) {
        return get(space.toId(node));
    }
    
    /**
     * Retrieve data for element with the given id
     * @param id
     * @return
     */
    U get(long id) {
        return map.get(id);
    }

    /**
     * Returns the memory consumption in bytes
     * @return
     */
    long getByteSize() {
        return this.map.getByteSize();
    }

    /**
     * Puts data
     * @param node
     * @param data
     */
    void put(int[] node, U data) {
        put(space.toId(node), data);
    }

    /**
     * Puts data
     * @param id
     * @param data
     */
    void put(long id, U data) {
        map.put(id, data);
    }
}
