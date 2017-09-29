package relatedness.kernel;

import ca.pjer.ekmeans.EKmeans;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import parsers.ParsedDocument;
import relatedness.embeddings.sense.bags.AdjectiveSenseBag;
import relatedness.embeddings.sense.bags.AdverbSenseBag;
import relatedness.embeddings.sense.bags.NounSenseBag;
import relatedness.embeddings.sense.bags.VerbSenseBag;
import relatedness.kernel.kmeans.CosineDistance;
import relatedness.kernel.kmeans.DistanceFunction;
import relatedness.kernel.kmeans.KMeans;
import utils.SynsetUtils;
import utils.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class ClusterRepresentation {
    public static HashMap<String, Integer> computeClusters(WordNetDatabase wnDatabase, WordVectors wordVectors, DistanceFunction distanceFunction, int numberOfClusters, ParsedDocument document) {
        ArrayList<String> wordBag = new ArrayList<>();
        Synset[] tmpSynsets;

        for(String word : document.getWords()) {
            tmpSynsets = WordUtils.getSynsetsFromWord(wnDatabase, word, null);

            for(Synset synset : tmpSynsets) {
                wordBag.addAll(Arrays.asList(getSenseBag(synset, word)));
            }
        }

        String[] words = wordBag.stream()
                .distinct()
                .filter(ClusterRepresentation::isPresent)
                .filter(wordVectors::hasWord)
                .toArray(String[]::new);

        double[][] wordEmbeddings = wordsToBoWE(wordVectors, words);

        System.out.println("[START] Building clusters");
        int[] assignments = buildClusters(wordEmbeddings, distanceFunction, words, numberOfClusters);
        System.out.println("{DONE] Building clusters");

        HashMap<String, Integer> mappings = new HashMap<>();
        for (int i = 0; i < assignments.length; i++) {
            mappings.put(words[i], assignments[i]);
        }

        // displayClusters(assignments, words, numberOfClusters);

        return mappings;
    }

    public static HashMap<String, Double[]> computeCentroids(WordNetDatabase wnDatabase, WordVectors wordVectors, DistanceFunction distanceFunction, int numberOfClusters, ParsedDocument document) {
        ArrayList<String> wordBag = new ArrayList<>();
        Synset[] tmpSynsets;

        for(String word : document.getWords()) {
            tmpSynsets = WordUtils.getSynsetsFromWord(wnDatabase, word, null);

            for(Synset synset : tmpSynsets) {
                wordBag.addAll(Arrays.asList(getSenseBag(synset, word)));
            }
        }

        String[] words = wordBag.stream()
                .distinct()
                .filter(ClusterRepresentation::isPresent)
                .filter(wordVectors::hasWord)
                .toArray(String[]::new);

        double[][] wordEmbeddings = wordsToBoWE(wordVectors, words);

        System.out.println("[START] Building clusters");
        HashMap<String, Double[]> mappings = buildCentroids(wordEmbeddings, distanceFunction, words, numberOfClusters);
        System.out.println("{DONE] Building clusters");

        return mappings;
    }

    public static double[] getRepresentation(HashMap<String, Integer> wordClusters, int numberOfClusters, Synset synset, String word) {
        double[] representation = new double[numberOfClusters];

        for (int i = 0; i < numberOfClusters; i++) {
            representation[i] = 0;
        }

        String[] senseBag = getSenseBag(synset, word);

        for (String aSenseBag : senseBag) {
            if(wordClusters.containsKey(aSenseBag))
                representation[wordClusters.get(aSenseBag)]++;
        }

        return representation;
    }

    private static HashMap<String, Double[]> buildCentroids(double[][] wordEmbeddings, DistanceFunction distanceFunction, String[] words, int k) {
        KMeans kmeans = new KMeans(wordEmbeddings, k);
        kmeans.setDistanceFunction(distanceFunction);
        kmeans.run();

        int[] assignments = kmeans.get_assignedCentroid();
        double[][] centroids = kmeans.get_centroids();

        HashMap<String, Double[]> wordCentroids = new HashMap<>();

        for (int i = 0; i < words.length; i++) {
            wordCentroids.put(words[i],  ArrayUtils.toObject(centroids[assignments[i]]));
        }

        return wordCentroids;
    }

    private static int[] buildClusters(double[][] wordEmbeddings, DistanceFunction distanceFunction, String[] words, int k) {
        KMeans kmeans = new KMeans(wordEmbeddings, k);
        kmeans.setDistanceFunction(distanceFunction);
        kmeans.run();

        int[] assignments = kmeans.get_assignedCentroid();

        // displayClusters(assignments, words, k);

        return assignments;
    }

    private static void displayClusters(int[] assignments, String[] words, int k) {
        ArrayList<String>[] centroids = new ArrayList[k];
        for (int i = 0; i < centroids.length; i++) {
            centroids[i] = new ArrayList<>();
        }

        for (int i = 0; i < words.length; i++) {
            centroids[assignments[i]].add(words[i]);
        }
        System.out.println();
    }

    private static boolean isPresent(String word) {
        return word != null;
    }

    public static double[][] wordsToBoWE(WordVectors wordVectors, String[] words) {
        double[][] result = Arrays.stream(words)
                .filter(ClusterRepresentation::isPresent)
                .filter(wordVectors::hasWord)
                .map(wordVectors::getWordVector)
                .toArray(double[][]::new);

        if(result.length == 0){
            result = new double[1][300];
            for (int i = 0; i < result[0].length; i++) {
                result[0][i] = 0;
            }
        }

        return result;
    }

    private static String[] getSenseBag(Synset synset, String word) {
        if(synset == null)
            return new String[0];

        SynsetType pos = synset.getType();

        if(pos == SynsetType.NOUN) {
            return NounSenseBag.getSenseBag(synset);
        } else if (pos == SynsetType.VERB) {
            return VerbSenseBag.getSenseBag(synset);
        } else if(pos == SynsetType.ADJECTIVE) {
            return AdjectiveSenseBag.getSenseBag(synset, word);
        } else if(pos == SynsetType.ADJECTIVE_SATELLITE) {
            return AdjectiveSenseBag.getSenseBag(synset, word);
        } else if(pos == SynsetType.ADVERB) {
            return AdverbSenseBag.getSenseBag(synset, word);
        }

        return new String[0];
    }
}
