package utils;

import edu.smu.tspell.wordnet.SynsetType;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class POSUtils {
    public static final int kAllType = -1;
    public static final int kNounType = 1;
    public static final int kVerbType = 2;
    public static final int kAdjType = 3;
    public static final int kAdvType = 4;
    public static final int kAdjSateliteType = 5;

    /**
     * Transforms a String POS Tag to a int one
     */
    public static int asInt(String pos) {
        if(pos.equals("ALL"))
            return -1;

        if (pos.charAt(0) == 'N') { //Noun values: NN, NNS, NNP
            return kNounType;
        } else if (pos.charAt(0) == 'V' || pos.charAt(0) == 'M') { //Verb values: VB, VBD, VBN, VBZ, VBP, VBG, MD
            return kVerbType;
        } else if (pos.charAt(0) == 'J') { //Adjectice values: JJ, JJR
            return kAdjType;
        } else if (pos.charAt(0) == 'R') { //Adverb values: RB, RBR
            return kAdvType;
        }

        return kNounType;
    }

    /**
     * Transforms a String POS Tag in a WordNet one
     */
    public static SynsetType asSynsetType(String pos){
        return asSynsetType(asInt(pos));
    }

    /**
     * Transforms an int representation of a POS Tag in a WordNet one
     */
    public static SynsetType asSynsetType(int pos) {
        switch (pos) {
            case kNounType: return SynsetType.NOUN;
            case kVerbType: return SynsetType.VERB;
            case kAdjType: return SynsetType.ADJECTIVE;
            case kAdjSateliteType: return SynsetType.ADJECTIVE_SATELLITE;
            case kAdvType: return SynsetType.ADVERB;
            case kAllType: return null;
        }
        return SynsetType.NOUN;
    }
}
