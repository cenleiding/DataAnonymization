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
 * A base class for data types
 * @author Fabian Prasser
 */
public abstract class DataType<T> {

	/** String */
	public static final DString STRING = new DString();
	/** Numeric */
	public static final DNumeric NUMERIC = new DNumeric();
	/** Date with format "yyyy-MM-dd" */
	public static final DDate DATE = new DDate("yyyy-MM-dd");
	/** Boolean */
	public static final DBoolean BOOLEAN = new DBoolean();

	/** Date with given format */
	public static final DDate DATE(String format) {
		return new DDate(format);
	}

	/** Numeric with given format */
	public static final DNumeric NUMERIC(String format) {
		return new DNumeric(format);
	}

    /**
     * Converts an object
     * @param object
     * @return
     */
    public abstract T fromObject(Object object);

    /**
     * Converts a string
     * @param string
     * @return
     */
    public abstract T fromString(String string);
}
