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
 * This class uses the finite difference method to approximate derivatives of functions
 * 
 * @author Fabian Prasser
 */
public class Derivation2D {

    /** See http://www.karenkopecky.net/Teaching/eco613614/Notes_NumericalDifferentiation.pdf*/
    private static final double EPSILON = Math.sqrt(Math.ulp(1d));

    /**
     * Returns the function derived by the first argument
     * @param function
     * @return
     */
    public Function2D derive1(final Function2D function) {
        return new Function2D() {
            public Double evaluate(Vector2D input) {
                return evaluateDerivativeFunction1(function, input);
            }
        };
    }

    /**
     * Returns the function derived by the second argument
     * @param function
     * @return
     */
    public Function2D derive2(final Function2D function) {
        return new Function2D() {
            public Double evaluate(Vector2D input) {
                return evaluateDerivativeFunction2(function, input);
            }
        };
    }

    /**
     * Result of the function derived by the first argument at the given point
     * @param function
     * @param point
     * @return
     */
    public double evaluateDerivativeFunction1(Function2D function, Vector2D point) {
        return evaluateDerivativeFunction1(function, point, function.evaluate(point));
    }

    /**
     * Result of the function derived by the first argument at the given point
     * @param function
     * @param point
     * @param result value of the function at the point
     * @return
     */
    public double evaluateDerivativeFunction1(Function2D function, Vector2D point, double result) {
        // See http://www.karenkopecky.net/Teaching/eco613614/Notes_NumericalDifferentiation.pdf
        double delta = EPSILON * Math.max(Math.abs(point.x), 1);
        double y1 = result;
        point.x += delta;
        double y2 = function.evaluate(point);
        point.x -= delta;
        return (y2 - y1) / delta;
    }

    /**
     * Result of the function derived by the second argument at the given point
     * @param function
     * @param point
     * @return
     */
    public double evaluateDerivativeFunction2(Function2D function, Vector2D point) {
        return evaluateDerivativeFunction2(function, point, function.evaluate(point));
    }

    /**
     * Result of the function derived by the second argument at the given point
     * @param function
     * @param point
     * @param result value of the function at the point
     * @return
     */
    public double evaluateDerivativeFunction2(Function2D function, Vector2D point, double result) {
        // See http://www.karenkopecky.net/Teaching/eco613614/Notes_NumericalDifferentiation.pdf
        double delta = EPSILON * Math.max(Math.abs(point.y), 1);
        double y1 = result;
        point.y += delta;
        double y2 = function.evaluate(point);
        point.y -= delta;
        return (y2 - y1) / delta;
    }
}
