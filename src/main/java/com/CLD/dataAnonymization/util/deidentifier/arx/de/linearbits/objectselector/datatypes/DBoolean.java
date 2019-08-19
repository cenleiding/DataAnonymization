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

package com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.datatypes;

/**
 * Boolean data type
 * @author Fabian Prasser
 */
public class DBoolean extends DataType<Boolean> {

    /**
     * Creates a new boolean data type
     */
    protected DBoolean(){
        // Empty by design
    }

    @Override
    public Boolean fromObject(Object object) {
        if (object instanceof Boolean) return (Boolean)object;
        return fromString(String.valueOf(object));
    }

    @Override
    public Boolean fromString(String value) {
        return Boolean.valueOf(value);
    }
}
