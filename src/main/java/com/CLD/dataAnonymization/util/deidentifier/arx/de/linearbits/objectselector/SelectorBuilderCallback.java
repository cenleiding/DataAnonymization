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

import java.util.Date;

import com.CLD.dataAnonymization.util.deidentifier.arx.de.linearbits.objectselector.datatypes.DataType;

/**
 * A callback for the builder
 * @author Fabian Prasser
 *
 * @param <T>
 */
public class SelectorBuilderCallback<T> implements ICallback{

    /**
     * Current operator context
     */
    private static enum Operator {
        EQUALS,
        NEQ,
        GEQ,
        LEQ,
        LESS,
        GREATER
    }

    /** The target selector */
    private final SelectorBuilder<T> selector;

    /** The query */
    private final String             query;

    /** The schema, if any */
    private final IAccessor<T>       accessor;

    /** The current field */
    private String                   currentField = null;

    /** The current operator */
    private Operator                 currentOp   = null;

    /** The current data type */
    private DataType<?>              currentType = null;

    /**
     * Creates a new selector 
     * @param selector
     */
    protected SelectorBuilderCallback(IAccessor<T> accessor, SelectorBuilder<T> selector, String query) {
        this.query = query;
        this.selector = selector;
        this.accessor = accessor;
    }

    /**
     * Handle an operator
     * @param start
     * @param length
     */
    public void and(int start, int length) {
        checkCurrent();
        selector.and();
    }
    /**
     * Handle an operator
     * @param start
     */
    public void begin(int start) {
        checkCurrent();
        selector.begin();
    }

    /**
     * Finally checks for dangling operators
     */
    public void check() {
        checkCurrent();
    }

    /**
     * Handle an operator
     * @param start
     */
    public void end(int start) {
        checkCurrent();
        selector.end();
    }

    /**
     * Handle an operator
     * @param start
     */
    public void equals(int start) {
        setCurrent(Operator.EQUALS);
    }

    /**
     * Handle an operator
     * @param start
     * @param length
     */
    public void field(int start, int length) {

        String field = query.substring(start + 1, start + length - 1);
        DataType<?> type = DataType.STRING;
        if (accessor.isExistanceSupported()) {
            if (!accessor.exists(field)) {
                throw new RuntimeException("Unknown field: " + field);
            }
        }
        if (accessor.isDataTypesSupported()) {
            type = accessor.getType(field);
        }

        this.currentType = type;
        this.selector.field(field);
        this.setCurrent(field);
    }

    /**
     * Handle an operator
     * @param start
     * @param length
     */
    public void geq(int start, int length) {
        setCurrent(Operator.GEQ);
    }

    /**
     * Handle an operator
     * @param start
     */
    public void greater(int start) {
        setCurrent(Operator.GREATER);
    }

    /**
     * Handle an operator
     * @param start
     */
    public void invalid(int start) {
        throw new RuntimeException("Syntax error at: " + start);
    }

    /**
     * Handle an operator
     * @param start
     * @param length
     */
    public void leq(int start, int length) {
        setCurrent(Operator.LEQ);
    }

    /**
     * Handle an operator
     * @param start
     */
    public void less(int start) {
        setCurrent(Operator.LESS);
    }

    /**
     * Handle an operator
     * @param start
     * @param length
     */
    public void neq(int start, int length) {
        setCurrent(Operator.NEQ);
    }

    /**
     * Handle an operator
     * @param start
     * @param length
     */
    public void or(int start, int length) {
        checkCurrent();
        selector.or();
    }
    
    /**
     * Handle an operator
     * @param start
     * @param length
     */
    public void value(int start, int length) {

        String string = escape(query.substring(start + 1, start + length - 1));
        if (currentOp == null) { throw new RuntimeException("Unknown operator: " + string); }
        if (currentType == null) { throw new RuntimeException("Unknown data type: " + string); }

        Object value = currentType.fromString(string);
        switch (currentOp) {
        case EQUALS:
            if (value instanceof Date) {
                selector.equals((Date) value);
            } else if (value instanceof String) {
                selector.equals((String) value);
            } else if (value instanceof Double) {
                selector.equals(((Double)value).doubleValue());
            } else if (value instanceof Boolean) {
                selector.equals((Boolean) value);
            }
            break;
        case NEQ:
            if (value instanceof Date) {
                selector.neq((Date) value);
            } else if (value instanceof String) {
                selector.neq((String) value);
            } else if (value instanceof Double) {
                selector.neq((Double) value);
            } else if (value instanceof Boolean) {
                selector.neq((Boolean) value);
            }
            break;
        case GEQ:
            if (value instanceof Date) {
                selector.geq((Date) value);
            } else if (value instanceof String) {
                selector.geq((String) value);
            } else if (value instanceof Double) {
                selector.geq((Double) value);
            } else if (value instanceof Boolean) {
                selector.geq((Boolean) value);
            }
            break;
        case GREATER:
            if (value instanceof Date) {
                selector.greater((Date) value);
            } else if (value instanceof String) {
                selector.greater((String) value);
            } else if (value instanceof Double) {
                selector.greater((Double) value);
            } else if (value instanceof Boolean) {
                selector.greater((Boolean) value);
            }
            break;
        case LEQ:
            if (value instanceof Date) {
                selector.leq((Date) value);
            } else if (value instanceof String) {
                selector.leq((String) value);
            } else if (value instanceof Double) {
                selector.leq((Double) value);
            } else if (value instanceof Boolean) {
                selector.leq((Boolean) value);
            }
            break;
        case LESS:
            if (value instanceof Date) {
                selector.less((Date) value);
            } else if (value instanceof String) {
                selector.less((String) value);
            } else if (value instanceof Double) {
                selector.less((Double) value);
            } else if (value instanceof Boolean) {
                selector.less((Boolean) value);
            }
            break;
        }
        currentOp = null;
        currentType = null;
    }

    /**
     * Checks whether the current context is empty
     */
    private void checkCurrent() {
        if (currentField != null) {
            throw new RuntimeException("Dangling field: " + currentField);
        }
        if (currentOp != null) {
            throw new RuntimeException("Dangling operator: " + currentOp);
        }
    }

    /**
     * Escapes a string value
     * @param substring
     * @return
     */
    private String escape(String string) {
        StringBuffer buffer = new StringBuffer();
        char[] array = string.toCharArray();
        for (int i=0; i<array.length; i++){
            if (array[i]=='\\') {
                if (i == array.length-1 || !(array[i+1]=='\\' || array[i+1]=='\'')) {
                    throw new RuntimeException("Invalid escape sequence in: "+string);
                } else {
                    buffer.append(array[i+1]);
                    i++;
                }
            } else {
                buffer.append(array[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * Handle an operator
     * @param operator
     */
    private void setCurrent(Operator operator) {
        
        if (currentField == null) {
            throw new RuntimeException("No field for operator: " + operator);
        }
        
        if (currentOp != null) {
            throw new RuntimeException("Duplicate operator: " + operator);
        } else {
            currentOp = operator;
            currentField = null;
        }
    }
    
    /**
     * Handle a field
     * @param field
     */
    private void setCurrent(String field) {
        if (currentField != null) {
            throw new RuntimeException("Duplicate field: " + field);
        } else {
            currentField = field;
        }
    }
}
