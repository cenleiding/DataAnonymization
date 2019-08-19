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
 * A Tokenizer for queries
 * 
 * @author Fabian Prasser
 */
public class SelectorTokenizer<T> {

    /** The callback*/
    private ICallback          callback = null;

    /**
     * Creates a new tokenizer
     */
    public SelectorTokenizer(ICallback callback) {
        this.callback = callback;
    }

    /**
     * Starts the tokenization process
     * @param query
     */
    public void tokenize(String query){

        int quote = -1;
        boolean first = true;
        char[] data = query.toCharArray();
        for (int i=0; i<data.length; i++){
            if (data[i]=='\\'){
                // Skip next
                i++; 
            } else if (data[i]=='\''){
                // Start quote
                if (quote == -1){
                    quote = i; 
                    // End quote
                } else {
                    if (first) {
                        callback.field(quote, i-quote+1);
                    } else {
                        callback.value(quote, i-quote+1);
                    }
                    quote = -1;
                    first = !first;
                }
            } else if (quote == -1 && data[i]=='(') {
                callback.begin(i);
            } else if (quote == -1 && data[i]==')') {
                callback.end(i);
            } else if (quote == -1 && i<data.length-2 && data[i]=='a' && data[i+1]=='n' && data[i+2]=='d') {
                callback.and(i, 3);
                i+=2;
            } else if (quote == -1 && i<data.length-1 && data[i]=='o' && data[i+1]=='r') {
                callback.or(i, 2);
                i++;
            } else if ((quote == -1 && i<data.length-1 && data[i]=='<' && data[i+1]=='>')) {
                callback.neq(i, 2);
                i++;
            } else if ((quote == -1 && i<data.length-1 && data[i]=='<' && data[i+1]=='=')) {
                callback.leq(i, 2);
                i++;
            } else if ((quote == -1 && i<data.length-1 && data[i]=='>' && data[i+1]=='=')) {
                callback.geq(i, 2);
                i++;
            } else if (quote == -1 && data[i]=='=') {
                callback.equals(i);
            } else if (quote == -1 && data[i]=='<') {
                callback.less(i);
            } else if (quote == -1 && data[i]=='>') {
                callback.greater(i);
            } else if (quote == -1 && (data[i]!=' ' && data[i]!='\t' && data[i]!='\n')){
                callback.invalid(i);
            }

            if (i>=data.length) break;
        }
        callback.check();
    }
}
