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
 * An accessor for map data elements
 * 
 * @author Fabian Prasser
 * 
 * @param <T>
 */
public class MapAccessor<T> implements IAccessor<Map<String, T>> {

    /** The datatypes */
    private Map<String, DataType<?>> map = new HashMap<String, DataType<?>>();

    /**
     * Constructor without data types. Everything is treated as a string
     */
    public MapAccessor() {
        // Empty by design
    }

    /**
     * Constructor with data types
     * 
     * @param map
     */
    public MapAccessor(Map<String, DataType<?>> map) {
        this.map = map;
    }

    @Override
    public boolean exists(String context) {
        return map.containsKey(context);
    }

    @Override
    public DataType<?> getType(String context) {
        return map.get(context);
    }

    @Override
    public Object getValue(Map<String, T> object, String context) {
        return object.get(context);
    }

    @Override
    public boolean isDataTypesSupported() {
        return !map.isEmpty();
    }

    @Override
    public boolean isExistanceSupported() {
        return !map.isEmpty();
    }
}
