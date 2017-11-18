package shotgunwsd;

import shotgunwsd.configuration.WindowConfiguration;
import it.unimi.dsi.fastutil.Hash;
import shotgunwsd.relatedness.embeddings.SenseEmbedding;
import shotgunwsd.utils.MatrixSimilarity;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class Automation {
    public static HashMap<String, Hashtable<Integer, List<WindowConfiguration>>> backupDocumentWindowSolutions;
    public static HashMap<String, HashMap<String, Integer>> backupWordCentroids;
    public static HashMap<String, HashMap<String, Double[]>> backupWordClusters;
    public static HashMap<String, MatrixSimilarity> backupMaxtrixSimilarity;

    public static void main(String[] args) {

        Integer[][] ns = {{4, 4}, {5, 5}, {6, 6}, {7, 7}};
        // Integer[][] ns = {{4, 5}, {4, 6}, {4, 7}, {5, 6}, {5, 7}, {6, 7}};
        // Integer[][] ns = {{4, 8}, {5, 8}, {6, 8}, {7, 8}};
        Integer[] cs = {5, 10, 15, 20};
        Integer[] ks = {1,5,10,15,20};
        Integer[][] minMaxSynsetCollisions = { {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 3}, {2, 4}, {2, 5}, {3, 4}, {3, 5}, {4, 5} };
        // Integer[][] minMaxSynsetCollisions = { {5, 5}, {1, 5}, {2, 5}, {3, 5}, {4, 5} };
        // Integer[][] minMaxSynsetCollisions = { {1, 1}, {2, 2}, {3, 3}, {4, 4}, {1, 2}, {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 4} };
        // Integer[][] minMaxSynsetCollisions = { {1, 1} };
//       String[] configurationOperationNames = {"add2", "log", "add"};
//        String[] senseComputationMethods = {"avg", "geo"};
        String[] configurationOperationNames = {"log"};
        String[] senseComputationMethods = {"avg"};

        String[] embeddingIDS = {
                "H:\\GoogleNews-vectors-negative300.bin",
                "H:\\glove.6B.300d.txt",
                "H:\\glove.42B.300d.txt",
                "H:\\glove.840B.300d.txt",
                "H:\\Senseval2-GN-Wiki-vectors600.txt",
                "H:\\Senseval2-GN-CC42-vectors600.txt",
                "H:\\Senseval2-GN-CC840-vectors600.txt"
        };

        String[] shotgunArgs = new String[30];

        shotgunArgs[14] = "-wn";
        shotgunArgs[15] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\dict2.1";
        // shotgunArgs[15] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\dict1.7.1\\dict";
        shotgunArgs[16] = "-weType";
        shotgunArgs[17] = "Google";
        shotgunArgs[18] = "-we";
        shotgunArgs[19] = "H:\\GoogleNews-vectors-negative300.bin";
        // shotgunArgs[19] = "H:\\Senseval3-GN-CC840-vectors600.txt";
        shotgunArgs[20] = "-input";
        shotgunArgs[21] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\data\\SemEval2007\\test\\eng-coarse-all-words.xml";
        // shotgunArgs[21] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\data\\Senseval2\\data\\dataset.semcor.lexsn.xml";
        // shotgunArgs[21] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\data\\Senseval3\\data\\dataset.semcor.lexsn.xml";
        shotgunArgs[22] = "-output";
        shotgunArgs[23] = "F:\\Research\\ShotgunWSD-jurnal\\results\\SemEval2007\\GN";
        shotgunArgs[24] = "-inputType";
        shotgunArgs[25] = "dataset-semeval2007";
        shotgunArgs[26] = "-outputType";
        shotgunArgs[27] = "dataset";


        shotgunArgs[10] = "-configurationOperationName";
        for (int m = 0; m < configurationOperationNames.length; m++) {
            shotgunArgs[11] = configurationOperationNames[m];

            shotgunArgs[12] = "-senseComputationMethod";
            for (int n = 0; n < senseComputationMethods.length; n++) {
                backupMaxtrixSimilarity = new HashMap<>();
                backupWordCentroids = new HashMap<>();
                backupWordClusters = new HashMap<>();

                shotgunArgs[13] = senseComputationMethods[n];

                shotgunArgs[0] = "-min_n";
                shotgunArgs[28] = "-max_n";
                for (int i = 0; i < ns.length; i++) {
                    shotgunArgs[1] = Integer.toString(ns[i][0]);
                    shotgunArgs[29] = Integer.toString(ns[i][1]);

                    shotgunArgs[4] = "-c";
                    for (int k = 0; k < cs.length; k++) {
                        shotgunArgs[5] = Integer.toString(cs[k]);

                        // TODO remove this
                        backupDocumentWindowSolutions = new HashMap<>();

                        shotgunArgs[2] = "-k";
                        for (int j = 0; j < ks.length; j++) {
                            shotgunArgs[3] = Integer.toString(ks[j]);

                            shotgunArgs[6] = "-minSynsetCollisions";
                            shotgunArgs[8] = "-maxSynsetCollisions";

                            for (int l = 0; l < minMaxSynsetCollisions.length; l++) {
                                shotgunArgs[7] = Integer.toString(minMaxSynsetCollisions[l][0]);
                                shotgunArgs[9] = Integer.toString(minMaxSynsetCollisions[l][1]);


                                // overwrite folder path
                                shotgunArgs[23] = "F:\\Research\\ShotgunWSD-jurnal\\results\\weighted\\SemEval2007\\GN\\matrix-optim\\test\\n-" + ns[i][0] + "-" + ns[i][1] +
                                        "-k-" + ks[j] +
                                        "-c-" + cs[k] +
                                        "-misc-" + minMaxSynsetCollisions[l][0] +
                                        "-masc-" + minMaxSynsetCollisions[l][1] +
                                        "-conf-" + configurationOperationNames[m] +
                                        "-comp-" + senseComputationMethods[n];

                                File outputFolder = new File(shotgunArgs[23]);

                                if (!outputFolder.exists()) {
                                    System.out.println(outputFolder);

                                    ShotgunWSD.main(shotgunArgs);
                                    // CustomEmbeddings.writeWords(SenseEmbedding.allWords, "H:\\semeval2007-words.txt");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static Hashtable<Integer, List<WindowConfiguration>> clone(Hashtable<Integer, List<WindowConfiguration>> obj) {
        Hashtable<Integer, List<WindowConfiguration>> newObj = new Hashtable<>();
        List<WindowConfiguration> list, clonedList;

        for(Integer key : obj.keySet()){
            list = obj.get(key);

            clonedList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                clonedList.add(list.get(i).clone());
            }

            newObj.put(key, clonedList);
        }

        return newObj;
    }

    public static List<WindowConfiguration> clone(List<WindowConfiguration> list) {
        List<WindowConfiguration> clonedList;

        clonedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            clonedList.add(list.get(i).clone());
        }

        return clonedList;
    }
}
