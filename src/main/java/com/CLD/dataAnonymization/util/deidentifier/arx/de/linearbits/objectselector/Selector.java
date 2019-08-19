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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.ops.AbstractOperator;

/**
 * The object selector
 * @author Fabian Prasser
 *
 * @param <T>
 */
public class Selector<T> {

    /** Root op */
    private AbstractOperator<T> root = null;

    /**
     * Creates a new selector
     * 
     * @param root
     * @param element
     * @param schema
     */
    protected Selector(AbstractOperator<T> root) {
        this.root = root;
    }

    /**
     * Call this for each element you want to check
     * 
     * @param element
     * @return
     */
    public boolean isSelected(T element) {
        return root.eval(element);
    }

    /**
     * Removes all selected elements. Returns a new collection.
     * 
     * @param input
     * @return
     */
    public Collection<T> remove(Collection<T> input) {
        List<T> result = new ArrayList<T>();
        for (T t : input){
            if (!isSelected(t)) result.add(t);
        }
        return result;
    }

    /**
     * Removes all selected elements. Returns a new list.
     * 
     * @param input
     * @return
     */
    public List<T> remove(List<T> input) {
        List<T> result = new ArrayList<T>();
        for (T t : input){
            if (!isSelected(t)) result.add(t);
        }
        return result;
    }

    /**
     * Removes all selected elements. Returns a new set.
     * 
     * @param input
     * @return
     */
    public Set<T> remove(Set<T> input) {
        Set<T> result = new HashSet<T>();
        for (T t : input){
            if (!isSelected(t)) result.add(t);
        }
        return result;
    }

    /**
     * Removes all selected elements. Returns a new array.
     * 
     * @param input
     * @return
     */
    public T[] remove(T[] input) {
        List<T> list = remove(Arrays.asList(input));
        T[] result = input.clone();
        for (int i=0; i<list.size(); i++){
            result[i] = list.get(i);
        }
        return Arrays.copyOf(result, list.size());
    }

    /**
     * Returns the subset of all selected elements. Returns a new collection.
     * 
     * @param input
     * @return
     */
    public Collection<T> retain(Collection<T> input) {
        List<T> result = new ArrayList<T>();
        for (T t : input){
            if (isSelected(t)) result.add(t);
        }
        return result;
    }

    /**
     * Returns the subset of all selected elements. Returns a new list.
     * 
     * @param input
     * @return
     */
    public List<T> retain(List<T> input) {
        List<T> result = new ArrayList<T>();
        for (T t : input){
            if (isSelected(t)) result.add(t);
        }
        return result;
    }

    /**
     * Returns the subset of all selected elements. Returns a new set.
     * 
     * @param input
     * @return
     */
    public Set<T> retain(Set<T> input) {
        Set<T> result = new HashSet<T>();
        for (T t : input){
            if (isSelected(t)) result.add(t);
        }
        return result;
    }

    /**
     * Returns the subset of all selected elements. Returns a new array.
     * 
     * @param input
     * @return
     */
    public T[] retain(T[] input) {
        List<T> list = retain(Arrays.asList(input));
        T[] result = input.clone();
        for (int i=0; i<list.size(); i++){
            result[i] = list.get(i);
        }
        return Arrays.copyOf(result, list.size());
    }

    /**
     * Returns a string representation of the operator tree
     */
    public String toString() {
        StringBuffer b = new StringBuffer();
        root.toString(b, "");
        return b.toString();
    }
}
