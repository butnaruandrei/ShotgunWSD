import configuration.WindowConfiguration;
import relatedness.embeddings.SenseEmbedding;

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

    public static void main(String[] args) {
        Integer[] ns = {4, 5, 6, 7, 8};
        Integer[] cs = {10,15,20};
        Integer[] ks = {1,5,10,15,20};
        // Integer[][] minMaxSynsetCollisions = { {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 3}, {2, 4}, {2, 5}, {3, 4}, {3, 5}, {4, 5} };
        Integer[][] minMaxSynsetCollisions = { {1, 1}, {2, 2}, {3, 3}, {4, 4}, {1, 2}, {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 4} };
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

        String[] shotgunArgs = new String[28];

        shotgunArgs[14] = "-wn";
        // shotgunArgs[15] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\dict1.7";
        shotgunArgs[15] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\dict1.7.1\\dict";
        shotgunArgs[16] = "-weType";
        shotgunArgs[17] = "Glove";
        shotgunArgs[18] = "-we";
        // shotgunArgs[19] = "H:\\GoogleNews-vectors-negative300.bin";
        // shotgunArgs[19] = "H:\\glove.42B.300d.txt";
        shotgunArgs[19] = "H:\\Senseval3-GN-CC840-vectors600.txt";
        shotgunArgs[20] = "-input";
        // shotgunArgs[21] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\data\\SemEval2007\\test\\eng-coarse-all-words.xml";
        shotgunArgs[21] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\data\\Senseval3\\data\\dataset.semcor.lexsn.xml";
        shotgunArgs[22] = "-output";
        shotgunArgs[23] = "F:\\Research\\ShotgunWSD-jurnal\\results\\Senseval3\\GN";
        shotgunArgs[24] = "-inputType";
        shotgunArgs[25] = "dataset-semcor";
        shotgunArgs[26] = "-outputType";
        shotgunArgs[27] = "dataset";

        shotgunArgs[10] = "-configurationOperationName";
        for (int m = 0; m < configurationOperationNames.length; m++) {
            shotgunArgs[11] = configurationOperationNames[m];

            shotgunArgs[12] = "-senseComputationMethod";
            for (int n = 0; n < senseComputationMethods.length; n++) {
                shotgunArgs[13] = senseComputationMethods[n];

                shotgunArgs[0] = "-n";
                for (int i = 0; i < ns.length; i++) {
                    shotgunArgs[1] = Integer.toString(ns[i]);

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
                                shotgunArgs[23] = "F:\\Research\\ShotgunWSD-jurnal\\results\\Senseval3\\GN-CC840\\n-" + ns[i] +
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
                                    CustomEmbeddings.writeWords(SenseEmbedding.allWords, "H:\\semeval2007-words.txt");
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
}
