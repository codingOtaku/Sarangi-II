/*
 * @(#) Song.java   2.0     June 9, 2016.
 *
 * Mahendra Thapa
 *
 * Institute of Engineering
 */

package com.sarangi.structures;

/**
 * A class for simple data structure to hold information related to a song.
 *
 * <p>Includes constructures for storing the song name and its audio features in corresponding fields.
 *
 * @author  Mahendra Thapa
 */

public class Song{

        /* FIELDS **********************************************/

        /**
         * Name of the song from which features is extracted.
         */
        protected String songName;

        /**
         * Intensity features of the given song.
         */
        protected double intensity;

        /* CONSTRUCTORS *****************************************/

        /**
         * Store the given audio features into the respected field.
         *
         * @param   songName        A reference to the name of the song.
         * 
         * @param   intensity       A reference to the intensity features of the song.
         */

        public Song(String songName, double intensity){

                this.songName = songName;
                this.intensity = intensity;

        }

        /**
         * Return the songName of the song.
         *
         * @return      The song name of the song.
         */

        public String getSongName(){
                return songName;
        }

        /**
         * Return the intensity features of the song.
         *
         * @return      Intensity features of the song.
         */
        public double getIntensity(){
                return intensity;
        }


}

