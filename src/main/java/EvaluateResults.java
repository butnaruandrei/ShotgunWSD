import java.io.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class EvaluateResults {
    public static void main(String[] args) {
        Integer[] ns = {5, 6, 7, 8};
        Integer[] cs = {15};
        Integer[] ks = {1, 5, 10, 15, 20};
        Integer[][] minMaxSynsetCollisions = { {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 3}, {2, 4}, {2, 5}, {3, 4}, {3, 5}, {4, 5} };
        String[] configurationOperationNames = {"add"};
        String[] senseComputationMethods = {"avg"};

        String outputPath;
        for (int m = 0; m < configurationOperationNames.length; m++) {
            System.out.print("\nconfigurationOperationName-" + configurationOperationNames[m]);
            for (int n = 0; n < senseComputationMethods.length; n++) {
                System.out.print("\nsenseComputationMethod-" + senseComputationMethods[n]);

                for (int k = 0; k < cs.length; k++) {
                    System.out.print("\nc-" + cs[k]);

                    for (int j = 0; j < ks.length; j++) {
                        System.out.print("\nk-" + ks[j]);

                        for (int l = 0; l < minMaxSynsetCollisions.length; l++) {

                            if(l == 0) {
                                System.out.print("\nwindow-size\t");
                                for (int i = 0; i < ns.length; i++) {
                                    System.out.print(ns[i] + "\t");
                                }
                                System.out.println("");
                            }

                            for (int i = 0; i < ns.length; i++) {
                                if(i == 0)
                                    System.out.print(minMaxSynsetCollisions[l][0] + "-" + minMaxSynsetCollisions[l][1] + "\t");

                                outputPath = "C:\\Users\\butna\\Desktop\\shotgunwsd\\n-" + ns[i] +
                                        "-k-" + ks[j] +
                                        "-c-" + cs[k] +
                                        "-misc-" + minMaxSynsetCollisions[l][0] +
                                        "-masc-" + minMaxSynsetCollisions[l][1] +
                                        "-conf-" + configurationOperationNames[m] +
                                        "-comp-" + senseComputationMethods[n];

                                File outputFolder = new File(outputPath);

                                if(outputFolder.exists()) {
                                    // System.out.println(outputFolder);
                                    mergeDocumentResults(outputPath);

                                    String[] cmd = {"perl", "C:/Users/butna/Desktop/dizertatie/WSD-GS/data/SemEval2007/new_scorer/scorer-for-java.pl", outputPath + "/results.txt"};
                                    Process p = null;
                                    try {
                                        p = Runtime.getRuntime().exec(cmd);
                                        p.waitFor();
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                                        double score = Double.parseDouble(reader.readLine());
                                        System.out.printf("%.3f\t", score);

                                    } catch (IOException | InterruptedException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                            System.out.println("");
                        }
                    }
                }
            }
        }
    }

    public static void mergeDocumentResults(String outputFolder) {
        // if(!(new File(outputFolder + "\\results.txt").exists())) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(outputFolder + "\\results.txt", "UTF-8");

                String currentLine, doc;

                Scanner resultsScanner = null;
                File folder = new File(outputFolder);
                File[] listOfFiles = folder.listFiles();
                for (File file : listOfFiles) {
                    if (file.isFile() && !Objects.equals(file.getName(), "README.txt") && !Objects.equals(file.getName(), "results.txt")) {
                        resultsScanner = new Scanner(file);

                        while (resultsScanner.hasNext()) {
                            currentLine = resultsScanner.nextLine();
                            writer.write(currentLine + "\n");
                        }
                    }
                }
                writer.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {

            }
        //}
    }
}
