/*
 * ObjectSelector - Object selection library for Java
 * Copyright (C) 2013 - 2016 Fabian Prasser
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.ops;


import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.IAccessor;

/**
 * Abstract base class for operators
 * @author Fabian Prasser
 */
public abstract class AbstractOperator<T> {

    /** Number of operands*/
    protected final int operands;
    /** The associated accessor*/
    protected final IAccessor<T> accessor;

    /**
     * Creates a new instance
     * @param accessor
     * @param operands
     */
    protected AbstractOperator(IAccessor<T> accessor, int operands){
        this.operands = operands;
        this.accessor = accessor;
    }

    /**
     * Evaluate the given element
     * @param object
     * @return
     */
    public abstract boolean eval(T object);

    /**
     * Returns the number of operands
     * @return
     */
    public int getNumOperands(){
        return this.operands;
    }

    /**
     * Writes a string representation of the operator tree to the buffer
     * @param buffer
     * @param prefix
     */
    public abstract void toString(StringBuffer buffer, String prefix);
}
