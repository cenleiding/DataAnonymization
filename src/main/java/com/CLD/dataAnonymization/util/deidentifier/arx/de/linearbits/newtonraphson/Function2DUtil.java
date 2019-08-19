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
 * Function utilities
 * @author Fabian Prasser
 *
 */
public class Function2DUtil {

    /**
     * Helper function that tries to determine whether the second function is a derivative regarding the first argument
     * of the first function in the range [x1,x2] at y with the given accuracy 
     * @param function
     * @param derivative
     * @param x1
     * @param x2
     * @param delta
     * @param y
     * @param accuracy
     * @return
     */
    public boolean isDerivativeFunction1(Function2D function, 
                                                Function2D derivative,
                                                double x1,
                                                double x2,
                                                double delta,
                                                double y,
                                                double accuracy) {
        
        Derivation2D derivation = new Derivation2D();
        Function2D estimate = derivation.derive1(function);
        return isSameFunction1(estimate, derivative, x1, x2, delta, y, accuracy);
    }

    /**
     * Helper function that tries to determine whether the second function is a derivative regarding the second argument
     * of the first function in the range [y1,y2] at x with the given accuracy 
     * @param function
     * @param derivative
     * @param y1
     * @param y2
     * @param x
     * @param accuracy
     * @return
     */
    public boolean isDerivativeFunction2(Function2D function, 
                                                Function2D derivative,
                                                double y1,
                                                double y2,
                                                double delta,
                                                double x,
                                                double accuracy) {

        Derivation2D derivation = new Derivation2D();
        Function2D estimate = derivation.derive2(function);
        return isSameFunction2(estimate, derivative, y1, y2, delta, x, accuracy);
    }

    /**
     * Helper function that tries to determine whether the second function is equal regarding the first argument
     * in the range [x1,x2] at y with the given accuracy 
     * @param function
     * @param derivative
     * @param x1
     * @param x2
     * @param delta
     * @param y
     * @param accuracy
     * @return
     */
    public boolean isSameFunction1(Function2D function1,
                                  Function2D function2,
                                  double x1,
                                  double x2,
                                  double delta,
                                  double y,
                                  double accuracy) {
        
        Vector2D input = new Vector2D(x1, y);
        for (double x = x1; x <= x2; x+=delta) {
            input.x = x;
            double y1 = function1.evaluate(input);
            double y2 = function2.evaluate(input);
            
            double min = Math.min(y1, y2);
            double max = Math.max(y1, y2);
            double diff = 1.0d - (min/max);
            
            if (diff > accuracy) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Helper function that tries to determine whether the second function is equal regarding the second argument
     * in the range [y1,y2] at x with the given accuracy 
     * @param function
     * @param derivative
     * @param y1
     * @param y2
     * @param x
     * @param accuracy
     * @return
     */
    public boolean isSameFunction2(Function2D function1,
                                   Function2D function2,
                                   double y1,
                                   double y2,
                                   double delta,
                                   double x,
                                   double accuracy) {

        Vector2D input = new Vector2D(x, y1);
        for (double y = y1; y <= y2; y += delta) {
            input.y = y;
            double x1 = function1.evaluate(input);
            double x2 = function2.evaluate(input);
            
            double min = Math.min(x1, x2);
            double max = Math.max(x1, x2);
            double diff = 1.0d - (min/max);
            
            if (diff > accuracy) {
                return false;
            }
        }
        return true;
    }
}
