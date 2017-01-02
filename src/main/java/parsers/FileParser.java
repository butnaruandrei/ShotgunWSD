package parsers;

import java.util.ArrayList;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class FileParser extends DocumentParser {
    ArrayList<ParsedDocument> documents;

    public FileParser(String filePath){

    }

    public ArrayList<ParsedDocument> parse() {
        return documents;
    }
}
