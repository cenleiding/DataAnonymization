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

import java.util.Iterator;

import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.ConditionalIntArrayIterator;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.IntArrayCondition;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.jhpl.JHPLIterator.LongIterator;

/**
 * This class provides methods that may not be safe to use on very large lattices because their complexity grows
 * linearly with the size of the lattice.
 * 
 * @author Fabian Prasser
 */
public class JHPLUnsafe {
    
    /** The lattice*/
    private final Lattice<?, ?> lattice;
    
    /**
     * Constructs a new instance
     * @param lattice
     */
    JHPLUnsafe(Lattice<?, ?> lattice) {
        this.lattice = lattice;
    }

    /**
     * Enumerates all nodes regardless of whether or not they are stored in the lattice
     * @return
     */
    public Iterator<int[]> listAllNodes() {
        return lattice.listAllNodes();
    }

    /**
     * Enumerates all nodes on the given level regardless of whether or not they are stored in the lattice
     * @return
     */
    public Iterator<int[]> listAllNodes(int level) {
        return lattice.listAllNodes(level);
    }
    
    /**
     * Enumerates all nodes regardless of whether or not they are stored in the lattice
     * @return
     */
    public LongIterator listAllNodesAsIdentifiers() {
        return lattice.listAllNodesAsIdentifiers();
    }

    /**
     * Enumerates all nodes on the given level regardless of whether or not they are stored in the lattice
     * @return
     */
    public LongIterator listAllNodesAsIdentifiers(int level) {
        return lattice.listAllNodesAsIdentifiers(level);
    }
    
    /**
     * Lists all nodes not stored in the lattice
     * @return
     */
    public Iterator<int[]> listNodesNotStored() {
        return new ConditionalIntArrayIterator(listAllNodes(), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.contains(array);
            }
        });
    }

    /**
     * Lists all nodes not stored in the lattice on the given level
     * @param level
     * @return
     */
    public Iterator<int[]> listNodesNotStored(int level) {
        return new ConditionalIntArrayIterator(listAllNodes(level), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.contains(array);
            }
        });
    }

    /**
     * Lists all nodes without any property
     * @return
     */
    public Iterator<int[]> listNodesWithoutProperty() {
        return new ConditionalIntArrayIterator(listAllNodes(), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, lattice.nodes().getLevel(array)); // TODO: Use iterator that returns the level
            }
        });
    }

    /**
     * Lists all nodes without any property
     * @param level
     * @return
     */
    public Iterator<int[]> listNodesWithoutProperty(int level) {
        return new ConditionalIntArrayIterator(listAllNodes(level), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, lattice.nodes().getLevel(array)); // TODO: Use iterator that returns the level
            }
        });
    }


    /**
     * Lists all nodes without the given property
     * @param property
     * @return
     */
    public Iterator<int[]> listNodesWithoutProperty(final PredictiveProperty property) {
        return new ConditionalIntArrayIterator(listAllNodes(), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, lattice.nodes().getLevel(array), property); // TODO: Use iterator that returns the level
            }
        });
    }

    /**
     * Lists all nodes without the given property
     * @param property
     * @param level
     * @return
     */
    public Iterator<int[]> listNodesWithoutProperty(final PredictiveProperty property, int level) {
        return new ConditionalIntArrayIterator(listAllNodes(level), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, lattice.nodes().getLevel(array), property); // TODO: Use iterator that returns the level
            }
        });
    }

    
    /**
     * Lists all nodes without any property and which have not been stored in this lattice
     * @return
     */
    public Iterator<int[]> listNodesWithoutPropertyAndNotStored() {
        return new ConditionalIntArrayIterator(listAllNodes(), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, lattice.nodes().getLevel(array)) && !lattice.contains(array); // TODO: Use iterator that returns the level
            }
        });
    }

    /**
     * Lists all nodes without any property and which have not been stored in this lattice
     * @param level
     * @return
     */
    public Iterator<int[]> listNodesWithoutPropertyAndNotStored(int level) {
        return new ConditionalIntArrayIterator(listAllNodes(level), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return !lattice.hasProperty(array, lattice.nodes().getLevel(array)) && !lattice.contains(array); // TODO: Use iterator that returns the level
            }
        });
    }

    /**
     * Lists all nodes with any property
     * @return
     */
    public Iterator<int[]> listNodesWithProperty() {
        return new ConditionalIntArrayIterator(listAllNodes(), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, lattice.nodes().getLevel(array)); // TODO: Use iterator that returns the level
            }
        });
    }

    /**
     * Lists all nodes with any property
     * @param level
     * @return
     */
    public Iterator<int[]> listNodesWithProperty(int level) {
        return new ConditionalIntArrayIterator(listAllNodes(level), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, lattice.nodes().getLevel(array)); // TODO: Use iterator that returns the level
            }
        });
    }
    
    /**
     * Lists all nodes with the given property
     * @param property
     * @return
     */
    public Iterator<int[]> listNodesWithProperty(final PredictiveProperty property) {
        return new ConditionalIntArrayIterator(listAllNodes(), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, lattice.nodes().getLevel(array), property); // TODO: Use iterator that returns the level
            }
        });
    }

    /**
     * Lists all nodes with the given property
     * @param property
     * @param level
     * @return
     */
    public Iterator<int[]> listNodesWithProperty(final PredictiveProperty property, int level) {
        return new ConditionalIntArrayIterator(listAllNodes(level), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, lattice.nodes().getLevel(array), property); // TODO: Use iterator that returns the level
            }
        });
    }


    /**
     * Lists all nodes with any property or which have been stored in the lattice
     * @return
     */
    public Iterator<int[]> listNodesWithPropertyOrStored() {
        return new ConditionalIntArrayIterator(listAllNodes(), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, lattice.nodes().getLevel(array)) || lattice.contains(array); // TODO: Use iterator that returns the level
            }
        });
    }

    /**
     * Lists all nodes with any property or which have been stored in the lattice
     * @param level
     * @return
     */
    public Iterator<int[]> listNodesWithPropertyOrStored(int level) {
        return new ConditionalIntArrayIterator(listAllNodes(level), new IntArrayCondition(){
            public boolean holds(int[] array) {
                return lattice.hasProperty(array, lattice.nodes().getLevel(array)) || lattice.contains(array); // TODO: Use iterator that returns the level
            }
        });
    }

    /**
     * Materializes the whole lattice. The result of this method is similar to calling put() for 
     * each node returned by listAllNodes(). It is here for your convenience, only. 
     */
    public void materialize() {
        lattice.materialize();
    }
}
