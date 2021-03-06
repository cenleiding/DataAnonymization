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

import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.datatypes.DataType;

/**
 * An accessor arrays of booleans
 * 
 * @author Fabian Prasser
 */
public class BooleanArrayAccessor extends PrimitiveArrayAccessor<boolean[]>{

    /**
     * Constructor without data types. Everything is treated as a string
     * 
     * @param header
     */
    public BooleanArrayAccessor(String[] header) {
        super(header, DataType.BOOLEAN);
    }

    /**
     * Constructor with data types
     * 
     * @param header
     * @param datatypes
     */
    public BooleanArrayAccessor(String[] header, DataType<?>[] datatypes) {
        super(header, datatypes);
    }

    @Override
    public Object getValue(boolean[] object, String context) {
        return object[super.map.get(context)];
    }

}
