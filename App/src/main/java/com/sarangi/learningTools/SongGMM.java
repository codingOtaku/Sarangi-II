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
 * A class to determine GMM parameters for the songs.
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
         * The Songs whose to be processed
         *
         */
        private List<Song> allSongs;


        /*CONSTRUCTORS****************************************************/

        /**
         * Inject the songs as dependency into the constructor. 
         * Also, set the log level.
         * The log levels are SEVERE, WARNING and INFO mainly.
         *
         * @param allSongs All the songs
         */

        public SongGMM(List<Song> allSongs){

                this.allSongs = allSongs;
                logger.setLevel(Level.SEVERE);

        }

        /**
         * Calculate GMM values
         *
         */
        public void calculateGMM() {

                // For each Song
                for (Song song: allSongs) { 

                        double[][] mfccData = getMfccData(song);

                        /*
                        GMeans gmeans = new GMeans(mfccData,10);
                        System.out.println(gmeans.toString());
                        */

                        MultivariateNormalMixtureExpectationMaximization mmm = new MultivariateNormalMixtureExpectationMaximization(mfccData);
                        // Make 6 components
                        mmm.fit(MultivariateNormalMixtureExpectationMaximization.estimate(mfccData,6));

                        //System.out.println("Log Likelihood:"+mmm.getLogLikelihood());
                        //List<Pair<Double,double>> results = mmm.getComponents();
                        //MultivariateNormalMixtureExpectationMaximization fittedModel = mmm.getFittedModel();
                        MixtureMultivariateNormalDistribution fittedModel = mmm.getFittedModel();

                        final List<Pair<Double, MultivariateNormalDistribution>> components = fittedModel.getComponents();

                        ClusterGMM gm1 = new ClusterGMM(components.get(0).getSecond());
                        ClusterGMM gm2 = new ClusterGMM(components.get(1).getSecond());

                        System.out.println(gm1.groundDist(gm2));

                        /*
                        GsonBuilder gsonBuilder = new GsonBuilder();  
                        gsonBuilder.serializeSpecialFloatingPointValues();  
                        Gson gson = gsonBuilder.setPrettyPrinting().create();
                        System.out.println(gson.toJson(fittedModel.getComponents().get(0).getSecond().getCovariances()));
                        */
                }
                
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

                jsonFormat.convertArrayToJSON(this.allSongs,filename);
        }

}
