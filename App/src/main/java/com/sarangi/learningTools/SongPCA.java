/*
 * (@)# SongPCA.java  2.0     July 19,2016.
 *
 * Bijay Gurung
 *
 * Institute of Engineering
 *
 */

package com.sarangi.learningTools;

import com.mkobos.pca_transform.*;
import Jama.Matrix;
import com.sarangi.json.*;
import com.sarangi.structures.*;
import java.util.logging.*;
import java.io.*;
import java.util.*;

/**
 * A class for performing PCA on the Song Features.
 *
 * @author Bijay Gurung
 * @version 2.0
 *
 */

public class SongPCA {

        /*FIELDS**********************************************************/

        /**
         * Logger is used to maintain the log of the program. The log contain the error message generated during the
         * execution of the program, warning messages to the user and information about the status of the program
         * to the user. The log is also beneficial during program debugging.
         */
        private Logger logger = Logger.getLogger("SongPCA");

        /**
         * The Songs whose features are to be processed using PCA.
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

        public SongPCA(List<Song> allSongs){

                this.allSongs = allSongs;
                logger.setLevel(Level.SEVERE);

        }

        /**
         * Run PCA on the songs in allSongs.
         *
         */
        public void runPCA() {

                List<Song> tempSongs = new ArrayList<Song>();

                // For each Song
                for (Song song: allSongs) { 

                // Get Jama Matrix for given Song
                
                        Matrix mfccMatrix = getMfccMatrix(song);
                // Apply PCA to Matrix
                        PCA pca = new PCA(mfccMatrix);

                        Matrix transformedMatrix =
                               pca.transform(mfccMatrix, PCA.TransformationType.ROTATION);
                // Convert Jama Matrix back to Song

                        Song transformedSong = new Song(song.getSongName(),
                                                        song.getIntensity(),
                                                        getMfccDataFromMfccMatrix(transformedMatrix),
                                                        song.getPitch());

                // Add song to tempSongs
                        tempSongs.add(transformedSong);
                }

                // Assign tempSongs to allSongs

                allSongs = tempSongs;
                
        }

        /**
         * Returns the corresponding Jama Matrix of the given Song
         * 
         * @param song Song object to be converted
         *
         * @return The corresponding Jama Matrix
         *
         */
        private static Matrix getMfccMatrix(Song song) {

                List<float[]> mfccData = song.getMelcoeff();

                double[][] mfccArray = new double[mfccData.size()][];

                for (int i = 0; i < mfccData.size(); i++) {

                        mfccArray[i] = convertFloatsToDoubles(mfccData.get(i));

                }

                return new Matrix(mfccArray);
        }

        /**
         * Returns the corresponding MFCC array of the given MFCC Jama Matrix
         * 
         * @param mfccMatrix Jama Matrix for MFCC data
         *
         * @return Corresponding MFCC data array
         *
         */
        private static List<float[]> getMfccDataFromMfccMatrix(Matrix mfccMatrix) {

                List<float[]> mfccData = new ArrayList<float[]>();


                for(int r = 0; r < mfccMatrix.getRowDimension(); r++){
                        float[] tempArray = new float[mfccMatrix.getColumnDimension()];
                    for(int c = 0; c < mfccMatrix.getColumnDimension(); c++){
                            tempArray[c] = ((float)mfccMatrix.get(r, c));
                    }
                    mfccData.add(tempArray);
                }

                return mfccData;
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
