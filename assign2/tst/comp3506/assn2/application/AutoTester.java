package comp3506.assn2.application;

import comp3506.assn2.utils.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;


/**
 * Hook class used by automated testing tool.
 * The testing tool will instantiate an object of this class to test the functionality of your assignment.
 * You must implement the constructor stub below and override the methods from the Search interface
 * so that they call the necessary code in your application.
 *
 * @author Sam Eadie
 *
 */
public class AutoTester implements Search {

    //Trie storing occurrences of all words in text for fast retrieval
    private OccurrenceTrie documentTrie = new OccurrenceTrie();

    //Store section titles in HashMap for fast access, value stores HashPair<sectionNumber, sectionStartLineNumber>
    private HashMap<String, HashPair<Integer, Integer>> sectionIndexes = new HashMap<>();

    //Stores starting line of sections in order, used for creation of documentTrie
    private ArrayList<Integer> sectionStarts = new ArrayList<>();

    //Stores stop words for fast access
    private HashSet<String> stopWords = new HashSet<>();

    //Stores text for fast retrieval - would actually be stored on disc with reference in main memory
    private HashMap<Integer, String> textLines = new HashMap<>();


    /**
     * Create an object that performs search operations on a document.
     * If indexFileName or stopWordsFileName are null or an empty string the document should be loaded
     * and all searches will be across the entire document with no stop words.
     * All files are expected to be in the files sub-directory and
     * file names are to include the relative path to the files (e.g. "files\\shakespeare.txt").
     *
     * @param documentFileName  Name of the file containing the text of the document to be searched.
     * @param indexFileName     Name of the file containing the index of sections in the document.
     * @param stopWordsFileName Name of the file containing the stop words ignored by most searches.
     * @throws FileNotFoundException if any of the files cannot be loaded.
     *                               The name of the file(s) that could not be loaded should be passed
     *                               to the FileNotFoundException's constructor.
     * @throws IllegalArgumentException if documentFileName is null or an empty string.
     */
    public AutoTester(String documentFileName, String indexFileName, String stopWordsFileName)
            throws FileNotFoundException, IllegalArgumentException {
        if ((documentFileName == null) || (documentFileName.length() == 0)) {
            throw new IllegalArgumentException("Invalid document filename");
        }

        try {
            readTextLines(documentFileName);
        } catch (IOException e ) {
            System.err.println("IO Exception with reading text lines");
        }

        try {
            if ((indexFileName == null) || (indexFileName.length() == 0)) {
                this.sectionStarts.append(1);
            } else {
                readSectionIndexes(indexFileName);
            }
        } catch (IOException e ) {
            System.err.println("IO Exception with IndexFileName");
        }

        try {
            formTrieFromFile(documentFileName);
        } catch (IOException e ) {
            System.err.println("IO exception with reading text lines");
        }

        try {
            if((stopWordsFileName == null) || (stopWordsFileName.length() == 0)) {
                this.stopWords = new HashSet<>();
            } else {
                readStopWords(stopWordsFileName);
            }
        } catch (Exception e) {
            System.err.println("IOException");
        }
    }

    private void readTextLines(String filename) throws IOException{
        //Create reader for document and remove byte order mark (first char)
        FileReader fReader = new FileReader(filename);
        BufferedReader documentReader = new BufferedReader(fReader);
        documentReader.read();

        this.textLines = new HashMap<>();
        Integer lineNumber = 0;
        String line;

        //Store all lines in HashMap
        while((line = documentReader.readLine()) != null) {
            this.textLines.put(lineNumber++, line);
        }
    }

    private void readSectionIndexes(String indexFileName) throws IOException {
        //Create reader for index file and remove byte order mark (first char)
        BufferedReader documentReader = new BufferedReader(new FileReader(indexFileName));
        documentReader.read();

        this.sectionIndexes = new HashMap<>();
        this.sectionStarts = new ArrayList<>();

        String line, sectionTitle;
        String[] parts;
        Integer sectionLineNumber, sectionNumber = 0;

        //Read file until end
        while ((line = documentReader.readLine()) != null) {
            line = line.toLowerCase();
            parts = line.split(",", 2);
            sectionLineNumber = Integer.parseInt(parts[1]);
            sectionTitle = parts[0];

            this.sectionStarts.append(sectionLineNumber);
            this.sectionIndexes.put(sectionTitle, new HashPair<>(sectionNumber, sectionLineNumber));

            sectionNumber++;
        }
    }

    private void readStopWords(String stopWordsFileName) throws IOException {
        //Create reader for stop words file and remove byte order mark (first char)
        BufferedReader documentReader = new BufferedReader(new FileReader(stopWordsFileName));
        documentReader.read();

        String line;
        this.stopWords = new HashSet<>();

        while ((line = documentReader.readLine()) != null) {
            this.stopWords.put(line.toLowerCase());
        }
    }

    public void formTrieFromFile(String filename) throws IOException {
        //Create reader for text and remove byte order mark (first char)
        BufferedReader documentReader = new BufferedReader(new FileReader(filename));
        documentReader.read();

        Integer sectionNumber = 0;
        int lineNumber = 1;

        //Initialise Trie for creation
        this.documentTrie = new OccurrenceTrie();
        OccurrenceTrieNode reference;
        HashPair<Integer, Integer> occurrence;


        String line;
        //Read file until end
        while((line = documentReader.readLine()) != null) {
            line = line.toLowerCase();
            reference = documentTrie.getRoot(); //Start adding next word from root
            occurrence = new HashPair<>(lineNumber, 1); //First word occurs at start of line

            for(int i = 0; i < line.length(); i++) {
                //Ignore punctation that isn't apostrophes
                if(!(Character.isLetterOrDigit(line.charAt(i)) || line.charAt(i) == '\'' || line.charAt(i) == ' ')) {
                    continue;

                    //Handle space characters - end of words
                } else if(line.charAt(i) == ' ') {
                    //Check we've traversed a non-empty word
                    if(!reference.equals(documentTrie.getRoot())) {
                        reference.addOccurrence(occurrence, sectionNumber);
                    }

                    //Set occurrence position for next word
                    occurrence = new HashPair<>(lineNumber, i + 2); //Column number = i + 1: next word starts at i + 2
                    reference = documentTrie.getRoot();
                    continue;

                    //Handle apostrophe
                } else if (line.charAt(i) == '\'') {
                    //Ignore apostrophes on ends of word
                    if (i + 1 == line.length() || (line.charAt(i + 1) == ' ')){
                        continue;
                    }

                    //Move start of word up one if apostrophe at start of word
                    if(i == 0 || (line.charAt(i - 1) == ' ')) {
                        occurrence.setRightValue(occurrence.getRightValue() + 1);
                    }
                }

                //General case - add leter and traverse down Trie
                reference = reference.addChild(line.charAt(i));

            }

            //Handle end of line
            reference.addOccurrence(occurrence, sectionNumber);
            lineNumber++;

            //Check if we've moved to new section
            if((sectionNumber < this.sectionStarts.size()) && (lineNumber >= this.sectionStarts.get(sectionNumber))) {
                sectionNumber++;
            }
        }
    }

    /**
     * Determines the number of times the word appears in the document.
     *
     * @param word The word to be counted in the document.
     * @return The number of occurrences of the word in the document.
     * @throws IllegalArgumentException if word is null or an empty String.
     */
    public int wordCount(String word) throws IllegalArgumentException {
        if(word.length() == 0) {
            return 0;
        }

        ArrayList<HashPair<Integer, Integer>> occurrences = this.documentTrie.getOccurrences(word);

        if(occurrences == null) {
            return 0;
        } else {
            return occurrences.size();
        }
    }

    private java.util.ArrayList<Pair<Integer, Integer>> dataTypeAdapter(ArrayList<HashPair<Integer, Integer>> list) {
        java.util.ArrayList<Pair<Integer, Integer>> convertedList = new java.util.ArrayList<>(list.size());

        for(int i = 0; i < list.size(); i++) {
            convertedList.add(new Pair<>(list.get(i).getLeftValue(), list.get(i).getRightValue()));
        }

        return convertedList;
    }

    /**
     * Finds all occurrences of the phrase in the document.
     * A phrase may be a single word or a sequence of words.
     *
     * @param phrase The phrase to be found in the document.
     * @return List of pairs, where each pair indicates the line and column number of each occurrence of the phrase.
     *         Returns an empty list if the phrase is not found in the document.
     * @throws IllegalArgumentException if phrase is null or an empty String.
     */
    public List<Pair<Integer,Integer>> phraseOccurrence(String phrase) throws IllegalArgumentException {

        if((phrase == null ) || (phrase.length() == 0)) {
            throw new IllegalArgumentException("Invalid phrase");
        }

        //If search for word not phrase
        if(!phrase.contains(" ")) {
            ArrayList<HashPair<Integer, Integer>> occurrences = this.documentTrie.getOccurrences(phrase);
            if (occurrences == null) {
                return new java.util.ArrayList<>();
            } else {
                return dataTypeAdapter(occurrences);
            }
        }

        //Find occurrences of first word
        ArrayList<HashPair<Integer, Integer>> occurrences = this.documentTrie.getOccurrences(phrase.split(" ")[0]);

        HashPair<Integer, Integer> occurrence;

        ArrayList<HashPair<Integer, Integer>> phraseOccurrences = new ArrayList<>();

        //Perform search starting at each occurrence of first word
        for(int i = 0; i < occurrences.size(); i++) {
            occurrence = occurrences.get(i);
            if(matchPatternFromOccurrence(occurrence, phrase)) {
                phraseOccurrences.append(occurrence);
            }
        }

        return dataTypeAdapter(phraseOccurrences);
    }

    private boolean matchPatternFromOccurrence(HashPair<Integer, Integer> occurrence, String phrase) {
        int lineNumber = occurrence.getLeftValue();
        int columnNumber = occurrence.getRightValue();
        int stringIndex = 0;
        char c;

        while(stringIndex < phrase.length()) {
            //Go to next line
            if(columnNumber >= this.textLines.get(lineNumber).length()) {
                lineNumber++;
                columnNumber = 1;
            }

            c = this.textLines.get(lineNumber).charAt(columnNumber - 1);

            //Valid char for comparison
            if(((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || (c == ' ')) {
                if(c != phrase.charAt(stringIndex++)) {
                    return false;
                }
                //Handle apostrophe
            } else if(c == '\'') {
                if((columnNumber + 1 >= this.textLines.get(lineNumber).length()) || (columnNumber == 1)
                        || (this.textLines.get(lineNumber).charAt(columnNumber - 1 - 1) == ' ')
                        || (this.textLines.get(lineNumber).charAt(columnNumber - 1 + 1) == ' ')) {
                    //Apostrophe at start/end of word
                } else {
                    if(c != phrase.charAt(stringIndex++)) {
                        return false;
                    }
                }
            }
            columnNumber++;
        }

        return true;
    }

    /**
     * Finds all occurrences of the prefix in the document.
     * A prefix is the start of a word. It can also be the complete word.
     * For example, "obscure" would be a prefix for "obscure", "obscured", "obscures" and "obscurely".
     *
     * @param prefix The prefix of a word that is to be found in the document.
     * @return List of pairs, where each pair indicates the line and column number of each occurrence of the prefix.
     *         Returns an empty list if the prefix is not found in the document.
     * @throws IllegalArgumentException if prefix is null or an empty String.
     */
    public List<Pair<Integer,Integer>> prefixOccurrence(String prefix) throws IllegalArgumentException {
        if((prefix == null) || (prefix.length() == 0)) {
            throw new IllegalArgumentException("Invalid prefix");
        }

        ArrayList<HashPair<Integer, Integer>> occurrences = this.documentTrie.getOccurrencesForSubtree(prefix);

        if(occurrences == null) {
            return new java.util.ArrayList<>();
        } else {
            return dataTypeAdapter(occurrences);
        }
    }

    /**
     * Searches the document for lines that contain all the words in the 'words' parameter.
     * Implements simple "and" logic when searching for the words.
     * The words do not need to be contiguous on the line.
     *
     * @param words Array of words to find on a single line in the document.
     * @return List of line numbers on which all the words appear in the document.
     *         Returns an empty list if the words do not appear in any line in the document.
     * @throws IllegalArgumentException if words is null or an empty array
     *                                  or any of the Strings in the array are null or empty.
     */
    public List<Integer> wordsOnLine(String[] words) throws IllegalArgumentException {
        if((words == null) || (words.length == 0)) {
            throw new IllegalArgumentException("Invalid search words");
        }

        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>(words.length);

        //Get occurrences of every word
        for(int i = 0; i < words.length; i++) {
            if((words[i] == null) || (words[i].length() == 0)) {
                throw new IllegalArgumentException("Invalid search word");
            }

            if(!stopWords.contains(words[i])) {
                occurrences.append(documentTrie.getOccurrences(words[i].toLowerCase()));
            }
        }

        //Find interesection of occurrences
        return Intersection.getIntersections(occurrences).toJavaArrayList();
    }

    /**
     * Searches the document for lines that contain any of the words in the 'words' parameter.
     * Implements simple "or" logic when searching for the words.
     * The words do not need to be contiguous on the line.
     *
     * @param words Array of words to find on a single line in the document.
     * @return List of line numbers on which any of the words appear in the document.
     *         Returns an empty list if none of the words appear in any line in the document.
     * @throws IllegalArgumentException if words is null or an empty array
     *                                  or any of the Strings in the array are null or empty.
     */
    public List<Integer> someWordsOnLine(String[] words) throws IllegalArgumentException {
        if((words == null) || (words.length == 0)) {
            throw new IllegalArgumentException("Invalid search words");
        }

        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>(words.length);

        //Get occurrences for every word
        for(int i = 0; i < words.length; i++) {
            if((words[i] == null) || (words[i].length() == 0)) {
                throw new IllegalArgumentException("Invalid search word");
            }

            if(!stopWords.contains(words[i])) {
                occurrences.append(documentTrie.getOccurrences(words[i].toLowerCase()));
            }
        }

        //Find union of occurrences
        return Intersection.getUnion(occurrences).toJavaArrayList();
    }

    /**
     * Searches the document for lines that contain all the words in the 'wordsRequired' parameter
     * and none of the words in the 'wordsExcluded' parameter.
     * Implements simple "not" logic when searching for the words.
     * The words do not need to be contiguous on the line.
     *
     * @param wordsRequired Array of words to find on a single line in the document.
     * @param wordsExcluded Array of words that must not be on the same line as 'wordsRequired'.
     * @return List of line numbers on which all the wordsRequired appear
     *         and none of the wordsExcluded appear in the document.
     *         Returns an empty list if no lines meet the search criteria.
     * @throws IllegalArgumentException if either of wordsRequired or wordsExcluded are null or an empty array
     *                                  or any of the Strings in either of the arrays are null or empty.
     */
    public List<Integer> wordsNotOnLine(String[] wordsRequired, String[] wordsExcluded)
            throws IllegalArgumentException {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> requiredOccurrences = new ArrayList<>(wordsRequired.length);

        if((wordsRequired == null ) || (wordsExcluded == null) ||
                (wordsRequired.length == 0) || (wordsExcluded.length == 0)) {
            throw new IllegalArgumentException("Invalid search words");
        }

        //Get occurrences of required words
        for(int i = 0; i < wordsRequired.length; i++) {
            if((wordsRequired[i] == null) || (wordsRequired[i].length() == 0)) {
                throw new IllegalArgumentException("Invalid required word");
            }

            if(!stopWords.contains(wordsRequired[i])) {
                requiredOccurrences.append(documentTrie.getOccurrences(wordsRequired[i].toLowerCase()));
            }
        }

        //Find interesection of occurrences
        ArrayList<Integer> intersection = Intersection.getIntersections(requiredOccurrences);

        ArrayList<ArrayList<HashPair<Integer, Integer>>> excludedOccurrences = new ArrayList<>(wordsExcluded.length);
        ArrayList<Integer> pointers = new ArrayList<>(wordsExcluded.length);

        int includedPointer = 0;
        //Get occurrences of not required words
        for(int i = 0; i < wordsExcluded.length; i++) {
            if((wordsExcluded[i] == null) || (wordsExcluded[i].length() == 0)) {
                throw new IllegalArgumentException("Invalid excluded word");
            }

            if(!this.stopWords.contains(wordsExcluded[i])) {
                excludedOccurrences.append(documentTrie.getOccurrences(wordsExcluded[i].toLowerCase()));
                pointers.append(0);
            }
        }


        while(includedPointer < intersection.size()) {
            boolean allGreater = true;
            for(int i = 0; i < pointers.size(); i++) {
                if(excludedOccurrences.get(i).get(pointers.get(i)).getLeftValue() < intersection.get(includedPointer)) {
                    pointers.set(i, pointers.get(i) + 1);
                    allGreater = false;
                } else if (excludedOccurrences.get(i).get(pointers.get(i)).getLeftValue().equals(intersection.get(includedPointer))) {
                    //System.out.println(intersection.get(includedPointer));
                    intersection.remove(includedPointer);
                    break;
                }
            }
            if(allGreater) {
                includedPointer++;
            }
        }

        return intersection.toJavaArrayList();
    }

    private HashPair<Integer[], ArrayList<HashSet<Integer>>> setupSectionSearch(String[] titles, String[] words) {

        //The section numbers of the titles
        Integer[] sectionNumbers = new Integer[titles.length];
        for(int i = 0; i < titles.length; i++) {
            if((titles[i] == null) || (titles[i].length() == 0)) { throw new IllegalArgumentException("Invalid title");
            } else {
                HashPair<Integer, Integer> sectionHashPair = this.sectionIndexes.get(titles[i]);
                //Section not in document - return empty list
                if(sectionHashPair == null) {
                    return null;
                }
                sectionNumbers[i] = sectionHashPair.getLeftValue();
            }
        }

        //An ArrayList of section numbers (Integers) each word in words occurs in
        ArrayList<HashSet<Integer>> wordSections = new ArrayList<>(words.length);
        //Get sections of every word
        for(int i = 0; i < words.length; i++) {
            if((words[i] == null) || (words[i].length() == 0)) { throw new IllegalArgumentException("Invalid search word");
            } else if(!stopWords.contains(words[i])) {
                OccurrenceTrieNode node  = documentTrie.getNodeTerminatingWord(words[i].toLowerCase());

                //A word doesnt occur in whole document
                if(node == null) {
                    wordSections.append(new HashSet<Integer>(0));
                } else {
                    wordSections.append(node.getSectionSet());
                }
            }
        }

        return new HashPair<>(sectionNumbers, wordSections);
    }

    private void addAllOccurrencesOfWordInSection(String word, Integer sectionNumber,
                                                  ArrayList<Triple<Integer, Integer, String>> occurrencesFound) {

        ArrayList<Integer> sectionNumbers = documentTrie.getSectionNumbers(word);
        ArrayList<HashPair<Integer, Integer>> occurrences = documentTrie.getOccurrences(word);

        //Binary search to find index of an occurrence in section
        int sectionNumberIndex = sectionNumbers.binarySearch(Comparator.naturalOrder(), sectionNumber);

        //Find all other occurrences in section (will be around sectionNumberIndex)
        int j = sectionNumberIndex;
        //Add all occurrences before
        while(sectionNumbers.get(j).equals(sectionNumber)) {
            occurrencesFound.append(new Triple<>(occurrences.get(j).getLeftValue(),
                    occurrences.get(j).getRightValue(), word));
            j--;
        }

        //Add all occurrences after
        j = sectionNumberIndex + 1;
        while(sectionNumbers.get(j).equals(sectionNumber)) {
            occurrencesFound.append(new Triple<>(occurrences.get(j).getLeftValue(),
                    occurrences.get(j).getRightValue(), word));
            j++;
        }


    }

    /**
     * Searches the document for sections that contain all the words in the 'words' parameter.
     * Implements simple "and" logic when searching for the words.
     * The words do not need to be on the same lines.
     *
     * @param titles Array of titles of the sections to search within,
     *               the entire document is searched if titles is null or an empty array.
     * @param words Array of words to find within a defined section in the document.
     * @return List of triples, where each triple indicates the line and column number and word found,
     *         for each occurrence of one of the words.
     *         Returns an empty list if the words are not found in the indicated sections of the document,
     *         or all the indicated sections are not part of the document.
     * @throws IllegalArgumentException if words is null or an empty array
     *                                  or any of the Strings in either of the arrays are null or empty.
     */
    public List<Triple<Integer,Integer,String>> simpleAndSearch(String[] titles, String[] words)
            throws IllegalArgumentException {
        if((words == null) || (words.length == 0)) { throw new IllegalArgumentException("Invalid words"); }
        if((titles == null) || (titles.length == 0)) { throw new IllegalArgumentException("Invalid titles"); }

        ArrayList<Triple<Integer, Integer, String>> occurrencesFound = new ArrayList<>();
        HashPair<Integer[], ArrayList<HashSet<Integer>>> setupHashPair = setupSectionSearch(titles, words);

        //Not all titles are in document
        if(setupHashPair == null) {
            return new java.util.ArrayList<>();
        }

        //The section numbers of the titles
        Integer[] sectionNumbers = setupHashPair.getLeftValue();
        //An ArrayList of section numbers (Integers) each word in words occurs in
        ArrayList<HashSet<Integer>> wordSections = setupHashPair.getRightValue();

        for(int i = 0; i < titles.length; i++) {
            addOccurrencesAND(sectionNumbers[i], wordSections, words, occurrencesFound);
        }

        return occurrencesFound.toJavaArrayList();
    }

    private void addOccurrencesAND(Integer sectionNumber, ArrayList<HashSet<Integer>> wordSections,
                                   String[] words, ArrayList<Triple<Integer, Integer, String>> occurrencesFound) {
        for(int i = 0; i < wordSections.size(); i++) {
            if(!wordSections.get(i).contains(sectionNumber)) {
                return; //Not all words are in section
            }
        }

        for(int i = 0; i < words.length; i++) {
            addAllOccurrencesOfWordInSection(words[i], sectionNumber,occurrencesFound);
        }

    }

    /**
     * Searches the document for sections that contain any of the words in the 'words' parameter.
     * Implements simple "or" logic when searching for the words.
     * The words do not need to be on the same lines.
     *
     * @param titles Array of titles of the sections to search within,
     *               the entire document is searched if titles is null or an empty array.
     * @param words Array of words to find within a defined section in the document.
     * @return List of triples, where each triple indicates the line and column number and word found,
     *         for each occurrence of one of the words.
     *         Returns an empty list if the words are not found in the indicated sections of the document,
     *         or all the indicated sections are not part of the document.
     * @throws IllegalArgumentException if words is null or an empty array
     *                                  or any of the Strings in either of the arrays are null or empty.
     */
    public List<Triple<Integer,Integer,String>> simpleOrSearch(String[] titles, String[] words)
            throws IllegalArgumentException {
        if((words == null) || (words.length == 0)) { throw new IllegalArgumentException("Invalid words"); }
        if((titles == null) || (titles.length == 0)) { throw new IllegalArgumentException("Invalid titles"); }

        ArrayList<Triple<Integer, Integer, String>> occurrencesFound = new ArrayList<>();
        HashPair<Integer[], ArrayList<HashSet<Integer>>> setupHashPair = setupSectionSearch(titles, words);

        //Not all titles are in document
        if(setupHashPair == null) {
            return new java.util.ArrayList<>();
        }

        //The section numbers of the titles
        Integer[] sectionNumbers = setupHashPair.getLeftValue();
        //An ArrayList of section numbers (Integers) each word in words occurs in
        ArrayList<HashSet<Integer>> wordSections = setupHashPair.getRightValue();

        for(int i = 0; i < titles.length; i++) {
            addOccurrencesOR(sectionNumbers[i], wordSections, words, occurrencesFound);
        }

        return occurrencesFound.toJavaArrayList();
    }

    private void addOccurrencesOR(Integer sectionNumber, ArrayList<HashSet<Integer>> wordSections,
                                  String[] words, ArrayList<Triple<Integer, Integer, String>> occurrencesFound) {
        for(int i = 0; i < wordSections.size(); i++) {
            if(wordSections.get(i).contains(sectionNumber)) {
                addAllOccurrencesOfWordInSection(words[i], sectionNumber, occurrencesFound);
            }
        }
    }

    /**
     * Searches the document for sections that contain all the words in the 'wordsRequired' parameter
     * and none of the words in the 'wordsExcluded' parameter.
     * Implements simple "not" logic when searching for the words.
     * The words do not need to be on the same lines.
     *
     * @param titles Array of titles of the sections to search within,
     *               the entire document is searched if titles is null or an empty array.
     * @param wordsRequired Array of words to find within a defined section in the document.
     * @param wordsExcluded Array of words that must not be in the same section as 'wordsRequired'.
     * @return List of triples, where each triple indicates the line and column number and word found,
     *         for each occurrence of one of the required words.
     *         Returns an empty list if the words are not found in the indicated sections of the document,
     *         or all the indicated sections are not part of the document.
     * @throws IllegalArgumentException if wordsRequired is null or an empty array
     *                                  or any of the Strings in any of the arrays are null or empty.
     */
    public List<Triple<Integer,Integer,String>> simpleNotSearch(String[] titles, String[] wordsRequired,
                                                                String[] wordsExcluded)
            throws IllegalArgumentException {
        if((wordsRequired == null) || (wordsRequired.length == 0)) { throw new IllegalArgumentException("Invalid required words"); }
        if((wordsExcluded == null) || (wordsExcluded.length == 0)) { throw new IllegalArgumentException("Invalid excluded words"); }
        if((titles == null) || (titles.length == 0)) { throw new IllegalArgumentException("Invalid titles"); }

        ArrayList<Triple<Integer, Integer, String>> occurrencesFound = new ArrayList<>();
        HashPair<Integer[], ArrayList<HashSet<Integer>>> setupHashPair = setupSectionSearch(titles, wordsRequired);

        //Not all titles are in document
        if(setupHashPair == null) {
            return new java.util.ArrayList<>();
        }

        //The section numbers of the titles
        Integer[] sectionNumbers = setupHashPair.getLeftValue();
        //An ArrayList of section numbers (Integers) each word in words occurs in
        ArrayList<HashSet<Integer>> wordsRequiredSections = setupHashPair.getRightValue();

        ArrayList<HashSet<Integer>> wordsExcludedSections = new ArrayList<>(wordsExcluded.length);
        //Get sections of excluded words
        for(int i = 0; i < wordsExcluded.length; i++) {
            if((wordsExcluded[i] == null) || (wordsExcluded[i].length() == 0)) { throw new IllegalArgumentException("Invalid search exclude word");
            } else if(!stopWords.contains(wordsExcluded[i])) {
                OccurrenceTrieNode node  = documentTrie.getNodeTerminatingWord(wordsExcluded[i].toLowerCase());

                //A word doesnt occur in whole document
                if(node == null) {
                    wordsExcludedSections.append(new HashSet<>(0));
                } else {
                    wordsExcludedSections.append(node.getSectionSet());
                }
            }
        }

        for(int i = 0; i < titles.length; i++) {
            addOccurrencesNOT(sectionNumbers[i], wordsRequiredSections, wordsExcludedSections, wordsRequired, occurrencesFound);
        }

        return occurrencesFound.toJavaArrayList();
    }

    public void addOccurrencesNOT(Integer sectionNumber, ArrayList<HashSet<Integer>> wordsRequiredSections,
                                  ArrayList<HashSet<Integer>> wordsExcludedSections, String[] wordsRequired,
                                  ArrayList<Triple<Integer, Integer, String>> occurrencesFound) {
        //Check section number contains all required words
        for(int i = 0; i < wordsRequiredSections.size(); i++) {
            if(!wordsRequiredSections.get(i).contains(sectionNumber)) {
                return;
            }
        }

        //Check section number doesnt contain an excluded word
        for(int i = 0; i < wordsExcludedSections.size(); i++) {
            if(wordsExcludedSections.get(i).contains(sectionNumber)) {
                return;
            }
        }

        //Add all occurrences of wordsRequired in section
        for(int i = 0; i < wordsRequired.length; i++) {
            addAllOccurrencesOfWordInSection(wordsRequired[i], sectionNumber, occurrencesFound);
        }
    }

    /**
     * Searches the document for sections that contain all the words in the 'wordsRequired' parameter
     * and at least one of the words in the 'orWords' parameter.
     * Implements simple compound "and/or" logic when searching for the words.
     * The words do not need to be on the same lines.
     *
     * @param titles Array of titles of the sections to search within,
     *               the entire document is searched if titles is null or an empty array.
     * @param wordsRequired Array of words to find within a defined section in the document.
     * @param orWords Array of words, of which at least one, must be in the same section as 'wordsRequired'.
     * @return List of triples, where each triple indicates the line and column number and word found,
     *         for each occurrence of one of the words.
     *         Returns an empty list if the words are not found in the indicated sections of the document,
     *         or all the indicated sections are not part of the document.
     * @throws IllegalArgumentException if wordsRequired is null or an empty array
     *                                  or any of the Strings in any of the arrays are null or empty.
     */
    public List<Triple<Integer,Integer,String>> compoundAndOrSearch(String[] titles, String[] wordsRequired,
                                                                    String[] orWords)
            throws IllegalArgumentException {
        if((wordsRequired == null) || (wordsRequired.length == 0)) { throw new IllegalArgumentException("Invalid required words"); }
        if((orWords == null) || (orWords.length == 0)) { throw new IllegalArgumentException("Invalid or words"); }
        if((titles == null) || (titles.length == 0)) { throw new IllegalArgumentException("Invalid titles"); }

        ArrayList<Triple<Integer, Integer, String>> occurrencesFound = new ArrayList<>();
        HashPair<Integer[], ArrayList<HashSet<Integer>>> setupHashPair = setupSectionSearch(titles, wordsRequired);

        //Not all titles are in document
        if(setupHashPair == null) {
            return new java.util.ArrayList<>();
        }

        //The section numbers of the titles
        Integer[] sectionNumbers = setupHashPair.getLeftValue();
        //An ArrayList of section numbers (Integers) each word in words occurs in
        ArrayList<HashSet<Integer>> wordsRequiredSections = setupHashPair.getRightValue();

        ArrayList<HashSet<Integer>> orWordsSections = new ArrayList<>(orWords.length);
        for(int i = 0; i < orWords.length; i++) {
            if((orWords[i] == null) || (orWords[i].length() == 0)) { throw new IllegalArgumentException("Invalid search or word");
            } else if(!stopWords.contains(orWords[i])) {
                OccurrenceTrieNode node  = documentTrie.getNodeTerminatingWord(orWords[i].toLowerCase());
                //A word doesnt occur in whole document
                if(node == null) {
                    orWordsSections.append(new HashSet<Integer>(0));
                } else {
                    orWordsSections.append(node.getSectionSet());
                }
            }
        }

        for(int i = 0; i < titles.length; i++) {
            addOccurrencesANDOR(sectionNumbers[i], wordsRequiredSections, orWordsSections, wordsRequired, orWords, occurrencesFound);
        }

        return occurrencesFound.toJavaArrayList();
    }

    private void addOccurrencesANDOR(Integer sectionNumber, ArrayList<HashSet<Integer>> wordsRequiredSections,
                                     ArrayList<HashSet<Integer>> orWordsSections, String[] wordsRequired,
                                     String [] orWords, ArrayList<Triple<Integer, Integer, String>> occurrencesFound) {

        //Check section contains all required words
        for(int i = 0; i < wordsRequiredSections.size(); i++) {
            if(!wordsRequiredSections.get(i).contains(sectionNumber)) {
                return;
            }
        }

        //Check it contains at least one or word
        boolean orConditionMet = false;
        for(int i = 0; i < orWordsSections.size(); i++) {
            if(orWordsSections.get(i).contains(sectionNumber)) {
                orConditionMet = true;
                addAllOccurrencesOfWordInSection(orWords[i], sectionNumber, occurrencesFound);
            }
        }

        if(orConditionMet) {
            //Add all required words
            for(int i = 0; i < wordsRequired.length; i++) {
                addAllOccurrencesOfWordInSection(wordsRequired[i], sectionNumber, occurrencesFound);
            }
        }
    }
}