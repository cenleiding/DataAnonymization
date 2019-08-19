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
 * Runtime measures
 * @author Fabian Prasser
 */
public class NewtonRaphsonMeasures {

    /** Iterations */
    private final int    iterations;
    /** Time */
    private final int    time;
    /** Tries */
    private final int    tries;
    /** Result quality */
    private final double quality;
    
    /**
     * Creates a new instance
     * @param iterations
     * @param tries
     * @param time
     * @param quality
     */
    NewtonRaphsonMeasures(int iterations, int tries, int time, double quality) {
        this.iterations = iterations;
        this.tries = tries;
        this.time = time;
        this.quality = quality;
    }

    /**
     * Returns the result quality, defined as the euclidean distance from a solution
     */
    public double getQuality() {
        return quality;
    }

    /**
     * Returns the number of iterations
     */
    public int getIterations() {
        return iterations;
    }

    /**
     * Returns the total time in milliseconds
     */
    public int getTime() {
        return time;
    }

    /**
     * Returns the number of tries
     */
    public int getTries() {
        return tries;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Measures: (Time: ").append(time).append(" [ms]");
        builder.append(", Tries: ").append(tries);
        builder.append(", Iterations: ").append(iterations);
        builder.append(", Quality: ").append(quality);
        builder.append(")");
        return builder.toString();
    }
}
