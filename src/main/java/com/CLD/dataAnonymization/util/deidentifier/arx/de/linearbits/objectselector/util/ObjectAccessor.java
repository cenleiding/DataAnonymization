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

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * An accessor for object data elements
 * 
 * @author Fabian Prasser
 * 
 * @param <T>
 */
public class ObjectAccessor<T> implements IAccessor<T> {

    /** The data types */
    private Map<String, DataType<?>> dt = new HashMap<String, DataType<?>>();

    /**
     * Constructor
     * 
     * @param clazz
     */
    public ObjectAccessor(Class<T> clazz) {

        for (Field field : clazz.getDeclaredFields()) {
            String name = field.getName();
            Class<?> type = field.getType();
            if (type.equals(Integer.class)) {
                dt.put(name, DataType.NUMERIC);
            } else if (type.equals(Float.class)) {
                dt.put(name, DataType.NUMERIC);
            } else if (type.equals(Short.class)) {
                dt.put(name, DataType.NUMERIC);
            } else if (type.equals(Character.class)) {
                dt.put(name, DataType.NUMERIC);
            } else if (type.equals(Double.class)) {
                dt.put(name, DataType.NUMERIC);
            } else if (type.equals(String.class)) {
                dt.put(name, DataType.STRING);
            } else if (type.equals(Boolean.class)) {
                dt.put(name, DataType.BOOLEAN);
            } else if (type.equals(Date.class)) {
                dt.put(name, DataType.DATE);
            } else if (type.equals(Integer.TYPE)) {
                dt.put(name, DataType.NUMERIC);
            } else if (type.equals(Float.TYPE)) {
                dt.put(name, DataType.NUMERIC);
            } else if (type.equals(Short.TYPE)) {
                dt.put(name, DataType.NUMERIC);
            } else if (type.equals(Character.TYPE)) {
                dt.put(name, DataType.NUMERIC);
            } else if (type.equals(Double.TYPE)) {
                dt.put(name, DataType.NUMERIC);
            } else if (type.equals(Boolean.TYPE)) {
                dt.put(name, DataType.BOOLEAN);
            }
        }
    }

    @Override
    public boolean exists(String context) {
        return dt.containsKey(context);
    }

    @Override
    public DataType<?> getType(String context) {
        return dt.get(context);
    }

    @Override
    public Object getValue(T object, String context) {
        try {
            return object.getClass().getDeclaredField(context).get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isDataTypesSupported() {
        return true;
    }

    @Override
    public boolean isExistanceSupported() {
        return true;
    }
}
