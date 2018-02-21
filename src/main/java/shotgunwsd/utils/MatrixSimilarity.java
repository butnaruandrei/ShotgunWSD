package shotgunwsd.utils;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import org.nd4j.linalg.factory.Nd4j;
import shotgunwsd.parsers.ParsedDocument;
import shotgunwsd.relatedness.SynsetRelatedness;
import shotgunwsd.relatedness.embeddings.SenseEmbedding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class MatrixSimilarity {
    private ParsedDocument _document;
    private double[][] _similarities;

    private Synset[] wordSynsets;
    private Integer[] synse2WordId;
    private String[] synsetIDs;
    private HashMap<String, Integer> reverseSynsetIDs;

    public static WordNetDatabase wnDatabase;
    private SynsetRelatedness synsetRelatedness;

    public MatrixSimilarity(ParsedDocument document, SynsetRelatedness synsetRelatedness) {
        this._document = document;

        this.synsetRelatedness = synsetRelatedness;

        computeSimilarityMatrix();
    }

    public double getSimilarity(String synsetID1, String synsetID2) {
        if(reverseSynsetIDs.get(synsetID1) == null || reverseSynsetIDs.get(synsetID2) == null) {
            return 0d;
        }
        return _similarities[reverseSynsetIDs.get(synsetID1)][reverseSynsetIDs.get(synsetID2)];
    }

    public double getSimilarity(Synset synset1, String word1, Synset synset2, String word2) {
        String synsetID1 = SynsetUtils.computeSynsetID(synset1, word1);
        String synsetID2 = SynsetUtils.computeSynsetID(synset2, word2);

        int index1 = reverseSynsetIDs.get(synsetID1);
        int index2 = reverseSynsetIDs.get(synsetID2);

        return _similarities[index1][index2];
    }

    private void computeSimilarityMatrix() {
        System.out.println("[START] Building similarity matrix");
        getAllSynsets();

        double[][] sims = new double[wordSynsets.length][wordSynsets.length];
        _similarities = new double[wordSynsets.length][wordSynsets.length];
        double sim;
        for (int i = 0; i < wordSynsets.length; i++) {
            for (int j = i; j < wordSynsets.length; j++) {
                sim = synsetRelatedness.computeSimilarity(wordSynsets[i], _document.getWord(synse2WordId[i]), wordSynsets[j], _document.getWord(synse2WordId[j]));
                sims[i][j] = sims[j][i] = sim;
            }
        }

        System.out.println("[DONE] Building similarity matrix");

        System.out.println("[START] Normalizing similarity matrix");
        for (int i = 0; i < wordSynsets.length; i++) {
            for (int j = 0; j < wordSynsets.length; j++) {
                _similarities[i][j] = sims[i][j] / Math.sqrt(sims[i][i] * sims[j][j]);
            }
        }
        System.out.println("[Done] Normalizing similarity matrix");

    }

    private void getAllSynsets() {
        ArrayList<Synset> synsets = new ArrayList<>();
        ArrayList<String> synsetIds = new ArrayList<>();
        ArrayList<Integer> words = new ArrayList<>();

        Synset[] tmpSynsets;
        String key;

        for (int i = 0; i < _document.wordsLength(); i++) {
            tmpSynsets = WordUtils.extractSynsets(wnDatabase, _document.getWord(i), POSUtils.asSynsetType(_document.getWordPos(i)));

            if(tmpSynsets.length == 0) {
                synsets.add(null);
                words.add(i);
                synsetIds.add(_document.getWord(i) + "-unknown");
            } else {
                for (Synset tmpSynset : tmpSynsets) {
                    key = SynsetUtils.computeSynsetID(tmpSynset, _document.getWord(i));
                    if (!synsetIds.contains(key)) {
                        synsets.add(tmpSynset);
                        words.add(i);
                        synsetIds.add(key);
                    }
                }
            }
        }

        this.wordSynsets = synsets.stream().toArray(Synset[]::new);
        this.synse2WordId = words.stream().toArray(Integer[]::new);
        this.synsetIDs = synsetIds.stream().toArray(String[]::new);

        System.out.println("Number of synsets: " + wordSynsets.length);

        reverseSynsetIDs = new HashMap<>();
        for (int i = 0; i < synsetIDs.length; i++) {
            reverseSynsetIDs.put(synsetIDs[i], i);
        }
    }
}
