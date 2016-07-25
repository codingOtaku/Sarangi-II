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
import org.apache.commons.math3.linear.*;

import com.telmomenezes.jfastemd.*;

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
import java.lang.Math;

/**
 * Feature Class for one Gaussian Distribution in a song
 *
 * @author Bijay Gurung
 * @version 2.0
 *
 */

public class ClusterGMM implements Feature {
        /*FIELDS**********************************************************/

        /**
         * Logger is used to maintain the log of the program. The log contain the error message generated during the
         * execution of the program, warning messages to the user and information about the status of the program
         * to the user. The log is also beneficial during program debugging.
         */
        private Logger logger = Logger.getLogger("ClusterGMM");

        /**
         * The distribution of one of the clusters of the Song
         *
         */
        public MultivariateNormalDistribution distribution;



        /*CONSTRUCTORS****************************************************/

        /**
         * Accept a MultivariateNormalDistribution object.
         * Also, set the log level.
         * The log levels are SEVERE, WARNING and INFO mainly.
         *
         * @param distribution The Multi variate Normal Distribution
         */

        public ClusterGMM(MultivariateNormalDistribution distribution){

                this.distribution = distribution;
                logger.setLevel(Level.SEVERE);

        }

        /**
         * Calculate the ground distance between two ClusterGMM objects.
         * Uses Symmetric KL Divergence Distance
         *
         */
        public double groundDist(Feature c) {
                ClusterGMM cgmm = (ClusterGMM) c;

                return klDivergence(this,cgmm) + klDivergence(cgmm,this);

        }

        /**
         * Calculate KL Divergence between two ClusterGMM objects.
         * This is non-symmetric.
         *
         */
        public static double klDivergence(ClusterGMM cg1, ClusterGMM cg2) {
                double[] mu1 = cg1.distribution.getMeans();
                double[] mu2 = cg2.distribution.getMeans();

                RealMatrix cov1 = cg1.distribution.getCovariances();
                RealMatrix cov2 = cg2.distribution.getCovariances();

                RealMatrix mu1Mat = MatrixUtils.createRealMatrix(SongPCA.DIMENSIONS,1);
                RealMatrix mu2Mat = MatrixUtils.createRealMatrix(SongPCA.DIMENSIONS,1);

                mu1Mat.setColumn(0,mu1);
                mu2Mat.setColumn(0,mu2);
                RealMatrix muDif = mu2Mat.subtract(mu1Mat);
                RealMatrix muDifTranspose = muDif.transpose();

                // Use LUDecomposition to get determinant and inverse 
                LUDecomposition decM1 = new LUDecomposition(cov1);
                LUDecomposition decM2 = new LUDecomposition(cov2);
                double detCov1 = decM1.getDeterminant();
                double detCov2 = decM2.getDeterminant();
                RealMatrix inversetM2 = decM2.getSolver().getInverse();

                return (0.5*Math.log(detCov2/detCov1) + inversetM2.multiply(cov1).getTrace() + 
                                (muDifTranspose.multiply(inversetM2.multiply(muDif))).getEntry(0,0) - SongPCA.DIMENSIONS);

        }
}
