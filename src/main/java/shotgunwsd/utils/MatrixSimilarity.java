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

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class MatrixSimilarity {
    private ParsedDocument _document;
    private double[][] _similarities;

    private int[] wordsSynsetStart;
    private int[] wordsSynsetLength;
    protected int[] synset2WordIndex;
    protected Synset[] wordsSynsets;

    public static WordNetDatabase wnDatabase;
    private SynsetRelatedness synsetRelatedness;

    public MatrixSimilarity(ParsedDocument document, SynsetRelatedness synsetRelatedness) {
        this._document = document;

        this.synsetRelatedness = synsetRelatedness;

        computeSynsetSimilarityMatrix();
    }

    public double getSimilarity(int synsetIndex1, int synsetIndex2){
        return _similarities[synsetIndex1][synsetIndex2];
    }

    public double getSimilarity(String synsetIndex1, String synsetIndex2){
        return _similarities[getSynsetIndex(synsetIndex1)][getSynsetIndex(synsetIndex2)];
    }

    private int getSynsetIndex(String representation) {
        int wordIndex = Integer.parseInt(representation.split("-")[0]);
        int synsetOffset = Integer.parseInt(representation.split("-")[1]);

        return wordsSynsetStart[wordIndex] + synsetOffset;
    }

    private void computeSynsetSimilarityMatrix() {
        buildWindowSynsetsArray();
        buildSynsetMapping();

        double[][] sims = new double[wordsSynsets.length][wordsSynsets.length];
        _similarities = new double[wordsSynsets.length][wordsSynsets.length];
        double sim;
        for (int i = 0; i < wordsSynsets.length; i++) {
            for (int j = i; j < wordsSynsets.length; j++) {
                sim = synsetRelatedness.computeSimilarity(wordsSynsets[i], _document.getWord(synset2WordIndex[i]), wordsSynsets[j], _document.getWord(synset2WordIndex[j]));
                sims[i][j] = sims[j][i] = sim;
            }
        }

        for (int i = 0; i < wordsSynsets.length; i++) {
            for (int j = 0; j < wordsSynsets.length; j++) {
                _similarities[i][j] = sims[i][j] / (sims[i][i] * sims[j][j]);
            }
        }
    }

    public int getWordsSynsetStart(int wordId) {
        return wordsSynsetStart[wordId];
    }

    /**
     * Build an array that stores, for each synset the word from the context window that represents.
     */
    protected void buildSynsetMapping() {
        int lastIdx;
        synset2WordIndex = new int[wordsSynsets.length];
        for (int j = 0; j < wordsSynsetStart.length; j++) {
            lastIdx = j + 1 == wordsSynsetStart.length ? wordsSynsets.length : wordsSynsetStart[j + 1];
            for (int k = wordsSynsetStart[j]; k < lastIdx; k++) {
                synset2WordIndex[k] = j;
            }
        }

    }

    /**
     * This method build and store an array that contains al synsets for all the words in the context window.
     * This is usefully to keep a simple representation of a synset, and to access it fast, without the need to look in WordNet.
     */
    protected void buildWindowSynsetsArray() {
        wordsSynsetStart = new int[_document.wordsLength()];
        wordsSynsetLength = new int[_document.wordsLength()];

        ArrayList<Synset> windowSynsets = new ArrayList<>();

        Synset[] tmpSynsets;
        int synsetStartIndex = 0, synsetLength;

        // For each word in the context window.
        for (int j = 0; j < _document.wordsLength(); j++) {
            // Extract the words synsets
            tmpSynsets = WordUtils.extractSynsets(wnDatabase, _document.getWord(j), POSUtils.asSynsetType(_document.getWordPos(j)));

            // Insert there synsets to an array
            synsetLength = tmpSynsets.length;
            if (tmpSynsets.length == 0) {
                synsetLength += 1;
                windowSynsets.add(null);
            } else {
                windowSynsets.addAll(Arrays.asList(tmpSynsets));
            }

            // Set the start index and the number of synsets
            wordsSynsetStart[j] = synsetStartIndex;
            wordsSynsetLength[j] = synsetLength;

            // Increment the index position, for the new word
            synsetStartIndex += synsetLength;
        }

        wordsSynsets = new Synset[windowSynsets.size()];
        wordsSynsets = windowSynsets.toArray(wordsSynsets);
    }
}
