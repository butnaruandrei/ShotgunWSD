import configuration.WindowConfiguration;
import configuration.operations.ConfigurationOperation;
import edu.smu.tspell.wordnet.WordNetDatabase;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import parsers.ParsedDocument;
import relatedness.SynsetRelatedness;
import utils.SynsetUtils;

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

    private ConfigurationOperation configurationOperation;
    private SynsetRelatedness synsetRelatedness;

    // TODO check if we can remove this threshold
    private long maxSynsetCombinationNumber = 1000000000; // The maximum number of possible synset combinations that a context window can have

    public static void loadWordNet(String wnDirectory) {
        System.setProperty("wordnet.database.dir", wnDirectory);
        ShotgunWSDRunner.wnDatabase = WordNetDatabase.getFileInstance();
    }

    /**
     *
     * @param document      The document that we want to desambiguate
     * @param windowSize    Length of the context windows
     * @param numberConfigs Number of sense configurations considered for the voting scheme
     */
    public ShotgunWSDRunner(ParsedDocument document, int windowSize, int numberConfigs, ConfigurationOperation configurationOperation, SynsetRelatedness synsetRelatedness) {
        this.document = document;
        this.windowSize = windowSize;
        this.numberConfigs = numberConfigs;

        this.configurationOperation = configurationOperation;
        this.synsetRelatedness = synsetRelatedness;
    }

    public void run() {
        Hashtable<Integer, List<WindowConfiguration>> documentWindowSolutions = computeWindows();

        System.out.println("ok");
    }

    /**
     * TODO write docs
     */
    private Hashtable<Integer, List<WindowConfiguration>> computeWindows(){
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
}

