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
 * Class for parentheses
 * @author Fabian Prasser
 */
public class ParenthesisOperator<T> extends AbstractOperator<T>{

    /** Open or close parentheses*/
    protected final boolean begin;

    /**
     * Constructor
     * @param begin
     */
    public ParenthesisOperator(IAccessor<T> accessor, boolean begin) {
        super(accessor, 0);
        this.begin = begin;
    }

    @Override
    public boolean eval(T object) {
        throw new UnsupportedOperationException("Parentheses cannot be evaluated");
    }

    /** Open or close parentheses*/
    public boolean isBegin() {
        return begin;
    }

    @Override
    public void toString(StringBuffer buffer, String prefix) {
        if (begin) {
            buffer.append(prefix).append("(");
        } else {
            buffer.append(prefix).append(")");			
        }
    }

    @Override
    public String toString() {
        if (begin) return "(";
        else return ")";
    }
}