package shotgunwsd.relatedness.kernel;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import shotgunwsd.parsers.ParsedDocument;
import shotgunwsd.relatedness.embeddings.sense.bags.AdjectiveSenseBag;
import shotgunwsd.relatedness.embeddings.sense.bags.AdverbSenseBag;
import shotgunwsd.relatedness.embeddings.sense.bags.NounSenseBag;
import shotgunwsd.relatedness.embeddings.sense.bags.VerbSenseBag;
import shotgunwsd.relatedness.kernel.kmeans.DistanceFunction;
import shotgunwsd.relatedness.kernel.kmeans.KMeans;
import shotgunwsd.utils.WordUtils;

import java.util.*;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class ClusterRepresentation {
    public static HashMap<String, Integer> computeClusters(WordNetDatabase wnDatabase, WordVectors wordVectors, DistanceFunction distanceFunction, int numberOfClusters, double wordPercentage, ParsedDocument document) {
        ArrayList<String> wordBag = new ArrayList<>();
        Synset[] tmpSynsets;
        int[] clusterSize = new int[numberOfClusters];
        for (int i = 0; i < numberOfClusters; i++) {
            clusterSize[i] = 0;
        }

        for(String word : document.getWords()) {
            tmpSynsets = WordUtils.extractSynsets(wnDatabase, word, null);

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

        for (int assignment : assignments) {
            clusterSize[assignment]++;
        }

        double mean = 0d;
        for (int aClusterSize : clusterSize) {
            mean += aClusterSize;
        }

        mean /= numberOfClusters;

        HashMap<String, Integer> mappings = new HashMap<>();
        for (int i = 0; i < assignments.length; i++) {
            if(clusterSize[assignments[i]] >= mean * wordPercentage) {
                mappings.put(words[i], assignments[i]);
            }
        }

        // displayClusters(assignments, words, numberOfClusters);
        return mappings;
    }

    public static HashMap<String, Double[]> computeCentroids(WordNetDatabase wnDatabase, WordVectors wordVectors, DistanceFunction distanceFunction, int numberOfClusters, ParsedDocument document) {
        ArrayList<String> wordBag = new ArrayList<>();
        Synset[] tmpSynsets;

        for(String word : document.getWords()) {
            tmpSynsets = WordUtils.extractSynsets(wnDatabase, word, null);

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

    protected static HashMap<String, Double[]> buildCentroids(double[][] wordEmbeddings, DistanceFunction distanceFunction, String[] words, int k) {
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

    protected static int[] buildClusters(double[][] wordEmbeddings, DistanceFunction distanceFunction, String[] words, int k) {
        KMeans kmeans = new KMeans(wordEmbeddings, k);
        kmeans.setDistanceFunction(distanceFunction);
        kmeans.run();

        int[] assignments = kmeans.get_assignedCentroid();

        // displayClusters(assignments, words, k);

        return assignments;
    }

    protected static void displayClusters(int[] assignments, String[] words, int k) {
        ArrayList<String>[] centroids = new ArrayList[k];
        for (int i = 0; i < centroids.length; i++) {
            centroids[i] = new ArrayList<>();
        }

        for (int i = 0; i < words.length; i++) {
            centroids[assignments[i]].add(words[i]);
        }
        System.out.println();
    }

    protected static boolean isPresent(String word) {
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

    protected static String[] getSenseBag(Synset synset, String word) {
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
