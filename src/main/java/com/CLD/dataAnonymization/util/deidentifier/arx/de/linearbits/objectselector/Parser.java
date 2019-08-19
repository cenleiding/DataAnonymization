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
import java.util.List;

/**
 * Parser for expressions created with the builder pattern
 * 
 * @author Fabian Prasser
 */
public class Parser<T> {

    /** The list of defined operators */
    private List<AbstractOperator<T>> operators;

    /** The resulting root operator */
    private AbstractOperator<T>       root;

    /**
     * Creates a new parser
     * 
     * @param operators
     */
    protected Parser(List<AbstractOperator<T>> operators) {
        this.operators = operators;
    }

    /**
     * Finds an expression within the given range
     * 
     * @param ops
     * @param offset
     * @param length
     * @return
     * @throws ParseException 
     */
    private int find(List<AbstractOperator<T>> ops, int offset, int length) throws ParseException {

        if (offset >= ops.size()) { throw new ParseException("Missing expression", offset); }

        AbstractOperator<T> op = ops.get(offset);
        if (op instanceof BinaryOperator) {

            // Invalid
            throw new ParseException("Expression must not start with binary operator", offset);
        } else if (op instanceof UnaryOperator) {

            // Just a unary operator
            return 1;

        } else if (op instanceof ParenthesisOperator) {

            ParenthesisOperator<T> pop = (ParenthesisOperator<T>) op;

            if (!pop.isBegin()) {

                // Invalid
                throw new ParseException("Invalid parenthesis", offset);

            } else {

                // Find closing bracket
                int open = 1;
                for (int i = offset + 1; i < offset + length; i++) {
                    if (ops.get(i) instanceof ParenthesisOperator) {
                        pop = (ParenthesisOperator<T>) ops.get(i);
                        if (pop.isBegin()) open++;
                        else open--;
                        if (open == 0) { return i - offset + 1; }
                    } else {
                    }
                }
                // Invalid
                throw new ParseException("Missing closing parentheses (" + open + ")", length);
            }
        } else {

            // Invalid
            throw new ParseException("Unknown operator", offset);
        }
    }

    /**
     * Parses the list of operators within the given range
     * 
     * @param ops
     * @param offset
     * @param length
     * @return
     * @throws ParseException 
     */
    private AbstractOperator<T> parse(List<AbstractOperator<T>> ops, int offset, int length) throws ParseException {

        int lLength = find(ops, offset, length);

        if (lLength == length) {

            // Case 1: EXPR
            if (length == 1) {

                // Return single operator
                return ops.get(offset);

            } else if ((ops.get(offset) instanceof ParenthesisOperator) &&
                    (ops.get(offset + length - 1) instanceof ParenthesisOperator)) {

                // Remove brackets
                return parse(ops, offset + 1, length - 2);

            } else {
                throw new ParseException("Invalid expression", offset);
            }

        } else {

            // Case 2: EXPR <OP> EXPR
            if (!(ops.get(offset + lLength) instanceof BinaryOperator)) {

                // Invalid
                throw new ParseException("Expecting EXPR <OP> EXPR", offset + lLength);
            } else {
                // Binary operator
                BinaryOperator<T> bop = (BinaryOperator<T>) ops.get(offset + lLength);
                bop.setLeft(parse(ops, offset, lLength));
                bop.setRight(parse(ops, offset + lLength + 1, length - lLength - 1));
                return bop;
            }
        }
    }

    /**
     * Returns the root operator
     * 
     * @return
     */
    protected AbstractOperator<T> getRoot() {
        return root;
    }

    /**
     * Starts the compilation process
     */
    protected void parse() throws ParseException {
        if (operators.isEmpty()) { throw new ParseException("Empty expression", 0); }
        this.root = parse(operators, 0, operators.size());
        if (this.root == null) { throw new ParseException("Cannot parse expression", 0); }
    }

}
