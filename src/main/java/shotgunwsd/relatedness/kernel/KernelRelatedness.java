package shotgunwsd.relatedness.kernel;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;
import shotgunwsd.parsers.ParsedDocument;
import shotgunwsd.relatedness.SynsetRelatedness;
import shotgunwsd.relatedness.embeddings.SenseEmbedding;
import shotgunwsd.relatedness.kernel.kmeans.DistanceFunction;
import shotgunwsd.relatedness.kernel.kmeans.EuclidianDistance;
import shotgunwsd.relatedness.kernel.method.PQKernel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class KernelRelatedness extends SynsetRelatedness {
    private static WordVectors wordVectors = null;
    public static HashMap<String, Integer> wordClusters;
    public static HashMap<String, Double[]> wordCentroids;
    private static int K = 40;
    private static DistanceFunction distanceFunction;
    static KernelRelatedness instance = null;

    protected KernelRelatedness(){}
    public static KernelRelatedness getInstance(String wePath, String weType, int K) {
        return getInstance(wePath, weType, K, new EuclidianDistance());
    }

    public static KernelRelatedness getInstance(String wePath, String weType, int K, DistanceFunction distanceFunction) {
        if(instance == null) {
            instance = new KernelRelatedness();
        }

        if(wordVectors == null) {
            KernelRelatedness.loadWordEmbeddings(wePath, weType);
        }

        KernelRelatedness.K = K;
        KernelRelatedness.distanceFunction = distanceFunction;

        return instance;
    }

    public void computeClusters(WordNetDatabase wnDatabase, ParsedDocument document) {
        KernelRelatedness.wordClusters = ClusterRepresentation.computeClusters(wnDatabase, wordVectors, distanceFunction, K, document);
    }

    public void setWordClusters(HashMap<String, Integer> wordClusters) {
        KernelRelatedness.wordClusters = wordClusters;
    }

    public double computeSimilarity(Object[] synsetRepresentation, String[] windowWords, int[] synset2WordIndex, int k, int j){
        double[][] histograms = (double[][])synsetRepresentation;

        return PQKernel.pqk(histograms[k], histograms[j]);
    }

    public double computeSimilarity(Synset synset1, String word1, Synset synset2, String word2){
        double[][] senseRepresentations = new double[2][];
        senseRepresentations[0] = ClusterRepresentation.getRepresentation(wordClusters, K, synset1, word1);
        senseRepresentations[1] = ClusterRepresentation.getRepresentation(wordClusters, K, synset2, word2);

        return computeSimilarity(senseRepresentations, null, null, 0, 1);
    }

    /**
     * Generates the histogram for each synset of every word from the context window
     */
    public Object[] computeSynsetRepresentations(Synset[] windowWordsSynsets, String[] windowWords, int[] synset2WordIndex) {
        Object[] windowWordsSenseHistograms = new double[windowWordsSynsets.length][];
        for (int k = 0; k < windowWordsSynsets.length; k++) {
            windowWordsSenseHistograms[k] = ClusterRepresentation.getRepresentation(wordClusters, K, windowWordsSynsets[k], windowWords[synset2WordIndex[k]]);
        }

        return windowWordsSenseHistograms;
    }

    public static void loadWordEmbeddings(String wePath, String weType) {
        try {
            switch (weType) {
                case "Google":
                    KernelRelatedness.wordVectors = WordVectorSerializer.loadGoogleModel(new File(wePath), true);
                    break;
                case "Glove":
                    KernelRelatedness.wordVectors = WordVectorSerializer.loadTxtVectors(new File(wePath));
                    break;
                default:
                    System.out.println("Word Embeddings type is invalid! " + weType + " is not a valid type. Please use Google or Glove model.");
                    System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("Could not find Word Embeddings file in " + wePath);
        }
    }
}
