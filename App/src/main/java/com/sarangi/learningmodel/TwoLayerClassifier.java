/**
 * @(#) TwoLayerClassifier.java 2.0     July 31, 2016
 *
 * Bijay Gurung
 *
 * Insitute of Engineering
 */

package com.sarangi.learningmodel; 

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.sarangi.structures.*;
import com.sarangi.json.*;
import com.sarangi.learningmodel.*;

import smile.classification.SVM;
import smile.classification.NeuralNetwork;
import smile.math.kernel.GaussianKernel;
import smile.math.Math;
import java.lang.Math.*;
import java.util.*;
/**
 * Class for running two layers of classification.
 *
 * @author Bijay Gurung
 */


public class TwoLayerClassifier {

        protected String[] labels;

        public TwoLayerClassifier(String[] labels) {
        
                this.labels = labels;
        }

        /**
         * Run the given classifier. 
         *
         *
         * @param trainingFilename The file where the training dataset is stored.
         * @param testFilename The file where the test dataset is stored.
         * @param featureType The type of feature to be used for classification.
         * @param classifierType The type of classifier to run.
         *
         */

        public void run(String trainingFilename, String testFilename, int size1) throws FileNotFoundException, IOException  {

                SongHandler trainingSongHandler = new SongHandler(trainingFilename);
                List<Song> trainingSongs = trainingSongHandler.loadSongs();

                SongHandler testSongHandler = new SongHandler(testFilename);
                List<Song> testSongs = testSongHandler.loadSongs();


                // Get label-wise datasets
                
                List<List<Song>> labelSongs = DatasetUtil.getLabelWiseDataset(trainingSongs,this.labels);
                // Create two training sets.
                if (size1 > trainingSongs.size()) {
                        size1 = trainingSongs.size()/2;
                }

                int partitionSize = size1/this.labels.length;

                List<Song> trainingSet1 = new ArrayList<Song>();
                List<Song> trainingSet2 = new ArrayList<Song>();

                for (List<Song> oneLabelSongs: labelSongs) {
                        trainingSet1.addAll(new ArrayList<Song>(oneLabelSongs.subList(0,partitionSize)));
                        trainingSet2.addAll(new ArrayList<Song>(oneLabelSongs.subList(partitionSize,oneLabelSongs.size())));
                }

                // Train for the first time
                
                int numOfClassifiers = 2;

                SarangiClassifier[] classifiers = new SarangiClassifier[numOfClassifiers];

                ClassifierFactory factory = new ClassifierFactory();
                classifiers[0] = factory.getClassifier(trainingSet1,this.labels,FeatureType.SARANGI_MFCC, "SVM");
                classifiers[1] = factory.getClassifier(trainingSet1,this.labels,FeatureType.SARANGI_MFCC, "NeuralNetwork");

                // Run trainingset2 to create dataset for second classification
                
                LearningDataset learningDataset2 = getPredictedDataset(trainingSet2, classifiers, numOfClassifiers);

                // Train again
                
                SVM svm = new SVM(new GaussianKernel(60.0d), 2.0d, Math.max(learningDataset2.labelIndices)+1, SVM.Multiclass.ONE_VS_ONE);
                svm.learn(learningDataset2.dataset,learningDataset2.labelIndices);
                svm.finish();

                //NeuralNetwork ann = new NeuralNetwork(NeuralNetwork.ErrorFunction.LEAST_MEAN_SQUARES,NeuralNetwork.ActivationFunction.LOGISTIC_SIGMOID,30,15,15);
                //ann.learn(learningDataset2.dataset,learningDataset2.labelIndices);

                // Test the second layer classification
                
                Result result = testClassifier(testSongs,classifiers,numOfClassifiers,svm);

                result.printData();

        }

        /**
         * TODO Clean this up.
         *
         *
         */
        public LearningDataset getPredictedDataset(List<Song> trainingSet2, SarangiClassifier[] classifiers, int numOfClassifiers) {
                double[][] dataset2 = new double[trainingSet2.size()][numOfClassifiers * this.labels.length];

                int[] dataset2Labels = new int[trainingSet2.size()];

                for (int i=0; i < trainingSet2.size(); i++) {
                        dataset2Labels[i] = DatasetUtil.getIndexOfLabel(trainingSet2.get(i).getSongName(),this.labels);

                        for (int j=0; j<numOfClassifiers; j++) {

                                int predictedIndex = classifiers[j].predict(trainingSet2.get(i)) - 1;

                                for (int k=0; k<labels.length; k++) {
                                        dataset2[i][j*numOfClassifiers+k] = (predictedIndex == k)? 1.0:0.0;
                                }
                                
                        }
                }

                LearningDataset learningDataset2 = new LearningDataset();
                learningDataset2.dataset = dataset2;
                learningDataset2.labelIndices = dataset2Labels;

                return learningDataset2;
        }

        public int predict(Song song,SarangiClassifier[] classifiers, int numOfClassifiers, SVM current) {
                List<Song> oneSong = new ArrayList<Song>();
                oneSong.add(song);

                // TODO Get a better solution than this Hacky one.
                LearningDataset songDataset = getPredictedDataset(oneSong,classifiers,numOfClassifiers);
                return current.predict(songDataset.dataset[0]);
        }

        public Result testClassifier(List<Song> testSongs,SarangiClassifier[] classifiers, int numOfClassifiers, SVM current) {
                   int correct = 0;
                    double[] labelAccuracy = new double[this.labels.length];
                    double[] labelCount = new double[this.labels.length];
                    int[][] confusionMatrix = new int[this.labels.length][this.labels.length];
                    double accuracy = 0.0;

                try{

                    for (Song song: testSongs) {

                            int labelIndex = DatasetUtil.getIndexOfLabel(song.getSongName(),this.labels);

                            labelCount[labelIndex-1]++;
                            int predictedLabel = this.predict(song,classifiers,numOfClassifiers,current);

                            confusionMatrix[labelIndex-1][predictedLabel-1]++;

                            if (predictedLabel == labelIndex){
                                    labelAccuracy[labelIndex-1]++;
                                    correct++;
                            }
                    }

                    int numOfSongs = testSongs.size();

                    accuracy = (100.0*correct/numOfSongs);

                    for (int i=0; i<labelAccuracy.length; i++) {
                            labelAccuracy[i] = (100.0*labelAccuracy[i]/labelCount[i]);
                    }


                }catch (Exception ex){
                        ex.printStackTrace();
                }
                    return new Result(accuracy,this.labels,labelAccuracy,confusionMatrix);
  
        }
}
