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
 * An accessor arrays of primitives
 * 
 * @author Fabian Prasser
 * 
 * @param <T>
 */
public abstract class PrimitiveArrayAccessor<T> implements IAccessor<T> {

    /** Indexes */
    protected Map<String, Integer>     map = new HashMap<String, Integer>();

    /** Datatypes */
    protected Map<String, DataType<?>> dt  = new HashMap<String, DataType<?>>();

    /**
     * Constructor without data types. Everything is treated as a string
     * 
     * @param header
     */
    public PrimitiveArrayAccessor(String[] header, DataType<?> type) {
        for (int i = 0; i < header.length; i++) {
            map.put(header[i], i);
            dt.put(header[i], type);
        }
    }

    /**
     * Constructor with data types
     * 
     * @param header
     * @param datatypes
     */
    public PrimitiveArrayAccessor(String[] header, DataType<?>[] datatypes) {
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
    public boolean isDataTypesSupported() {
        return !dt.isEmpty();
    }

    @Override
    public boolean isExistanceSupported() {
        return true;
    }
}
