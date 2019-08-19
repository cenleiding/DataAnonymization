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
 * The class implements the Newton-Raphson algorithm
 * 
 * @author Fabian Prasser
 */
public class NewtonRaphson2D extends NewtonRaphsonConfiguration<NewtonRaphson2D>{

    /** SVUID*/
    private static final long serialVersionUID = -2439285310000826600L;

    /** Constraints */
    private final Constraint2D[]                                     constraints;

    /** Derivative functions */
    private final Function<Vector2D, SquareMatrix2D>                 derivativeFunction;

    /** A function implementing the object function and the derivate functions */
    private final Function<Vector2D, Pair<Vector2D, SquareMatrix2D>> masterFunction;

    /** Measures of the last run */
    private NewtonRaphsonMeasures                                    measures;

    /** First and second function */
    private final Function<Vector2D, Vector2D>                       objectFunction;

    /** First function */
    private final Function2D                                         objectFunction1;

    /** Second function */
    private final Function2D                                         objectfunction2;

    /**
     * Creates a new instance
     * @param function
     */
    public NewtonRaphson2D(Function<Vector2D, Pair<Vector2D, SquareMatrix2D>> function) {
        this(function, (Constraint2D[])null);
    }

    /**
     * Creates a new instance
     * @param function
     * @param constraints
     */
    public NewtonRaphson2D(Function<Vector2D, Pair<Vector2D, SquareMatrix2D>> function,
                           Constraint2D... constraints) {
        this.masterFunction = function;
        this.objectFunction = null;
        this.derivativeFunction = null;
        this.objectFunction1 = null;
        this.objectfunction2 = null;
        this.constraints = constraints;
    }

    /**
     * Creates a new instance
     * @param functions
     * @param derivatives
     */
    public NewtonRaphson2D(Function<Vector2D, Vector2D> functions,
                           Function<Vector2D, SquareMatrix2D> derivatives) {
        this(functions, derivatives, (Constraint2D[])null);
    }

    /**
     * Creates a new instance
     * @param functions
     * @param derivatives
     * @param constraints
     */
    public NewtonRaphson2D(Function<Vector2D, Vector2D> functions,
                           Function<Vector2D, SquareMatrix2D> derivatives,
                           Constraint2D... constraints) {
        this.objectFunction = functions;
        this.derivativeFunction = derivatives;
        this.objectFunction1 = null;
        this.objectfunction2 = null;
        this.masterFunction = null;
        this.constraints = constraints;
    }

    /**
     * Creates a new instance. This variant will automatically derive the given functions.
     * @param function1
     * @param function2
     */
    public NewtonRaphson2D(Function2D function1,
                           Function2D function2) {
        this(function1, function2, (Constraint2D[])null);
    }

    /**
     * Creates a new instance. This variant will automatically derive the given functions.
     * @param function1
     * @param function2
     * @param constraints
     */
    public NewtonRaphson2D(Function2D function1,
                           Function2D function2,
                           Constraint2D... constraints) {
        this.objectFunction = null;
        this.derivativeFunction = null;
        this.objectFunction1 = function1;
        this.objectfunction2 = function2;
        this.masterFunction = null;
        this.constraints = constraints;
    }

    /**
     * Creates a new instance
     * @param function1
     * @param function2
     * @param derivatives
     */
    public NewtonRaphson2D(Function2D function1,
                           Function2D function2,
                           Function<Vector2D, SquareMatrix2D> derivatives) {
        this(function1, function2, derivatives, (Constraint2D[])null);
    }

    /**
     * Creates a new instance
     * @param function1
     * @param function2
     * @param derivatives
     * @param constraints
     */
    public NewtonRaphson2D(Function2D function1,
                           Function2D function2,
                           Function<Vector2D, SquareMatrix2D> derivatives,
                           Constraint2D... constraints) {
        this.objectFunction = null;
        this.derivativeFunction = derivatives;
        this.objectFunction1 = function1;
        this.objectfunction2 = function2;
        this.masterFunction = null;
        this.constraints = constraints;
    }

    /**
     * Creates a new instance. This variant will automatically derive the given functions.
     * @param function1
     * @param function2
     * @param derivative11
     * @param derivative12
     * @param derivative21
     * @param derivative22
     */
    public NewtonRaphson2D(final Function2D function1,
                           final Function2D function2,
                           final Function2D derivative11,
                           final Function2D derivative12,
                           final Function2D derivative21,
                           final Function2D derivative22) {
        this(function1, function2, derivative11, derivative12, derivative21, derivative22, (Constraint2D[])null);
    }

    /**
     * Creates a new instance. This variant will automatically derive the given functions.
     * @param function1
     * @param function2
     * @param derivative11
     * @param derivative12
     * @param derivative21
     * @param derivative22
     * @param constraints
     */
    public NewtonRaphson2D(final Function2D function1,
                           final Function2D function2,
                           final Function2D derivative11,
                           final Function2D derivative12,
                           final Function2D derivative21,
                           final Function2D derivative22,
                           final Constraint2D... constraints) {
        this.objectFunction = null;
        this.constraints = constraints;
        this.derivativeFunction = new Function<Vector2D, SquareMatrix2D>(){
            SquareMatrix2D result = new SquareMatrix2D();
            public SquareMatrix2D evaluate(Vector2D input) {
                result.x1 = derivative11.evaluate(input);
                result.x2 = derivative12.evaluate(input);
                result.y1 = derivative21.evaluate(input);
                result.y2 = derivative22.evaluate(input);
                return result;
            }
        };
        this.objectFunction1 = function1;
        this.objectfunction2 = function2;
        this.masterFunction = null;
    }
    
    /**
     * Configures this instance
     * @param config
     */
    public NewtonRaphson2D configure(NewtonRaphsonConfiguration<?> config) {
        this.accuracy = config.accuracy;
        this.iterationsPerTry = config.iterationsPerTry;
        this.iterationsTotal = config.iterationsTotal;
        this.timePerTry = config.timePerTry;
        this.timeTotal = config.timeTotal;
        this.preparedStartValues = config.preparedStartValues;
        if (this.preparedStartValues != null) {
            this.iterationsTotal = (this.preparedStartValues.length + 1) * iterationsPerTry; // Includes given start value
        }
        return this;
    }
    
    /**
     * Returns measurements for the last run
     */
    public NewtonRaphsonMeasures getMeasures() {
        return measures;
    }

    /**
     * Returns the vector of solutions obtained by the Newton-Raphson algorithm.
     */
    public Vector2D solve() {
        return this.solve(new Vector2D(1.0d, 1.0d));
    }

    /**
     * Returns the vector of solutions obtained by the Newton-Raphson algorithm.
     * 
     * @param start
     */
    public Vector2D solve(Vector2D start) {
        return this._solve(start);
    }

    /**
     * Implementation of the Newton-Raphson algorithm
     * @param start
     * @param constraints
     * @return
     */
    private Vector2D _solve(Vector2D start) {
        
        // Init math stuff
        Vector2D init = start.clone();
        Vector2D solution = null;
        Vector2D object = new Vector2D();
        SquareMatrix2D derivatives = new SquareMatrix2D();
        Derivation2D derivation = this.derivativeFunction != null ? null : new Derivation2D();
        
        // Measure
        long totalStart = System.currentTimeMillis();
        int totalTries = 0;
        int totalIterations = 0;
        int preparedStartValuesOffset = 0;

        // Solve
        outer: while (totalIterations <= this.iterationsTotal) {

            // Init timers
            long startPerTry = System.currentTimeMillis();
            int iterations = 0;

            // Init solution
            if (solution == null) {
                solution = start;
            } else {
                if (this.preparedStartValues != null) {
                    solution = new Vector2D(preparedStartValues[preparedStartValuesOffset][0], preparedStartValues[preparedStartValuesOffset++][1]);
                } else {
                    solution = new Vector2D((Math.random() * 2d - 1d) * init.x, 
                                            (Math.random() * 2d - 1d) * init.y);
                }
            }

            // Loop
            inner: while (true) {
                
                // Measure
                totalIterations++;
                
                // Without master function
                if (masterFunction == null) { 
        
                    // Evaluate object function
                    if (this.objectFunction == null) {
                        object.x = objectFunction1.evaluate(solution);
                        object.y = objectfunction2.evaluate(solution);
                    } else {
                        object = objectFunction.evaluate(solution);
                    }
        
                    // Break
                    if (Math.abs(object.x) <= accuracy && Math.abs(object.y) <= accuracy) {

                        // Store measures
                        double quality = 1.0d - Math.sqrt(object.x * object.x + object.y * object.y);
                        measures = new NewtonRaphsonMeasures(totalIterations, 
                                                             totalTries, 
                                                             (int)(System.currentTimeMillis() - totalStart),
                                                             quality);
                        
                        // Return
                        return solution;
                    }
        
                    // Derive
                    if (this.derivativeFunction == null) {
                        derivatives.x1 = derivation.evaluateDerivativeFunction1(objectFunction1, solution, object.x);
                        derivatives.x2 = derivation.evaluateDerivativeFunction2(objectFunction1, solution, object.x);
                        derivatives.y1 = derivation.evaluateDerivativeFunction1(objectfunction2, solution, object.y);
                        derivatives.y2 = derivation.evaluateDerivativeFunction2(objectfunction2, solution, object.y);
                    } else {
                        derivatives = this.derivativeFunction.evaluate(solution);
                    }
                    
                // With master function
                } else {
        
                    // Evaluate object function and derivatives
                    Pair<Vector2D, SquareMatrix2D> results = masterFunction.evaluate(solution);
                    object = results.first;
                    derivatives = results.second;
                    
                    // Break
                    if (Math.abs(object.x) <= accuracy && Math.abs(object.y) <= accuracy) {

                        // Store measures
                        double quality = 1.0d - Math.sqrt(object.x * object.x + object.y * object.y);
                        measures = new NewtonRaphsonMeasures(totalIterations, 
                                                             totalTries, 
                                                             (int)(System.currentTimeMillis() - totalStart),
                                                             quality);
                        
                        // Return
                        return solution;
                    }
                }
        
                // Compute difference
                derivatives.inverse();
                object.times(derivatives);
                solution.minus(object);
                
                // Timing limit
                long time = System.currentTimeMillis();
                if (time - totalStart > timeTotal ||
                    (preparedStartValues != null && preparedStartValuesOffset == preparedStartValues.length)) {
                    break outer;
                }

                // Check constraints
                if (constraints != null) {
                    for (Constraint2D constraint : constraints) {
                        if (!constraint.evaluate(solution)) {
                            break inner;
                        }
                    }
                }
                
                // Error or constraint reached
                if (solution.isNaN() || 
                    iterations++ >= iterationsPerTry || 
                    time - startPerTry > timePerTry) {
                    break inner;
                }
            }
        }
        
        // Store measures
        measures = new NewtonRaphsonMeasures(totalIterations, 
                                             totalTries, 
                                             (int)(System.currentTimeMillis() - totalStart),
                                             0d);
        
        // Nothing found
        return new Vector2D(Double.NaN, Double.NaN);
    }
}
