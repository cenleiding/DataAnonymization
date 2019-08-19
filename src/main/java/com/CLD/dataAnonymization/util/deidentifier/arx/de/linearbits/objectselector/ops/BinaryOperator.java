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
 * Abstract base class for binary operators
 * @author Fabian Prasser
 */
public abstract class BinaryOperator<T> extends AbstractOperator<T>{

    /** Left child*/
    protected AbstractOperator<T> left;
    /** Right child*/
    protected AbstractOperator<T> right;
    /** For conversion to string*/
    private final String label;

    /**
     * Constructor
     */
    public BinaryOperator(IAccessor<T> accessor, String label) {
        super(accessor, 2);
        this.label = label;
    }

    /** Returns the left operator*/
    public AbstractOperator<T> getLeft() {
        return left;
    }

    /** Returns the right operator*/
    public AbstractOperator<T> getRight() {
        return right;
    }

    /** Sets the left operator*/
    public void setLeft(AbstractOperator<T> left) {
        this.left = left;
    }

    /** Sets the right operator*/
    public void setRight(AbstractOperator<T> right) {
        this.right = right;
    }

    @Override
    public void toString(StringBuffer buffer, String prefix) {
        buffer.append(prefix);
        buffer.append(label);
        buffer.append("\n");
        left.toString(buffer, prefix + "   ");
        buffer.append("\n");
        right.toString(buffer, prefix + "   ");	
    }

    @Override
    public String toString() {
        return label;
    }
}
