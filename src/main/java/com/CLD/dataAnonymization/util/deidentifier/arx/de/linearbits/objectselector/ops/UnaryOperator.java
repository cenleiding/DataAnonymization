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
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.datatypes.DataType;

import java.util.Date;

/**
 * Abstract base class for unary operators that check actual data items
 * @author Fabian Prasser
 */
public abstract class UnaryOperator<T> extends AbstractOperator<T>{

    /** The context*/
    protected final String context;

    /** The data type*/
    private final DataType<?> type;

    /** For conversion to string*/
    private final String label;

    /**
     * Constructor
     * @param context
     */
    public UnaryOperator(IAccessor<T> accessor, String context, String label) {
        super(accessor, 1);

        if (context == null){
            throw new IllegalArgumentException("No context specified");
        }

        this.context = context;
        this.label = label;

        if (accessor.isDataTypesSupported()) {
            this.type = accessor.getType(context);
        } else {
            this.type = null;
        }
    }

    /**
     * Returns the data item as a boolean
     * @param element
     * @return
     */
    public Boolean getBoolean(T element) {
        if (type != null && type != DataType.BOOLEAN) {
            throw new RuntimeException("Type mismatch for field '"+context+"'. Cannot convert "+type.getClass().getSimpleName()+" to Boolean");
        } else if (type != null) {
            return (Boolean)type.fromObject(accessor.getValue(element, context));
        } else {
            return Boolean.valueOf(String.valueOf(accessor.getValue(element, context)));
        }
    }

    /**
     * Returns the context
     * @return
     */
    public String getContext(){
        return context;
    }

    /**
     * Returns the data item as a date
     * @param element
     * @return
     */
    public Date getDate(T element) {
        if (type != null && type != DataType.DATE) {
            throw new RuntimeException("Type mismatch for field '"+context+"'. Cannot convert "+type.getClass().getSimpleName()+" to Date");
        } else if (type != null) {
            return (Date)type.fromObject(accessor.getValue(element, context));
        } else {
            return DataType.DATE.fromString(String.valueOf(accessor.getValue(element, context)));
        }
    }

    /**
     * Returns the data item as a double
     * @param element
     * @return
     */
    public double getDouble(T element) {
        if (type != null && type != DataType.NUMERIC) {
            throw new RuntimeException("Type mismatch for field '"+context+"'. Cannot convert "+type.getClass().getSimpleName()+" to Numeric");
        } else if (type != null) {
            return (Double)type.fromObject(accessor.getValue(element, context));
        } else {
            return Double.valueOf(String.valueOf(accessor.getValue(element, context)));
        }
    }

    /**
     * Returns the data item as a string
     * @param element
     * @return
     */
    public String getString(T element) {
        if (type != null && type != DataType.STRING) {
            throw new RuntimeException("Type mismatch for field '"+context+"'. Cannot convert "+type.getClass().getSimpleName()+" to String");
        } else if (type != null) {
            return (String)type.fromObject(accessor.getValue(element, context));
        } else {
            return String.valueOf(accessor.getValue(element, context));
        }
    }

    @Override
    public void toString(StringBuffer buffer, String prefix) {
        buffer.append(prefix).append(context).append(label);	
    }

    @Override
    public String toString() {
        return label;
    }
}