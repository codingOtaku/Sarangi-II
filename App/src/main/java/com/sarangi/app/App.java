/*
 * @(#) App.java 2.0    June 9, 2016
 *
 * Mahendra Thapa
 *
 * Institue of Engineering
 */

package com.sarangi.app;

import java.io.*;
import java.util.*;

import com.sarangi.learningmodel.ann.*;
import com.sarangi.learningmodel.svm.*;
import com.sarangi.learningmodel.*;
import com.sarangi.structures.FeatureType;

/**
 * A main class for interfacing all the other sub-classes.
 *
 * <p>Includes the main method which extract the features of the training set and test set.
 *
 *
 *
 * @author  Mahendra Thapa
 *
 */

public class App
{

        /*CONSTRUCTORS **********************************************/

        private App(){

        }

        /**
         * Extract the features from the training and testing sets of data and stores in the
         * training.txt and testing.txt.
         *
         * @param   args    Take an argument from the command line terminal if any.
         *
         */

        public static void main( String[] args )
                throws FileNotFoundException, IOException
        {
                String trainingFilename = "src/resources/song/songFeatures/features.txt";
                String testFilename = "src/resources/song/songFeatures/test.txt";

               //FeatureExtractor.extractFeature(new String("src/resources/song/songFeatures/features.txt"),new String("src/resources/song/Mood_training"));
               //FeatureExtractor.extractFeature(new String("src/resources/song/songFeatures/test.txt"),new String("src/resources/song/Mood_testing"));

                //ClassifierRunner runner = new ClassifierRunner(new String[]{"classical","hiphop","jazz","pop","rock"});
                //runner.runCrossValidation(trainingFilename, FeatureType.SARANGI_MFCC,10,"SVM");
                
                TwoLayerClassifier classifier = new TwoLayerClassifier(new String[]{"classical","hiphop","jazz","pop","rock"});

                classifier.run(trainingFilename,testFilename,300);

        }
}
