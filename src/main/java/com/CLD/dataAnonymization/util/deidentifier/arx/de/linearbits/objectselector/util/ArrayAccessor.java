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

package com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.util;

import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.IAccessor;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.datatypes.DataType;

import java.util.HashMap;
import java.util.Map;

/**
 * An accessor array data elements
 * 
 * @author Fabian Prasser
 * 
 * @param <T>
 */
public class ArrayAccessor<T> implements IAccessor<T[]> {

    /** Indexes */
    private Map<String, Integer>     map = new HashMap<String, Integer>();

    /** Datatypes */
    private Map<String, DataType<?>> dt  = new HashMap<String, DataType<?>>();

    /**
     * Constructor without data types. Everything is treated as a string
     * 
     * @param header
     */
    public ArrayAccessor(String[] header) {
        for (int i = 0; i < header.length; i++) {
            map.put(header[i], i);
        }
    }

    /**
     * Constructor with data types
     * 
     * @param header
     * @param datatypes
     */
    public ArrayAccessor(String[] header, DataType<?>[] datatypes) {
        for (int i = 0; i < header.length; i++) {
            map.put(header[i], i);
            dt.put(header[i], datatypes[i]);
        }
    }

    @Override
    public boolean exists(String context) {
        return map.containsKey(context);
    }

    @Override
    public DataType<?> getType(String context) {
        return dt.get(context);
    }

    @Override
    public Object getValue(T[] object, String context) {
        return object[map.get(context)];
    }

    @Override
    public boolean isDataTypesSupported() {
        return !dt.isEmpty();
    }

    @Override
    public boolean isExistanceSupported() {
        return true;
    }
}
