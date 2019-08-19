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

import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.ops.AbstractOperator;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.ops.BinaryOperator;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.ops.ParenthesisOperator;
import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.ops.UnaryOperator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A builder for object selectors
 * 
 * @author Fabian Prasser
 */
public class SelectorBuilder<T> {

    /** The list of operators defined via the builder pattern */
    private final List<AbstractOperator<T>> operators = new ArrayList<AbstractOperator<T>>();

    /** The current context (field) */
    private String                          context   = null;

    /** Current schema */
    private final IAccessor<T>              accessor;

    /**
     * Creates a new selector
     * @param accessor
     */
    public SelectorBuilder(IAccessor<T> accessor) {
        this.accessor = accessor;
    }

    /**
     * Creates a new selector with the given schema and the given query
     * @param accessor
     * @param query
     */
    public SelectorBuilder (IAccessor<T> accessor, String query) throws ParseException {
        this.accessor = accessor;
        SelectorTokenizer<T> tokenizer = new SelectorTokenizer<T>(new SelectorBuilderCallback<T>(accessor, this, query));
        try {
            tokenizer.tokenize(query);
        } catch (Exception e){
            throw new ParseException(e.getMessage(), 0);
        }
    }

    /**
     * Logical and operator
     * @return
     */
    public SelectorBuilder<T> and(){
        operators.add(new BinaryOperator<T>(accessor, "and"){
            @Override
            public boolean eval(T element) {
                return left.eval(element) && right.eval(element);
            }
        });
        return this;
    }

    /**
     * Parentheses
     * @return
     */
    public SelectorBuilder<T> begin(){
        operators.add(new ParenthesisOperator<T>(accessor, true));
        return this;
    }

    /**
     * Compiles the selector. Call this before starting the selection process
     * @return
     */
    public Selector<T> build() throws ParseException {
        Parser<T> p = new Parser<T>(operators);
        p.parse();
        p.getRoot();
        return new Selector<T>(p.getRoot());
    }

    /**
     * Parentheses
     * @return
     */
    public SelectorBuilder<T> end(){
        operators.add(new ParenthesisOperator<T>(accessor, false));
        return this;
    }

    /**
     * Boolean not equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> neq(final Boolean val){
        operators.add(new UnaryOperator<T>(accessor, context, "<>[boolean]"+val){
            @Override
            public boolean eval(T element) {
                return getBoolean(element).compareTo(val) != 0;
            }
        });
        return this;
    }

    /**
     * Not equals for Dates
     * @param val
     * @return
     */
    public SelectorBuilder<T> neq(final Date val){
        operators.add(new UnaryOperator<T>(accessor, context, "<>[date]"+val){
            @Override
            public boolean eval(T element) {
                return getDate(element).compareTo(val) != 0;
            }
        });
        return this;
    }

    /**
     * Numeric not equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> neq(final double val){
        operators.add(new UnaryOperator<T>(accessor, context, "<>[numeric]"+val){
            @Override
            public boolean eval(T element) {
                return getDouble(element) != val;
            }
        });
        return this;
    }

    /**
     * Lexicographic not equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> neq(final String val){
        operators.add(new UnaryOperator<T>(accessor, context, "<>[string]"+val){
            @Override
            public boolean eval(T element) {
                return getString(element).compareTo(val) != 0;
            }
        });
        return this;
    }

    /**
     * Boolean equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> equals(final Boolean val){
        operators.add(new UnaryOperator<T>(accessor, context, "=[boolean]"+val){
            @Override
            public boolean eval(T element) {
                return getBoolean(element).compareTo(val) == 0;
            }
        });
        return this;
    }

    /**
     * Equals for Dates
     * @param val
     * @return
     */
    public SelectorBuilder<T> equals(final Date val){
        operators.add(new UnaryOperator<T>(accessor, context, "=[date]"+val){
            @Override
            public boolean eval(T element) {
                return getDate(element).compareTo(val) == 0;
            }
        });
        return this;
    }

    /**
     * Numeric equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> equals(final double val){
        operators.add(new UnaryOperator<T>(accessor, context, "=[numeric]"+val){
            @Override
            public boolean eval(T element) {
                return getDouble(element) == val;
            }
        });
        return this;
    }

    /**
     * Lexicographic equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> equals(final String val){
        operators.add(new UnaryOperator<T>(accessor, context, "=[string]"+val){
            @Override
            public boolean eval(T element) {
                return getString(element).compareTo(val) == 0;
            }
        });
        return this;
    }

    /**
     * Matches regular expression 
     * @param val
     * @return
     */
    public SelectorBuilder<T> matches(final String val){
    	final Pattern pattern = Pattern.compile(val);
        operators.add(new UnaryOperator<T>(accessor, context, "matches[regexp]"+val){
            @Override
            public boolean eval(T element) {
            	return pattern.matcher(getString(element)).matches();
            }
        });
        return this;
    }

    /**
     * Change the context to a new field
     * @param name
     * @return
     */
    public SelectorBuilder<T> field(String name){
        this.context = name;
        return this;
    }

    /**
     * Boolean greater than or equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> geq(final Boolean val){
        operators.add(new UnaryOperator<T>(accessor, context, ">=[boolean]"+val){
            @Override
            public boolean eval(T element) {
                return getBoolean(element).compareTo(val) >= 0;
            }
        });
        return this;
    }

    /**
     * Greater than or equals for Dates
     * @param val
     * @return
     */
    public SelectorBuilder<T> geq(final Date val){
        operators.add(new UnaryOperator<T>(accessor, context, ">=[date]"+val){
            @Override
            public boolean eval(T element) {
                return getDate(element).compareTo(val) >= 0;
            }
        });
        return this;
    }

    /**
     * Numeric greater than or equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> geq(final double val){
        operators.add(new UnaryOperator<T>(accessor, context, ">=[numeric]"+val){
            @Override
            public boolean eval(T element) {
                return getDouble(element) >= val;
            }
        });
        return this;
    }

    /**
     * Lexicographic greater than or equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> geq(final String val){
        operators.add(new UnaryOperator<T>(accessor, context, ">=[string]"+val){
            @Override
            public boolean eval(T element) {
                return getString(element).compareTo(val) >= 0;
            }
        });
        return this;
    }

    /**
     * Boolean greater than
     * @param val
     * @return
     */
    public SelectorBuilder<T> greater(final Boolean val){
        operators.add(new UnaryOperator<T>(accessor, context, ">[boolean]"+val){
            @Override
            public boolean eval(T element) {
                return getBoolean(element).compareTo(val) > 0;
            }
        });
        return this;
    }

    /**
     * Greater for Dates
     * @param val
     * @return
     */
    public SelectorBuilder<T> greater(final Date val){
        operators.add(new UnaryOperator<T>(accessor, context, ">[date]"+val){
            @Override
            public boolean eval(T element) {
                return getDate(element).compareTo(val) > 0;
            }
        });
        return this;
    }

    /**
     * Numeric greater than
     * @param val
     * @return
     */
    public SelectorBuilder<T> greater(final double val){
        operators.add(new UnaryOperator<T>(accessor, context, ">[numeric]"+val){
            @Override
            public boolean eval(T element) {
                return getDouble(element) > val;
            }
        });
        return this;
    }

    /**
     * Lexicographic greater than
     * @param val
     * @return
     */
    public SelectorBuilder<T> greater(final String val){
        operators.add(new UnaryOperator<T>(accessor, context, ">[string]"+val){
            @Override
            public boolean eval(T element) {
                return getString(element).compareTo(val) > 0;
            }
        });
        return this;
    }

    /**
     * Boolean less than or equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> leq(final Boolean val){
        operators.add(new UnaryOperator<T>(accessor, context, "<=[boolean]"+val){
            @Override
            public boolean eval(T element) {
                return getBoolean(element).compareTo(val) <= 0;
            }
        });
        return this;
    }

    /**
     * Less than or equals for Dates
     * @param val
     * @return
     */
    public SelectorBuilder<T> leq(final Date val){
        operators.add(new UnaryOperator<T>(accessor, context, "<=[date]"+val){
            @Override
            public boolean eval(T element) {
                return getDate(element).compareTo(val) <= 0;
            }
        });
        return this;
    }

    /**
     * Numeric less than or equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> leq(final double val){
        operators.add(new UnaryOperator<T>(accessor, context, "<=[numeric]"+val){
            @Override
            public boolean eval(T element) {
                return getDouble(element) <= val;
            }
        });
        return this;
    }

    /**
     * Lexicographic less than or equals
     * @param val
     * @return
     */
    public SelectorBuilder<T> leq(final String val){
        operators.add(new UnaryOperator<T>(accessor, context, "<=[string]"+val){
            @Override
            public boolean eval(T element) {
                return getString(element).compareTo(val) <= 0;
            }
        });
        return this;
    }

    /**
     * Boolean less than
     * @param val
     * @return
     */
    public SelectorBuilder<T> less(final Boolean val){
        operators.add(new UnaryOperator<T>(accessor, context, "<[boolean]"+val){
            @Override
            public boolean eval(T element) {
                return getBoolean(element).compareTo(val) < 0;
            }
        });
        return this;
    }

    /**
     * Less for Dates
     * @param val
     * @return
     */
    public SelectorBuilder<T> less(final Date val){
        operators.add(new UnaryOperator<T>(accessor, context, "<[date]"+val){
            @Override
            public boolean eval(T element) {
                return getDate(element).compareTo(val) < 0;
            }
        });
        return this;
    }

    /**
     * Numeric less than
     * @param val
     * @return
     */
    public SelectorBuilder<T> less(final double val){
        operators.add(new UnaryOperator<T>(accessor, context, "<[numeric]"+val){
            @Override
            public boolean eval(T element) {
                return getDouble(element) < val;
            }
        });
        return this;
    }

    /**
     * Lexicographic less than
     * @param val
     * @return
     */
    public SelectorBuilder<T> less(final String val){
        operators.add(new UnaryOperator<T>(accessor, context, "<[string]"+val){
            @Override
            public boolean eval(T element) {
                return getString(element).compareTo(val) < 0;
            }
        });
        return this;
    }

    /**
     * Logical or operator
     * @return
     */
    public SelectorBuilder<T> or(){
        operators.add(new BinaryOperator<T>(accessor, "or"){
            @Override
            public boolean eval(T element) {
                return left.eval(element) || right.eval(element);
            }
        });
        return this;
    }
}
