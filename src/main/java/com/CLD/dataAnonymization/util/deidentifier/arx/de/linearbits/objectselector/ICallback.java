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

/**
 * A callback for the tokenizer
 * @author Fabian Prasser
 */
public interface ICallback {

    /** 
     * Logical and
     * 
     * @param start
     * @param length
     */
    void and(int start, int length);

    /**
     * Open parenthesis
     * @param start
     */
    void begin(int start);

    /**
     * Final check after the tokenization process
     */
    void check();

    /**
     * Closing parenthesis
     * @param start
     */
    void end(int start);

    /**
     * Equals
     * @param start
     */
    void equals(int start);

    /**
     * Field
     * @param start
     * @param length
     */
    void field(int start, int length);

    /**
     * Greater than or equals
     * @param start
     * @param length
     */
    void geq(int start, int length);

    /**
     * Greater than
     * @param start
     */
    void greater(int start);

    /**
     * Invalid expression
     * @param start
     */
    void invalid(int start);

    /**
     * Less than or equals
     * @param start
     * @param length
     */
    void leq(int start, int length);

    /**
     * Less than
     * @param start
     */
    void less(int start);

    /**
     * Not equals
     * @param start
     */
    void neq(int start, int length);

    /**
     * Logical or
     * @param start
     * @param length
     */
    void or(int start, int length);
    
    /**
     * Value
     * @param start
     * @param length
     */
    void value(int start, int length);
}

