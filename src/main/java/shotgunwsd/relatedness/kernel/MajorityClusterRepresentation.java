package shotgunwsd.relatedness.kernel;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import shotgunwsd.parsers.ParsedDocument;
import shotgunwsd.relatedness.kernel.kmeans.DistanceFunction;
import shotgunwsd.utils.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class MajorityClusterRepresentation extends ClusterRepresentation {
    public static HashMap<String, Double[]> ccomputeWordRepresentations(WordNetDatabase wnDatabase, WordVectors wordVectors, DistanceFunction distanceFunction, int numberOfClusters, double wordPercentage, ParsedDocument document) {
        HashMap<String, Double[]> clusters = new HashMap<>();
        int[] clusterSize = new int[numberOfClusters];
        for (int i = 0; i < numberOfClusters; i++) {
            clusterSize[i] = 0;
        }

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

        for (int assignment : assignments) {
            clusterSize[assignment]++;
        }

        double mean = 0d;
        for (int aClusterSize : clusterSize) {
            mean += aClusterSize;
        }

        mean /= numberOfClusters;

        for (int i = 0; i < assignments.length; i++) {
            if(clusterSize[assignments[i]] >= mean * wordPercentage) {
                clusters.put(words[i], ArrayUtils.toObject(wordEmbeddings[i]));
            }
        }

        return clusters;
    }
}
