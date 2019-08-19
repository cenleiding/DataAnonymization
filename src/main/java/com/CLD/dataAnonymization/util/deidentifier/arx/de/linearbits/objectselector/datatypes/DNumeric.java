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

import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * Double data type
 * @author Fabian Prasser
 */
public class DNumeric extends DataType<Double>{

	/** Format, if any*/
	private String string;
	/** Format, if any*/
	private DecimalFormat format;
	
    /**
     * Creates a new numeric data type
     */
    protected DNumeric(){
    	this.string = null;
    	this.format = null;
    }
    
    /**
     * Create a numeric with a format string. Format strings must be valid formats
     * for <code>DecimalFormat</code>.
     * @param format
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/text/DecimalFormat.html">DecimalFormat</a>
     */
    protected DNumeric(String format){
    	if (format == null){
    		this.string = null;
    		this.format = null;	
    	} else {
    		this.string = format;
    		this.format = new DecimalFormat(string);
    	}
    }

    @Override
    public Double fromObject(Object object) {
        if (object instanceof Double) return (Double)object;
        else if (object instanceof Character) return Double.valueOf((Character)object);
        else if (object instanceof Short) return Double.valueOf((Short)object);
        else if (object instanceof Integer) return Double.valueOf((Integer)object);
        else if (object instanceof Float) return Double.valueOf((Float)object);
        return fromString(String.valueOf(object));
    }

    @Override
    public Double fromString(String value) {
        try {
			return format == null ? Double.valueOf(value) : format.parse(value).doubleValue();
		} catch (NumberFormatException | ParseException e) {
			throw new RuntimeException(e);
		}
    }
}
