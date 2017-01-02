package parsers;

import java.util.ArrayList;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class ParsedDocument {
    private String docID;

    private String[] words;
    private String[] wordPos;

    private ArrayList<String> docWordsLemma;
    private ArrayList<String> docWordsID;

    public ParsedDocument(String docID, ArrayList<String> docWords, ArrayList<String> docWordPos, ArrayList<String> docWordsLemma, ArrayList<String> docWordsID){
        this.docID = docID;

        this.words = docWords.toArray(new String[docWords.size()]);
        this.wordPos = docWordPos.toArray(new String[docWordPos.size()]);

        this.docWordsLemma = docWordsLemma;
        this.docWordsID = docWordsID;
    }

    public String[] getWords(){
        return this.words;
    }

    public String[] getWordPos() {
        return this.wordPos;
    }

    public int wordsLength(){
        return words.length;
    }
}
