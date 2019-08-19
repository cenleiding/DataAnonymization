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
 * This class implements a property that is predictive within the lattice. This means that
 * properties are inherited to predecessors or successors of an element in the lattice. Properties can be 
 * predictive either for all (direct and indirect) successors or for all (direct and indirect) predecessors or 
 * for both (direct and indirect) successors and (direct and indirect) predecessors of a given element. In 
 * addition to predictive properties, data can be associated to individual nodes.
 * 
 * @author Fabian Prasser
 */
public class PredictiveProperty {

    /**
     * Direction of prediction
     * 
     * @author Fabian Prasser
     */
    public static enum Direction {
        /** Predictive for no element*/
        NONE,
        /** Predictive for the element and all of its (direct and indirect) successors*/
        BOTH,
        /** Predictive for the element and all of its (direct and indirect) predecessors*/
        DOWN,
        /** Predictive for the element and all of its (direct and indirect) successors and (direct and indirect) predecessors*/
        UP
    }

    /** Direction of prediction*/
    private final Direction direction;
    /** Label*/
    private final String    label;

    /**
     * Constructor
     * @param direction
     */
    public PredictiveProperty(Direction direction) {
        this.label = "Property#" + this.hashCode();
        this.direction = direction;
    }
    /**
     * Constructor
     * @param label
     * @param direction
     */
    public PredictiveProperty(String label, Direction direction) {
        this.direction = direction;
        this.label = label;
    }
    
    /**
     * Returns the direction of prediction
     * @return
     */
    public Direction getDirection() {
        return this.direction;
    }
    
    /**
     * Returns the label
     * @return
     */
    public String getLabel() {
        return this.label;
    }
}
