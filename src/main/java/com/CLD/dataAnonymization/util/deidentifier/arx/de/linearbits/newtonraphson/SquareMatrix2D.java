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
 * This class implements a square matrix in RxR
 * @author Fabian Prasser
 */
public class SquareMatrix2D {

    /** (x1 x2), (y1 y2) */
    public double x1;
    /** (x1 x2), (y1 y2) */
    public double x2;
    /** (x1 x2), (y1 y2) */
    public double y1;
    /** (x1 x2), (y1 y2) */
    public double y2;

    /**
     * Creates a new instance
     */
    public SquareMatrix2D() {
        // Empty by design
    }
    
    /**
     * Creates a new instance
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     */
    public SquareMatrix2D(double x1, double x2, double y1, double y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    /**
     * Returns the determinant
     * @return
     */
    public double det() {
        return x1 * y2 - x2 * y1;
    }

    /**
     * Computes the inverse matrix
     */
    public void inverse() {
        double scalar = 1.0d / det();
        double x1 = this.x1;
        this.x1 = this.y2 * scalar;
        this.x2 = -this.x2 * scalar;
        this.y1 = -this.y1 * scalar;
        this.y2 = x1 * scalar;
    }

    /**
     * Returns a string representation
     */
    public String toString() {
        return "(" + x1 + ", " + x2 + "), (" + y1 + ", " + y2 + ")";
    }
}
