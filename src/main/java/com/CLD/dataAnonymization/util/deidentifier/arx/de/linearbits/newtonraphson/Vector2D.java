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
 * This class implements a vector in RxR
 * @author Fabian Prasser
 */
public class Vector2D {

    /** Dimension */
    public double x;
    /** Dimension */
    public double y;

    /**
     * Creates a new instance
     */
    public Vector2D() {
        // Empty by design
    }

    /**
     * Creates a new instance
     * @param x
     * @param y
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Clone
     */
    public Vector2D clone() {
        return new Vector2D(x,y);
    }

    /**
     * Returns whether any of the components is NaN
     * @return
     */
    public boolean isNaN() {
        return Double.isNaN(x) || Double.isNaN(y);
    }

    /**
     * Subtracts the given vector
     * @param vector
     */
    public void minus(Vector2D vector) {
        this.x -= vector.x;
        this.y -= vector.y;
    }

    /**
     * Multiplies the vector with the given matrix
     * @param matrix
     */
    public void times(SquareMatrix2D matrix) {
        double x = matrix.x1 * this.x + matrix.x2 * this.y;
        double y = matrix.y1 * this.x + matrix.y2 * this.y;
        this.x = x;
        this.y = y;
    }
    
    /**
     * Returns a string representation
     */
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
