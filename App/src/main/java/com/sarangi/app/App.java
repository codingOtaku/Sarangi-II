/*
 * @(#) App.java 2.0    June 9, 2016
 *
 * Mahendra Thapa
 *
 * Institue of Engineering
 */

package com.sarangi.app;

import com.sarangi.json.SongLoader;
import com.sarangi.learningTools.*;
import com.sarangi.structures.Song;

import com.telmomenezes.jfastemd.*;

import java.util.*;

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
        {

                FeatureExtractor featureExtractor = new FeatureExtractor();

                String fileWithoutPCA = new String("src/resources/song/songFeatures/sampleSongs.txt");
                String fileWithPCA = new String("src/resources/song/songFeatures/sampleSongsPCA.txt");
                //featureExtractor.extractFeature(new String("src/resources/song/songFeatures/features.txt"),new String("src/resources/song/training"));
                featureExtractor.extractFeature(fileWithoutPCA,new String("src/resources/song/testing"));

                SongLoader songLoader = new SongLoader(fileWithoutPCA);
                songLoader.loadSongs();

                SongPCA songPCA = new SongPCA(songLoader.getSongs());
                songPCA.runPCA();
                songPCA.storeSongs(fileWithPCA);

                songLoader.setFilename(fileWithPCA);
                songLoader.loadSongs();

                List<Song> allSongs = songLoader.getSongs();
                
                SongGMM songGMM1 = new SongGMM(allSongs.get(0));
                songGMM1.calculateGMM();
                SongGMM songGMM2 = new SongGMM(allSongs.get(1));
                songGMM2.calculateGMM();

                Signature sig1 = songGMM1.getSignature();
                Signature sig2 = songGMM2.getSignature();

                System.out.println(JFastEMD.distance(sig1,sig2,-1));

        }
}
