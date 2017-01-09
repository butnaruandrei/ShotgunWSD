import configuration.WindowConfiguration;
import edu.smu.tspell.wordnet.WordNetDatabase;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import parsers.ParsedDocument;
import utils.SynsetUtils;

import java.io.File;
import java.io.IOException;
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

    // TODO check if we can remove this threshold
    private long maxSynsetCombinationNumber = 1000000000; // The maximum number of possible synset combinations that a context window can have

    public static void loadWordEmbeddings(String wePath, String weType) {
        try {
            switch (weType) {
                case "Google":
                    ShotgunWSDRunner.wordVectors = WordVectorSerializer.loadGoogleModel(new File(wePath), true);
                    break;
                case "Glove":
                    ShotgunWSDRunner.wordVectors = WordVectorSerializer.loadTxtVectors(new File(wePath));
                    break;
                default:
                    System.out.println("Word Embeddings type is invalid! " + weType + " is not a valid type. Please use Google or Glove model.");
                    System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("Could not find Word Embeddings file in " + wePath);
        }
    }

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
    public ShotgunWSDRunner(ParsedDocument document, int windowSize, int numberConfigs) {
        this.document = document;
        this.windowSize = windowSize;
        this.numberConfigs = numberConfigs;
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
        long combinations;
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

            ShotgunWSDLocal localWSD = new ShotgunWSDLocal(wordIndex, windowWords, windowWordsPOS, numberConfigs);
            localWSD.run(wnDatabase, wordVectors);
            windowSolutions = localWSD.getWindowSolutions();

            documentWindowSolutions.put(wordIndex, windowSolutions);
        }

        return documentWindowSolutions;
    }
}

