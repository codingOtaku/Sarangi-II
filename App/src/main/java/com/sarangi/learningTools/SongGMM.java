/*
 * (@)# SongGMM.java  2.0     July 20,2016.
 *
 * Bijay Gurung
 *
 * Institute of Engineering
 *
 */

package com.sarangi.learningTools;

import org.apache.commons.math3.distribution.*;
import org.apache.commons.math3.distribution.*;
import org.apache.commons.math3.distribution.fitting.*;
import org.apache.commons.math3.util.Pair;

import com.telmomenezes.jfastemd.*;

import smile.clustering.GMeans;
import com.sarangi.json.*;
import com.sarangi.structures.*;
import java.util.logging.*;
import java.io.*;
import java.util.*;
import java.lang.Object.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.reflect.*;
import java.lang.reflect.Type;

/**
 * Represents the GMM for a particular song.
 *
 * @author Bijay Gurung
 * @version 2.0
 *
 */

public class SongGMM {

        /*FIELDS**********************************************************/

        /**
         * Logger is used to maintain the log of the program. The log contain the error message generated during the
         * execution of the program, warning messages to the user and information about the status of the program
         * to the user. The log is also beneficial during program debugging.
         */
        private Logger logger = Logger.getLogger("SongGMM");

        /**
         * The Song to be processed
         *
         */
        private Song song;

        /**
         * The components representing the multivariate normal distribution of the song.
         *
         */
        private List<Pair<Double, MultivariateNormalDistribution>> components;

        public static int numOfClusters = 6;

        /*CONSTRUCTORS****************************************************/

        /**
         * Inject the song as dependency into the constructor. 
         * Also, set the log level.
         * The log levels are SEVERE, WARNING and INFO mainly.
         *
         * @param song The Song Object
         */

        public SongGMM(Song song){

                this.song = song;
                logger.setLevel(Level.SEVERE);

        }

        /**
         * Calculate GMM values
         *
         */
        public void calculateGMM() {


                double[][] mfccData = getMfccData(this.song);


                MultivariateNormalMixtureExpectationMaximization mmm = new MultivariateNormalMixtureExpectationMaximization(mfccData);
                
                // Make 6 components
                mmm.fit(MultivariateNormalMixtureExpectationMaximization.estimate(mfccData,numOfClusters));

                MixtureMultivariateNormalDistribution fittedModel = mmm.getFittedModel();

                this.components = fittedModel.getComponents();

                
        }

        /**
         * Get the jFastEMD Signature for the song.
         *
         */
        public Signature getSignature() {

            ClusterGMM[] clusters= new ClusterGMM[numOfClusters];
            double[] weights = new double[numOfClusters];

            for (int i=0; i<numOfClusters; i++) {
                    clusters[i] = new ClusterGMM(components.get(i).getSecond());
                    weights[i] = components.get(i).getFirst();
            }

            Signature signature = new Signature();
            signature.setNumberOfFeatures(numOfClusters);
            signature.setFeatures(clusters);
            signature.setWeights(weights);

            return signature;

        }

        /**
         * Returns the corresponding 2D double of the given Song
         * 
         * @param song Song object to be converted
         *
         * @return The corresponding 2D doubles array
         *
         */
        private static double[][] getMfccData(Song song) {

                List<float[]> mfccData = song.getMelcoeff();

                double[][] mfccArray = new double[mfccData.size()][];

                for (int i = 0; i < mfccData.size(); i++) {

                        mfccArray[i] = convertFloatsToDoubles(mfccData.get(i));

                }

                return mfccArray;
        }

        /**
         * Returns the corresponding MFCC array of the given MFCC Jama Matrix
         * 
         * @param mfccData 2D array of doubles
         *
         * @return Corresponding MFCC data array
         *
         */
        private static List<float[]> convertMfccToList(double[][] mfccData) {

                List<float[]> mfccListData = new ArrayList<float[]>();

                for(int r = 0; r < mfccData.length; r++){
                        float[] tempArray = new float[mfccData[r].length];
                    for(int c = 0; c < mfccData[r].length; c++){
                            tempArray[c] = ((float)mfccData[r][c]);
                    }
                    mfccListData.add(tempArray);
                }

                return mfccListData;
        }

        /**
         * Return array of doubles using an array of floats
         * 
         * @param input Array of floats
         *
         * @return Array of doubles
         *
         */
        public static double[] convertFloatsToDoubles(float[] input) {
            if (input == null) {
                return null; // Or throw an exception - your choice
            }

            double[] output = new double[input.length];

            for (int i = 0; i < input.length; i++) {
                output[i] = input[i];
            }
            return output;
        }

        public void storeSongs(String filename) {

                //TODO JSONFormat is being instantiated unnecessarily.

                JSONFormat jsonFormat = new JSONFormat();

                //jsonFormat.convertArrayToJSON(this.allSongs,filename);
        }

}
