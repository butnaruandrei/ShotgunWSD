import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import parsers.DocumentParser;
import parsers.ParsedDocument;
import parsers.SemEval2007Parser;
import relatedness.embeddings.SenseEmbedding;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class CustomEmbeddings {
    public static void main(String[] args) {
        String[] sources = {"H:\\GoogleNews-vectors-negative300.bin", "H:\\glove.840B.300d.txt"};
        String documents_path = "F:\\Research\\ShotgunWSD-jurnal\\senseval3-words.txt";

        HashSet<String> words = readWords(documents_path);
        createNewEmbeddings(sources, words, "H:\\Senseval3-GN-CC840-vectors600.txt");
    }

    public static HashSet<String> readWords(String path) {
        HashSet<String> words = new HashSet<>();

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));
            String line = br.readLine();

            while (line != null) {
                words.add(line);
                line = br.readLine();
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    public static void createNewEmbeddings(String[] sources, HashSet<String> words, String output){
        Hashtable<String, ArrayList<Double>> newEmbeddings = new Hashtable<>();

        Double[] vectorTmp;
        double[] vectorTmp2;
        WordVectors embeddings;
        ArrayList<Double> tmp;
        for (int i = 0; i < sources.length; i++) {
            embeddings = loadEmbeddings(sources[i]);

            for(String word: words) {
                if (embeddings.hasWord(word)) {
                    vectorTmp2 = embeddings.getWordVector(word);

                    vectorTmp = new Double[vectorTmp2.length];
                    for (int j = 0; j < vectorTmp2.length; j++) {
                        vectorTmp[j] = vectorTmp2[j];
                    }
                } else {
                    vectorTmp = emptyEmbedding(300);
                }

                if(newEmbeddings.containsKey(word)) {
                    tmp = newEmbeddings.get(word);
                    tmp.addAll(Arrays.asList(vectorTmp));
                    newEmbeddings.put(word, tmp);
                } else {
                    tmp = new ArrayList<>(Arrays.asList(vectorTmp));
                    newEmbeddings.put(word, tmp);
                }
            }
        }

        System.out.println(newEmbeddings.containsKey("deeply"));

        saveEmbeddings(newEmbeddings, output);
    }

    public static void saveEmbeddings(Hashtable<String, ArrayList<Double>> embeddings, String output_path) {
        String word;
        ArrayList<Double> tmpEmbedding;
        try {
            PrintWriter writer = new PrintWriter(output_path, "UTF-8");

            for (String w : embeddings.keySet()) {
                word = w.replace("$", "${dollar}")
                        .replace("_", "${underscore}")
                        .replace("\n", "${newline}")
                        .replace(" ", "${space}")
                        .replace("#", "${hash}")
                        .replace("\t", "${tab}")
                        .replace("+", "${plus}");

                writer.write(word + " ");

                tmpEmbedding = embeddings.get(w);
                writer.write(Double.toString(tmpEmbedding.get(0)));
                for (int i = 1; i < tmpEmbedding.size(); i++) {
                    writer.write(" " + Double.toString(tmpEmbedding.get(i)));
                }
                writer.write("\n");
            }

            writer.close();
        } catch (IOException e) {
            // do something
        }
    }

    public static WordVectors loadEmbeddings(String path){
        try {
            if(path.endsWith("bin")) {
                return WordVectorSerializer.loadGoogleModel(new File(path), true);
            } else if (path.endsWith("txt")) {
                return WordVectorSerializer.loadTxtVectors(new File(path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Double[] emptyEmbedding(int size) {
        Double[] empty = new Double[size];

        for (int i = 0; i < size; i++) {
            empty[i] = 0.0;
        }

        return empty;
    }

    public static void writeWords(HashSet<String> words, String output_path) {
        try {
            PrintWriter writer = new PrintWriter(output_path, "UTF-8");

            for(String w: words) {
                writer.write(w + "\n");
            }

            writer.close();
        } catch(Exception e) {

        }
    }
}
