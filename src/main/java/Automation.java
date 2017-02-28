import configuration.WindowConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class Automation {
    public static Hashtable<String, Hashtable<Integer, List<WindowConfiguration>>> documentWindowSolutions;

    public static void main(String[] args) {
        Integer[] ns = {5, 6};
        Integer[] cs = {15};
        Integer[] ks = {1};
        Integer[][] minMaxSynsetCollisions = { {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 3}, {2, 4}, {2, 5}, {3, 4}, {3, 5}, {4, 5} };
        String[] configurationOperationNames = {"add"};
        String[] senseComputationMethods = {"avg"};


        String[] shotgunArgs = new String[28];

        shotgunArgs[14] = "-wn";
        shotgunArgs[15] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\dict";
        shotgunArgs[16] = "-weType";
        shotgunArgs[17] = "Google";
        shotgunArgs[18] = "-we";
        shotgunArgs[19] = "H:\\GoogleNews-vectors-negative300.bin";
        shotgunArgs[20] = "-input";
        shotgunArgs[21] = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\data\\SemEval2007\\test\\eng-coarse-all-words.xml";
        shotgunArgs[22] = "-output";
        shotgunArgs[23] = "C:\\Users\\butna\\Desktop\\shotgunwsd";
        shotgunArgs[24] = "-inputType";
        shotgunArgs[25] = "dataset";
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

                        // reset cache! TODO remove this!!!!!!!!!!!!!!!!!!
                        documentWindowSolutions = new Hashtable<>();

                        shotgunArgs[2] = "-k";
                        for (int j = 0; j < ks.length; j++) {
                            shotgunArgs[3] = Integer.toString(ks[j]);

                            shotgunArgs[6] = "-minSynsetCollisions";
                            shotgunArgs[8] = "-maxSynsetCollisions";
                            for (int l = 0; l < minMaxSynsetCollisions.length; l++) {
                                shotgunArgs[7] = Integer.toString(minMaxSynsetCollisions[l][0]);
                                shotgunArgs[9] = Integer.toString(minMaxSynsetCollisions[l][1]);


                                // overwrite folder path
                                shotgunArgs[23] = "C:\\Users\\butna\\Desktop\\shotgunwsd\\n-" + ns[i] +
                                        "-k-" + ks[j] +
                                        "-c-" + cs[k] +
                                        "-misc-" + minMaxSynsetCollisions[l][0] +
                                        "-masc-" + minMaxSynsetCollisions[l][1] +
                                        "-conf-" + configurationOperationNames[m] +
                                        "-comp-" + senseComputationMethods[n];

                                File outputFolder = new File(shotgunArgs[23]);

                                // if(!outputFolder.exists()) {
                                    System.out.println(outputFolder);
                                    ShotgunWSD.main(shotgunArgs);
                                // }
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
