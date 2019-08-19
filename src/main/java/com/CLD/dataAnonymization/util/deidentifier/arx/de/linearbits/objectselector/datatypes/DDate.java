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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date data type
 * @author Fabian Prasser
 */
public class DDate extends DataType<Date> {

    /** The formatter*/
    private final SimpleDateFormat formatter;

    /**
     * Create a date with a format string. Format strings must be valid formats
     * for <code>SimpleDateFormat</code>.
     * @param format
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
     */
    protected DDate(String format){
        formatter = new SimpleDateFormat(format);
    }

    @Override
    public Date fromObject(Object object) {
        if (object instanceof Date) return (Date)object;
        return fromString(String.valueOf(object));
    }


    @Override
    public Date fromString(String value) {
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
