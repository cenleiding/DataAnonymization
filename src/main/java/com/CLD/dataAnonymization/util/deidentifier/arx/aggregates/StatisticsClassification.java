/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2018 Fabian Prasser and contributors
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
package com.CLD.dataAnonymization.util.deidentifier.arx.aggregates;

import cern.colt.GenericSorting;
import cern.colt.Swapper;
import cern.colt.function.IntComparator;
import com.CLD.dataAnonymization.util.deidentifier.arx.ARXClassificationConfiguration;
import com.CLD.dataAnonymization.util.deidentifier.arx.ARXFeatureScaling;
import com.CLD.dataAnonymization.util.deidentifier.arx.DataHandleInternal;
import com.CLD.dataAnonymization.util.deidentifier.arx.aggregates.classification.*;
import com.CLD.dataAnonymization.util.deidentifier.arx.common.WrappedBoolean;
import com.CLD.dataAnonymization.util.deidentifier.arx.common.WrappedInteger;
import com.CLD.dataAnonymization.util.deidentifier.arx.exceptions.ComputationInterruptedException;
import com.CLD.dataAnonymization.util.deidentifier.arx.exceptions.UnexpectedErrorException;

import java.text.ParseException;
import java.util.*;

/**
 * Statistics representing the performance of various classifiers
 * 
 * @author Fabian Prasser
 * @author Johanna Eicher
 */
public class StatisticsClassification {

    /**
     * A ROC curve
     * 
     * @author Fabian Prasser
     */
    public static class ROCCurve {

        /** True positives */
        private final double[] truePositive;
        /** False positives */
        private final double[] falsePositive;
        /** AUC */
        private final double   AUC;
        /** Sensitivity */
        private double         sensitivity;
        /** Specificity */
        private double         specificity;
        /** Brier score */
        private final double   brierScore;
        
        /**
         * Creates a new ROC curve
         * @param value
         * @param confidences - (index, conf-1, ..., conf-numClasses), (index, conf-1, ..., numClasses), etc.
         * @param numClasses
         * @param valueIndex
         * @param handle
         * @param handleIndex
         */
        private ROCCurve(String value,
                         double[] confidences,
                         int numClasses,
                         int valueIndex,
                         DataHandleInternal handle,
                         int handleIndex) {
            
            int numSamples = confidences.length / (numClasses + 1);
            int valueID = handle.getValueIdentifier(handleIndex, value); // Value in 1-vs-all
            final boolean[] isPositive = new boolean[numSamples]; // isPositive[n]
            final double[] confidence = new double[numSamples]; // confidence[n]

            int positives = 0;
            int tp = 0;
            int tn = 0;
            int fp = 0;
            int fn = 0;
            double brier = 0;
            
            int j = 0;
            
            // For each record
            for (int i = 0; i < confidences.length; i += (numClasses + 1)) {
                
                // Prepare
                int index = (int)confidences[i];
                isPositive[j] = (handle.getEncodedValue(index, handleIndex, true) == valueID);
                confidence[j] = confidences[i + 1 + valueIndex];
                positives += isPositive[j] ? 1 : 0;
                
                // Determine predicted value
                double max = Double.MIN_VALUE;
                int maxIndex = -1;
                for (int k = 0; k < numClasses; k++) {
                    double probability = confidences[i + 1 + k];
                    if (probability == max) {
                        maxIndex = -1; // Reset, if ambiguous
                    } else if (probability > max) {
                        max = probability;
                        maxIndex = k;
                    }
                }
                boolean isPredictedPositive = maxIndex == valueIndex;
                
                // If the prediction was unambiguous 
                if (maxIndex != -1) {
                    tp += isPredictedPositive && isPositive[j] ? 1 : 0;
                    tn += !isPredictedPositive && !isPositive[j] ? 1 : 0;
                    fp += isPredictedPositive && !isPositive[j] ? 1 : 0;
                    fn += !isPredictedPositive && isPositive[j] ? 1 : 0;
                    brier += Math.pow(confidence[j] - (isPositive[j] ? 1 : 0), 2);
                }
                j++;
            }
            
            // Compute performance measures
            sensitivity = (double) tp / (tp + fn);
            specificity = (double) tn / (fp + tn);
            brierScore = (double) brier / (double)numSamples;
            
            // Sort by confidence
            GenericSorting.mergeSort(0, isPositive.length, new IntComparator() {
                @Override public int compare(int arg0, int arg1) {
                    return Double.compare(confidence[arg0], confidence[arg1]);
                }
            }, new Swapper() {
                @Override public void swap(int arg0, int arg1) {
                    double temp = confidence[arg0];
                    confidence[arg0] = confidence[arg1];
                    confidence[arg1] = temp;
                    boolean temp2 = isPositive[arg0];
                    isPositive[arg0] = isPositive[arg1];
                    isPositive[arg1] = temp2;
                }
            });
            
            // Initialize curve
            truePositive = new double[numSamples];
            falsePositive = new double[numSamples];
            
            // Draw curve
            int negatives = numSamples - positives;
            int x = 0;
            int y = 0;
            int offset=0;
            for (int i = isPositive.length - 1; i >= 0; i--) {
                x += isPositive[i] ? 0 : 1;
                y += isPositive[i] ? 1 : 0;
                falsePositive[offset] = (double)x/(double)(negatives);
                truePositive[offset] = (double)y/(double)positives;
                offset++;
            }

            // Calculate AUC: trapezoidal rule
            double AUC = 0d;
            for (int i=0; i<truePositive.length-1; i++) {
                double minX = Math.min(falsePositive[i], falsePositive[i + 1]);
                double maxX = Math.max(falsePositive[i], falsePositive[i + 1]);
                double minY = Math.min(truePositive[i], truePositive[i + 1]);
                double maxY = Math.max(truePositive[i], truePositive[i + 1]);
                AUC += (maxX - minX) * (minY + maxY) / 2d;
            }
            this.AUC = AUC;
        }

        /**
         * Returns the AUC
         * @return
         */
        public double getAUC() {
            return AUC;
        }
        
        /**
         * Returns the Brier score.
         * @return
         */
        public double getBrierScore() {
            return brierScore;
        }

        /**
         * Returns false-positive rates for all cut-off points
         * @return the false positive rate
         */
        public double[] getFalsePositiveRate() {
            return falsePositive;
        }
        
        /**
         * Return the sensitivity = tp / (tp + fn).
         * @return the sensitivity
         */
        public double getSensitivity() {
            return sensitivity;
        }
        
        /**
         * Returns the specificity = tn / (fp + tn).
         * @return the specificity
         */
        public double getSpecificity() {
            return specificity;
        }

        /**
         * Returns true-positive rates for all cut-off points
         * @return the true positive rate
         */
        public double[] getTruePositiveRate() {
            return truePositive;
        }
    }
    
    /**
     * Returns the classification method for the given config
     * @param specification
     * @param config
     * @return
     */
    private static ClassificationMethod getClassifier(ClassificationDataSpecification specification,
                                                      ARXClassificationConfiguration<?> config) {
        if (config instanceof ClassificationConfigurationLogisticRegression) {
            return new MultiClassLogisticRegression(specification, (ClassificationConfigurationLogisticRegression)config);
        } else if (config instanceof ClassificationConfigurationNaiveBayes) {
            System.setProperty("smile.threads", "1");
            return new MultiClassNaiveBayes(specification, (ClassificationConfigurationNaiveBayes)config);
        } else if (config instanceof ClassificationConfigurationRandomForest) {
            System.setProperty("smile.threads", "1");
            return new MultiClassRandomForest(specification, (ClassificationConfigurationRandomForest)config);
        } else {
            throw new IllegalArgumentException("Unknown type of configuration");
        }
    }
    
    /** Interrupt flag */
    private final WrappedBoolean  interrupt;
    /** Interrupt flag */
    private final WrappedInteger  progress;
    /** Number of classes */
    private int                   numClasses;
    /** Number of samples*/
    private int                   numSamples;
    /** Random */
    private final Random          random;
    /** Measurements */
    private int                   numMeasurements;

    /** ZeroR accuracy */
    private double                zeroRAccuracy;
    /** ZeroR average error */
    private double                zeroRAverageError;
    /** ZeroR ROC curve */
    private Map<String, ROCCurve> zerorROC    = new HashMap<>();

    /** Original/Output accuracy */
    private double                accuracy;
    /** Original/Output average error */
    private double                averageError;
    /** Original/Output ROC curve */
    private Map<String, ROCCurve> ROC   = new HashMap<>();

    /** Original accuracy */
    private double                originalAccuracy;
    /** Original accuracy */
    private double                originalAverageError;
    /** Original ROC curve */
    private Map<String, ROCCurve> originalROC = new HashMap<>();

    /**
     * Creates a new set of statistics for the given classification task
     * @param inputHandle - The input features handle
     * @param outputHandle - The output features handle
     * @param features - The feature attributes
     * @param clazz - The class attributes
     * @param config - The configuration
     * @param scaling 
     * @param interrupt - The interrupt flag
     * @param progress 
     * @throws ParseException 
     */
    StatisticsClassification(DataHandleInternal inputHandle,
                             DataHandleInternal outputHandle,
                             String[] features,
                             String clazz,
                             ARXClassificationConfiguration<?> config,
                             ARXFeatureScaling scaling, 
                             WrappedBoolean interrupt,
                             WrappedInteger progress) throws ParseException {

        // Init
        this.interrupt = interrupt;
        this.progress = progress;
        
        // Number of records to consider
        this.numSamples = getNumSamples(inputHandle.getNumRows(), config);
        
        // Initialize random
        if (!config.isDeterministic()) {
            this.random = new Random();
        } else {
            this.random = new Random(config.getSeed());
        }
        
        // Create specification
        ClassificationDataSpecification specification = new ClassificationDataSpecification(inputHandle, 
                                                                                            outputHandle, 
                                                                                            scaling,
                                                                                            features,
                                                                                            clazz,
                                                                                            interrupt);

        // Number of class values
        this.numClasses = specification.classMap.size();
        
        // Train and evaluate
        int k = numSamples > config.getNumFolds() ? config.getNumFolds() : numSamples;
        List<List<Integer>> folds = getFolds(inputHandle.getNumRows(), numSamples, k);

        // Track
        int classifications = 0;
        double total = 100d / ((double)numSamples * (double)folds.size());
        double done = 0d;
        
        // ROC
        double[] inputConfidences = new double[numSamples * ( 1 + numClasses)];
        double[] outputConfidences = (inputHandle == outputHandle) ? null : new double[numSamples * ( 1 + numClasses)];
        double[] zerorConfidences = new double[numSamples * ( 1 + numClasses)];
        int confidencesIndex = 0;
                
        // For each fold as a validation set
        for (int evaluationFold = 0; evaluationFold < folds.size(); evaluationFold++) {
            
            // Create classifiers
            ClassificationMethod inputClassifier = getClassifier(specification, config);
            ClassificationMethod inputZeroR = new MultiClassZeroR(specification);
            ClassificationMethod outputClassifier = null;
            if (inputHandle != outputHandle) {
                outputClassifier = getClassifier(specification, config);
            }
            
            // Try
            try {
                
                // Train with all training sets
                boolean trained = false;
                for (int trainingFold = 0; trainingFold < folds.size(); trainingFold++) {
                    if (trainingFold != evaluationFold) {                        
                        for (int index : folds.get(trainingFold)) {
                            checkInterrupt();
                            inputClassifier.train(inputHandle, outputHandle, index);
                            inputZeroR.train(inputHandle, outputHandle, index);
                            if (outputClassifier != null && !outputHandle.isOutlier(index)) {
                                outputClassifier.train(outputHandle, outputHandle, index);
                                trained = true;
                            }
                            this.progress.value = (int)((++done) * total);
                        }
                    }
                }
                
                // Close
                inputClassifier.close();
                inputZeroR.close();
                if (outputClassifier != null && trained) {
                    outputClassifier.close();
                }
                
                // Now validate
                for (int index : folds.get(evaluationFold)) {
                    
                    // Check
                    checkInterrupt();
                    
                    // Classify
                    ClassificationResult resultInput = inputClassifier.classify(inputHandle, index);
                    ClassificationResult resultInputZR = inputZeroR.classify(inputHandle, index);
                    ClassificationResult resultOutput = outputClassifier == null || !trained ? null : outputClassifier.classify(outputHandle, index);
                    classifications++;
                        
                    // Correct result
                    String actualValue = outputHandle.getValue(index, specification.classIndex, true);
                        
                    // Maintain data about ZeroR
                    this.zeroRAverageError += resultInputZR.error(actualValue);
                    this.zeroRAccuracy += resultInputZR.correct(actualValue) ? 1d : 0d;
                    double[] confidences = resultInputZR.confidences();
                    zerorConfidences[confidencesIndex] = index;
                    System.arraycopy(confidences, 0, zerorConfidences, confidencesIndex + 1, confidences.length);

                    // Maintain data about input-based classifier
                    boolean correct = resultInput.correct(actualValue);
                    this.originalAverageError += resultInput.error(actualValue);
                    this.originalAccuracy += correct ? 1d : 0d;
                    confidences = resultInput.confidences();
                    inputConfidences[confidencesIndex] = index;
                    System.arraycopy(confidences, 0, inputConfidences, confidencesIndex + 1, confidences.length);

                    // Maintain data about output-based                     
                    if (resultOutput != null) {
                        correct = resultOutput.correct(actualValue);
                        this.averageError += resultOutput.error(actualValue);
                        this.accuracy += correct ? 1d : 0d;
                        confidences = resultOutput.confidences();
                        outputConfidences[confidencesIndex] = index;
                        System.arraycopy(confidences, 0, outputConfidences, confidencesIndex + 1, confidences.length);
                    }
                        
                    // Next
                    confidencesIndex += numClasses + 1;
                    
                    this.progress.value = (int)((++done) * total);
                }
            } catch (Exception e) {
                if (e instanceof ComputationInterruptedException) {
                    throw e;
                } else {
                    throw new UnexpectedErrorException(e);
                }
            }
        }
        
        // Maintain data about inputZR
        this.zeroRAverageError /= (double)classifications;
        this.zeroRAccuracy/= (double)classifications;

        // Maintain data about inputLR
        this.originalAverageError /= (double)classifications;
        this.originalAccuracy /= (double)classifications;
        
        // Initialize ROC curves for zeroR
        for (String attr : specification.classMap.keySet()) {
            zerorROC.put(attr, new ROCCurve(attr,
                                            zerorConfidences,
                                            numClasses,
                                            specification.classMap.get(attr),
                                            outputHandle,
                                            specification.classIndex));
        }

        // Initialize ROC curves on original data
        for (String attr : specification.classMap.keySet()) {
            originalROC.put(attr, new ROCCurve(attr,
                                               inputConfidences,
                                               numClasses,
                                               specification.classMap.get(attr),
                                               outputHandle,
                                               specification.classIndex));
        }
        // Initialize ROC curves on anonymized data
        if (inputHandle != outputHandle) {
            for (String attr : specification.classMap.keySet()) {
                ROC.put(attr, new ROCCurve(attr,
                                           outputConfidences,
                                           numClasses,
                                           specification.classMap.get(attr),
                                           outputHandle,
                                           specification.classIndex));
            }
        }

        // Maintain data about outputLR                        
        if (inputHandle != outputHandle) {
            this.averageError /= (double)classifications;
            this.accuracy /= (double)classifications;
        } else {
            this.averageError = this.originalAverageError;
            this.accuracy = this.originalAccuracy;
        }
        
        this.numMeasurements = classifications;
    }

    /**
     * Returns the resulting accuracy. Obtained by generating a
     * classification model from the output (or input) dataset.
     * 
     * @return
     */
    public double getAccuracy() {
        return this.accuracy;
    }
    
    /**
     * Returns the average error, defined as avg(1d-probability-of-correct-result) for
     * each classification event.
     * 
     * @return
     */
    public double getAverageError() {
        return this.averageError;
    }

    /**
     * Returns the set of class attributes
     * @return
     */
    public Set<String> getClassValues() {
        return this.originalROC.keySet();
    }
    
    /**
     * Returns the number of classes
     * @return
     */
    public int getNumClasses() {
        return this.numClasses;
    }
    
    /**
     * Returns the number of measurements
     * @return
     */
    public int getNumMeasurements() {
        return this.numMeasurements;
    }

    /**
     * Returns the maximal accuracy. Obtained by generating a
     * classification model from the input dataset.
     * 
     * @return
     */
    public double getOriginalAccuracy() {
        return this.originalAccuracy;
    }

    /**
     * Returns the average error, defined as avg(1d-probability-of-correct-result) for
     * each classification event.
     * 
     * @return
     */
    public double getOriginalAverageError() {
        return this.originalAverageError;
    }
    
    /**
     * Returns the ROC curve for this class value calculated using the one-vs-all approach.
     * @param clazz
     * @return
     */
    public ROCCurve getOriginalROCCurve(String clazz) {
        return this.originalROC.get(clazz);
    }

    /**
     * Returns the ROC curve for this class value calculated using the one-vs-all approach.
     * @param clazz
     * @return
     */
    public ROCCurve getZeroRROCCurve(String clazz) {
        return this.zerorROC.get(clazz);
    }

    /**
     * Returns the ROC curve for this class value calculated using the one-vs-all approach.
     * @param clazz
     * @return
     */
    public ROCCurve getROCCurve(String clazz) {
        return this.ROC.get(clazz);
    }
    
    /**
     * Returns the minimal accuracy. Obtained by training a
     * ZeroR classifier on the input dataset.
     * 
     * @return
     */
    public double getZeroRAccuracy() {
        return this.zeroRAccuracy;
    }

    /**
     * Returns the average error, defined as avg(1d-probability-of-correct-result) for
     * each classification event.
     * 
     * @return
     */
    public double getZeroRAverageError() {
        return this.zeroRAverageError;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StatisticsClassification{\n");
        builder.append(" - Accuracy:\n");
        builder.append("   * Original: ").append(originalAccuracy).append("\n");
        builder.append("   * ZeroR: ").append(zeroRAccuracy).append("\n");
        builder.append("   * Output: ").append(accuracy).append("\n");
        builder.append(" - Average error:\n");
        builder.append("   * Original: ").append(originalAverageError).append("\n");
        builder.append("   * ZeroR: ").append(zeroRAverageError).append("\n");
        builder.append("   * Output: ").append(averageError).append("\n");
        builder.append(" - Number of classes: ").append(numClasses).append("\n");
        builder.append(" - Number of measurements: ").append(numMeasurements).append("\n");
        builder.append("}");
        return builder.toString();
    }
    
    /**
     * Checks whether an interruption happened.
     */
    private void checkInterrupt() {
        if (interrupt.value) {
            throw new ComputationInterruptedException("Interrupted");
        }
    }

    /**
     * Creates the folds
     * @param numRecords
     * @param numSamples
     * @param k
     * @return
     */
    private List<List<Integer>> getFolds(int numRecords, int numSamples, int k) {
        
        // Prepare indexes of all records
        List<Integer> rows = new ArrayList<>();
        for (int row = 0; row < numRecords; row++) {
            rows.add(row);
        }
        
        // Shuffle
        Collections.shuffle(rows, random);
        
        // Select subset of size numSamples
        rows = rows.subList(0, numSamples);

        // Create folds
        List<List<Integer>> folds = new ArrayList<>();
        int size = rows.size() / k;
        size = size > 1 ? size : 1;
        for (int i = 0; i < k; i++) {
            // For each fold
            int min = i * size;
            int max = (i + 1) * size;
            if (i == k - 1) {
                max = rows.size();
            }

            // Collect rows
            List<Integer> fold = new ArrayList<>();
            for (int j = min; j < max; j++) {
                fold.add(rows.get(j));
            }

            // Store
            folds.add(fold);
        }

        // Free
        rows.clear();
        rows = null;
        return folds;
    }
    
    /**
     * Returns the number of samples as the minimum of actual number of rows in
     * the dataset and maximal number of rows as specified in config.
     * 
     * @param numRows
     * @param config
     * @return
     */
    private int getNumSamples(int numRows, ARXClassificationConfiguration<?> config) {
        int numSamples = numRows;
        if (config.getMaxRecords() > 0) {
            numSamples = Math.min(config.getMaxRecords(), numSamples);
        }
        return numSamples;
    }
}