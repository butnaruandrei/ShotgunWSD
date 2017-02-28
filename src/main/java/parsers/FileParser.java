package parsers;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.apache.commons.io.IOUtils;
import utils.POSUtils;
import utils.SynsetUtils;
import utils.WordUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class FileParser extends DocumentParser {
    private String filePath;
    ArrayList<ParsedDocument> documents;
    public static WordNetDatabase wnDatabase;

    public FileParser(String filePath){
        this.filePath = filePath;
        this.documents = new ArrayList<>();
    }

    private String readFile(){
        try {
            try (FileInputStream inputStream = new FileInputStream(filePath)) {
                return IOUtils.toString(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public ArrayList<ParsedDocument> parse() {
        String file = readFile();

        if(file.isEmpty())
            return documents;

        Path p = Paths.get(filePath);
        String docID = p.getFileName().toString();

        ArrayList<String> docWords, docWordsLemma, docWordsPOS, docWordsID;

        docWords = new ArrayList<>();
        docWordsLemma = new ArrayList<>();
        docWordsPOS = new ArrayList<>();
        docWordsID = new ArrayList<>();

        Document document = new Document(file);
        List<Sentence> sentences = document.sentences();

        List<String> words;
        String pos;
        int wordIndex = 0;
        for(Sentence sentence : sentences) {
            words = sentence.words();

            for(int i = 0; i < words.size(); i++){
                pos = fixPOS(sentence.posTag(i));
                if((pos.equals("NN") || pos.equals("VB") || pos.equals("JJ") || pos.equals("RB")) && WordUtils.inWordNet(wnDatabase, sentence.lemma(i), POSUtils.asSynsetType(pos))) {
                    docWords.add(words.get(i));
                    docWordsLemma.add(sentence.lemma(i));
                    docWordsPOS.add(pos);
                    docWordsID.add(Integer.toString(wordIndex));
                }

                wordIndex += 1;
            }
        }

        documents.add(new ParsedDocument(docID, docWords, docWordsPOS, docWordsLemma, docWordsID));

        return documents;
    }

    private static String fixPOS(String pos) {
        if (pos.charAt(0) == 'n' || pos.charAt(0) == 'N') { //Noun values: NN, NNS, NNP
            return "NN";
        } else if (pos.charAt(0) == 'v' || pos.charAt(0) == 'V') { //Verb values: VB, VBD, VBN, VBZ, VBP, VBG, MD
            return "VB";
        } else if (pos.charAt(0) == 'a' || pos.charAt(0) == 'J') { //Adjectice values: JJ, JJR
            return "JJ";
        } else if (pos.charAt(0) == 'r' || pos.charAt(0) == 'R') { //Adverb values: RB, RBR
            return "RB";
        }

        return pos;
    }
}
