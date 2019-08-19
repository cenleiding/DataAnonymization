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

import java.io.Serializable;

/**
 * This class defines runtime constraints for the solver
 * @author Fabian Prasser
 */
public class NewtonRaphsonConfiguration<T> implements Serializable {

    /** Constant */
    public static final double DEFAULT_ACCURACY           = 10e-6;
    
    /** Constant */
    public static final int    DEFAULT_ITERATIONS_PER_TRY = 100;
    /** Constant */
    public static final int    DEFAULT_ITERATIONS_TOTAL   = 1000;
    /** Constant */
    public static final int    DEFAULT_TIME_PER_TRY       = 1000;
    /** Constant */
    public static final int    DEFAULT_TIME_TOTAL         = 10000;
    /** SVUID*/
    private static final long serialVersionUID = -8127035086199045592L;

    /**
     * Creates a new instance
     * @return
     */
    public static NewtonRaphsonConfiguration<?> create() {
        return new NewtonRaphsonConfiguration<>();
    }

    /** Runtime constraint */
    double     accuracy         = DEFAULT_ACCURACY;
    /** Runtime constraint */
    int        iterationsPerTry = DEFAULT_ITERATIONS_PER_TRY;
    /** Runtime constraint */
    int        iterationsTotal  = DEFAULT_ITERATIONS_TOTAL;
    /** Runtime constraint */
    int        timePerTry       = DEFAULT_TIME_PER_TRY;
    /** Runtime constraint */
    int        timeTotal        = DEFAULT_TIME_TOTAL;
    /** Runtime constraint */
    double[][] preparedStartValues      = null;
    
    /**
     * Constructor
     */
    protected NewtonRaphsonConfiguration() {
        // Empty by design
    }

    /**
     * Accuracy. Default is  10e-6.
     */
    @SuppressWarnings("unchecked")
    public T accuracy(double accuracy) {
        this.accuracy = accuracy;
        return (T)this;
    }

    /**
     * @return the accuracy
     */
    public double getAccuracy() {
        return accuracy;
    }
    
    /**
     * @return the iterationsPerTry
     */
    public int getIterationsPerTry() {
        return iterationsPerTry;
    }

    /**
     * @return the iterationsTotal
     */
    public int getIterationsTotal() {
        return iterationsTotal;
    }

    /**
     * @return the start values
     */
    public double[][] getStartValues() {
        return preparedStartValues;
    }

    /**
     * @return the timePerTry
     */
    public int getTimePerTry() {
        return timePerTry;
    }
    
    /**
     * @return the timeTotal
     */
    public int getTimeTotal() {
        return timeTotal;
    }

    /**
     * Iterations per try. Default is 100.
     */
    @SuppressWarnings("unchecked")
    public T iterationsPerTry(int iterationsPerTry) {
        this.iterationsPerTry = iterationsPerTry;
        return (T)this;
    }

    /**
     * Total iterations. Default is 1000.
     */
    @SuppressWarnings("unchecked")
    public T iterationsTotal(int iterationsTotal) {
        this.iterationsTotal = iterationsTotal;
        return (T)this;
    }

    /**
     * Predefined start values
     */
    @SuppressWarnings("unchecked")
    public T preparedStartValues(double[][] startValues) {
        if (startValues != null) {
            if (startValues.length == 0) {
                throw new IllegalArgumentException("Invalid start values");
            }
            for (double[] values : startValues) {
                if (values == null || values.length != 2) {
                    throw new IllegalArgumentException("Invalid start values");
                }
            }
        }
        this.preparedStartValues = startValues;
        return (T)this;
    }

    /**
     * Time in milliseconds per try. Default is 1000.
     */
    @SuppressWarnings("unchecked")
    public T timePerTry(int timePerTry) {
        this.timePerTry = timePerTry;
        return (T)this;
    }

    /**
     * Total time in milliseconds. Default is 10000.
     */
    @SuppressWarnings("unchecked")
    public T timeTotal(int timeTotal) {
        this.timeTotal = timeTotal;
        return (T)this;
    }
}
