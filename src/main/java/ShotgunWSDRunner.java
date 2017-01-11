import configuration.WindowConfiguration;
import configuration.operations.ConfigurationOperation;
import edu.smu.tspell.wordnet.WordNetDatabase;
import it.unimi.dsi.fastutil.Hash;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import parsers.ParsedDocument;
import relatedness.SynsetRelatedness;
import utils.SynsetUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class ShotgunWSDRunner {
    private ParsedDocument document;
    public static WordVectors wordVectors;
    public static WordNetDatabase wnDatabase;

    private int windowSize;
    private int numberConfigs;
    private int minSynsetCollisions;
    private int maxSynsetCollisions;

    private ConfigurationOperation configurationOperation;
    private SynsetRelatedness synsetRelatedness;

    // TODO check if we can remove this threshold
    private long maxSynsetCombinationNumber = 1000000000; // The maximum number of possible synset combinations that a context window can have

    public static void loadWordNet(String wnDirectory) {
        System.setProperty("wordnet.database.dir", wnDirectory);
        ShotgunWSDRunner.wnDatabase = WordNetDatabase.getFileInstance();
    }

    /**
     * @param document      The document that we want to desambiguate
     * @param windowSize    Length of the context windows
     * @param numberConfigs Number of sense configurations considered for the voting scheme
     */
    public ShotgunWSDRunner(ParsedDocument document, int windowSize, int numberConfigs, int minSynsetCollisions, int maxSynsetCollisions, ConfigurationOperation configurationOperation, SynsetRelatedness synsetRelatedness) {
        this.document = document;
        this.windowSize = windowSize;
        this.numberConfigs = numberConfigs;
        this.minSynsetCollisions = minSynsetCollisions;
        this.maxSynsetCollisions = maxSynsetCollisions;

        this.configurationOperation = configurationOperation;
        this.synsetRelatedness = synsetRelatedness;
    }

    public void run() {
        Hashtable<Integer, List<WindowConfiguration>> documentWindowSolutions = computeWindows();

        mergeWindowSolutions(documentWindowSolutions);

        System.out.println("ok");
    }

    /**
     * TODO write docs
     */
    private Hashtable<Integer, List<WindowConfiguration>> computeWindows() {
        String[] windowWords, windowWordsPOS;
        long combinations = 0;
        List<WindowConfiguration> windowSolutions;
        Hashtable<Integer, List<WindowConfiguration>> documentWindowSolutions = new Hashtable<>();

        for (int wordIndex = 0; wordIndex <= document.wordsLength() - windowSize; wordIndex++) {
            windowWords = Arrays.copyOfRange(document.getWords(), wordIndex, wordIndex + windowSize);
            windowWordsPOS = Arrays.copyOfRange(document.getWordPos(), wordIndex, wordIndex + windowSize);

            combinations = SynsetUtils.numberOfSynsetCombination(wnDatabase, windowWords, windowWordsPOS);
            while (combinations > maxSynsetCombinationNumber) {
                windowWords = Arrays.copyOfRange(windowWords, 0, windowWords.length - 2);
                windowWordsPOS = Arrays.copyOfRange(windowWordsPOS, 0, windowWordsPOS.length - 2);
                combinations = SynsetUtils.numberOfSynsetCombination(wnDatabase, windowWords, windowWordsPOS);
            }

            System.out.println("Start Local ShotgunWSD from word " + wordIndex + "; Number of combinations: " + combinations);
            ShotgunWSDLocal localWSD = new ShotgunWSDLocal(wordIndex, windowWords, windowWordsPOS, numberConfigs, configurationOperation, synsetRelatedness);
            localWSD.run(wnDatabase);
            windowSolutions = localWSD.getWindowSolutions();

            documentWindowSolutions.put(wordIndex, windowSolutions);
        }

        return documentWindowSolutions;
    }

    private Hashtable<Integer, List<WindowConfiguration>> mergeWindowSolutions(Hashtable<Integer, List<WindowConfiguration>> documentWindowSolutions) {
        Hashtable<Integer, List<WindowConfiguration>> mergedWindows = null;

        for (int synsetCollisions = maxSynsetCollisions; synsetCollisions >= minSynsetCollisions; synsetCollisions--) {
            mergedWindows = mergeWindows(documentWindowSolutions, document.wordsLength(), synsetCollisions);
        }

        return mergedWindows;
    }

    private Hashtable<Integer, List<WindowConfiguration>> mergeWindows(Hashtable<Integer, List<WindowConfiguration>> documentWindowSolutions, int numberOfWords, int synsetCollisions){
        List<WindowConfiguration> configList1, configList2;
        WindowConfiguration window1, window2, mergedWindow;

        boolean collided = false;

        // l - the l word from the document
        for (int l = 0; l < numberOfWords - 1; l++) {
            if (documentWindowSolutions.containsKey(l)) {
                configList1 = documentWindowSolutions.get(l);

                // i - the i window of the l word
                for (int i = 0; i < configList1.size(); i++) {
                    window1 = configList1.get(i);

                    // j - the j word of the i window OR the (l + j) word of the document
                    for (int j = 0; j < window1.getLength() - synsetCollisions; j++) {
                        if(documentWindowSolutions.containsKey(j + l + 1)) {
                            configList2 = documentWindowSolutions.get(j + l + 1);

                            // k - the index of the window, of the (l + j) word from the document, we want to merge with
                            for (int k = 0; k < configList2.size(); k++) {
                                collided = false;
                                window2 = configList2.get(k);

                                if(WindowConfiguration.hasCollisions(window1, window2, j + 1, synsetCollisions)) {
                                    mergedWindow = WindowConfiguration.merge(window1, window2, j + 1);

                                    if (mergedWindow != null) {
                                        collided = true;
                                        configList1.add(mergedWindow);
                                    }

                                    configList2.remove(k);
                                    k--;
                                }
                            }
                        }
                    }

                    if(collided)
                        configList1.remove(i);
                }
            }
        }

        return documentWindowSolutions;
    }
}
