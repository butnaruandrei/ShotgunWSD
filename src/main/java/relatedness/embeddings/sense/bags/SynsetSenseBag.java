package relatedness.embeddings.sense.bags;

import edu.smu.tspell.wordnet.Synset;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SynsetSenseBag {
    // TODO read stopwords from file
    public static final String[] stopWords = { "a", "about", "above", "across", "after", "afterwards",
            "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always",
            "am", "among", "amongst",  "an", "and", "another", "any", "anyhow", "anyone", "anything",
            "anyway", "anywhere", "are", "aren't", "around", "as", "at", "be", "because", "been", "before",
            "behind", "below", "beside", "besides", "between", "beyond", "both", "but", "by", "can't",
            "cannot", "could" ,"couldn't", "did", "didn't", "do", "does", "doesn't",  "don't", "during",
            "each", "either", "else", "elsewhere", "enough", "etc", "ever", "every", "everyone",
            "everything", "everywhere", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't",
            "have", "haven't", "having", "he", "hence", "her", "here", "hereafter", "hereby", "hers",
            "herself", "him", "himself", "his", "how", "i", "if", "in", "into", "is", "isn't", "it",
            "it's", "its", "itself", "let's", "like", "many", "me", "meanwhile", "more", "most", "mostly",
            "much", "mustn't", "my", "myself", "near", "neither", "never", "no", "none", "nor", "not",
            "nothing", "now", "of", "off", "often", "on", "once", "only", "onto", "or", "other", "others",
            "otherwise", "ought", "our", "ours", "ourselves", "over", "own", "past", "per", "please",
            "rather", "same", "seem", "seems", "shall", "shan't", "she", "should", "shouldn't", "since",
            "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "such",
            "than", "that", "the", "their", "theirs", "them", "themselves", "then", "there", "thereafter",
            "thereby", "therefore", "these", "they", "this", "those", "through", "throughout", "thus",
            "to", "together", "too", "toward", "towards", "under", "unless", "until", "up", "upon", "us",
            "very", "via", "was", "wasn't", "we", "were", "weren't", "what", "when", "whenever", "where",
            "whether", "which", "while", "who", "whoever", "whom", "whose", "why", "with", "within",
            "without", "won't", "would", "wouldn't", "yes", "yet", "you", "your", "yours", "yourself",
            "yourselves" };

    protected static String getSenseKeysBag(Synset synset) {
        String senseBag = "";

        String[] senseKeys = synset.getSenseKeys();
        for(String senseKey : senseKeys) {
            senseBag += senseKey.split("%")[0] + " ";
        }

        return senseBag;
    }

    protected static String getSynsetBag(Synset synset) {
        String senseBag = " ";
        String[] examples;

        senseBag += getSenseKeysBag(synset);
        senseBag += synset.getDefinition();
        examples = synset.getUsageExamples();

        for (String example1 : examples)
            senseBag += " " + example1;

        return senseBag;
    }

    protected static String[] extractWordsFromSenseBag(String senseBag){
        String[] words;

        senseBag = senseBag.replace("\"", "").replaceAll(" +", " ");

        words = senseBag.trim().split("[^a-zA-Z\']+");
        words = eliminateStopWordsFromWordSet(words);
        words = new HashSet<>(Arrays.asList(words)).toArray(new String[words.length]); // remove duplicate words

        return words;
    }

    // This method eliminates stop words and removes apostrophe terminations of remaning words.
    public static String[] eliminateStopWordsFromWordSet(String[] contextWords) {
        boolean[] isStopWord = new boolean[contextWords.length];
        int i,j,newContextSize = contextWords.length;

        for (i = 0; i < contextWords.length; i++) {
            isStopWord[i] = false;

            for (j = 0; j < stopWords.length; j++)
                if (contextWords[i].toLowerCase().equals(stopWords[j]))
                    isStopWord[i] = true;

            if (isStopWord[i]) {
                newContextSize--;
            } else if(contextWords[i].indexOf("\'") > 0){
                contextWords[i] = contextWords[i].substring(0,contextWords[i].indexOf("\'"));
                for (j = 0; j < stopWords.length; j++)
                    if (contextWords[i].toLowerCase().equals(stopWords[j]))
                        isStopWord[i] = true;

                if (isStopWord[i])
                    newContextSize--;
            }

        }
        String[] newContextWords = new String[newContextSize];
        for (i = 0, j = 0; i < contextWords.length; i++)
            if (!isStopWord[i]){
                newContextWords[j++] = contextWords[i];
            }
        return newContextWords;
    }
}
