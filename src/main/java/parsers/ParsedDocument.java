package parsers;

import java.util.ArrayList;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class ParsedDocument {
    private String docID;

    private String[] words;
    private String[] wordPos;

    private String[] docWordsLemma;
    private String[] docWordsID;

    public ParsedDocument(String docID, ArrayList<String> docWords, ArrayList<String> docWordPos, ArrayList<String> docWordsLemma, ArrayList<String> docWordsID){
        this.docID = docID;

        this.words = docWords.toArray(new String[docWords.size()]);
        this.wordPos = docWordPos.toArray(new String[docWordPos.size()]);

        this.docWordsLemma = docWordsLemma.toArray(new String[docWordsLemma.size()]);
        this.docWordsID = docWordsID.toArray(new String[docWordsID.size()]);
    }

    public String getDocID() {
        return this.docID;
    }

    public String[] getWordLemmas() {
        return docWordsLemma;
    }

    public String[] getWordsID(){
        return docWordsID;
    }

    public String[] getWords(){
        return this.words;
    }

    public String getWord(int index) { return this.words[index]; }

    public String[] getWordPos() {
        return this.wordPos;
    }

    public String getWordPos(int index) { return this.wordPos[index]; }

    public int wordsLength(){
        return words.length;
    }
}
