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

package com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector;

import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.datatypes.DataType;

/**
 * An interface for accessing data elements
 * 
 * @author Fabian Prasser
 * @param <T>
 */
public interface IAccessor<T> {

    /**
     * Returns whether the given context exists
     * 
     * @param context
     * @return
     */
    public boolean exists(String context);

    /**
     * Returns the datatype of the given context
     * 
     * @param context
     * @return
     */
    public DataType<?> getType(String context);

    /**
     * Return the value
     * 
     * @param context
     * @return
     */
    public Object getValue(T object, String context);

    /**
     * Is the getType() method implemented?
     * 
     * @return
     */
    public boolean isDataTypesSupported();

    /**
     * Is the exists() method implemented?
     * 
     * @return
     */
    public boolean isExistanceSupported();
}
