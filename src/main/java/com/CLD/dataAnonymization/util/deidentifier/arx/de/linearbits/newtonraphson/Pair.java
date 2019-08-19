/*
 * Copyright 2015 Fabian Prasser
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
package com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.newtonraphson;

/**
 * A pair
 * @author Fabian Prasser
 * 
 * @param <T>
 * @param <U>
 */
public class Pair<T, U> {
    
    /** First element*/
    public final T first;
    /** Second element*/
    public final U second;
    
    /**
     * Creates a new instance
     * @param first
     * @param second
     */
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }
}
