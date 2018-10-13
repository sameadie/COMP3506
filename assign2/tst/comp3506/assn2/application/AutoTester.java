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
 * @bigO
 *      O(numSections + numStopWords + numLines) space complexity:
 *          documentTrie : O(27^longestWord)
 *          sectionIndexes : O(numSections)
 *          sectionStarts : O(numSections)
 *          stopWords : O(numStopWords)
 *          textLines : O(lenText) but stored on disc with references in main memory
 *
 *
 * @author Sam Eadie
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

    /**
     * Reads the file's lines into a HashMap
     *
     * @param filename
     *      the document's filename
     *
     * @throws IOException
     *      If the file cannot be read
     */
    private void readTextLines(String filename) throws IOException{
        //Create reader for document
        FileReader fReader = new FileReader(filename);
        BufferedReader documentReader = new BufferedReader(fReader);

        this.textLines = new HashMap<>();
        Integer lineNumber = 0;
        String line;

        //Store all lines in HashMap
        while((line = documentReader.readLine()) != null) {
            this.textLines.put(lineNumber++, line.toLowerCase());
        }
    }

    /**
     * Reads in the section names and start lines into a
     * HashMap<sectionName, Pair<sectionNumber, sectionStartLineNumber>> and an
     * ArrayList<sectionLineNumber>
     *
     * @param indexFileName
     *      The filename of the document to read sections from
     *
     * @throws IOException
     *      If the file cannot be read
     */
    private void readSectionIndexes(String indexFileName) throws IOException {
        //Create reader for index file
        BufferedReader documentReader = new BufferedReader(new FileReader(indexFileName));

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

    /**
     * Reads all words from stopWordsFilename into a HashSet
     *
     * @param stopWordsFileName
     *      The filename of the document storing stop words for this search application
     *
     * @throws IOException
     *      If the file cannot be read
     */
    private void readStopWords(String stopWordsFileName) throws IOException {
        //Create reader for stop words file
        BufferedReader documentReader = new BufferedReader(new FileReader(stopWordsFileName));

        String line;
        this.stopWords = new HashSet<>();

        while ((line = documentReader.readLine()) != null) {
            this.stopWords.put(line.toLowerCase());
        }
    }


    /**
     * Forms an OccurrenceTrie storing occurrences of all words in the document with
     * specified filename
     *
     * @bigO
     *      O(numChars + numWords + numLines):
     *
     *      O(numChars) = it iterates through every character in the document and performs constant time
     *          comparison against \n to split into lines. Adds or traverses down trie for every character in
     *          the document which is at most O(27) each, hence O(numChars). Even though it traverses each line
     *          twice (first to split into new line, then to actually process line and add words to trie) it is
     *          still O(numChars) time
     *      O(numWords) = adds occurrence to each word, performs O(1) operation numWords times
     *      O(numLines) = must check if we're in a new section after each line, performs O(1) operation numLines
     *          number of times
     *
     * @param filename
     *      The filename of the document to read words and occurences into a trie
     *
     * @throws IOException
     *      If the file cannot be read
     */
    public void formTrieFromFile(String filename) throws IOException {
        //Create reader for text
        BufferedReader documentReader = new BufferedReader(new FileReader(filename));

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
            reference = documentTrie.getRoot(); //Start adding word from root
            occurrence = new HashPair<>(lineNumber, 1); //First word occurs at start of line

            for(int i = 0; i < line.length(); i++) {
                //Non-alphanumerical characters that arent apostrophes also mark the end of words - add word to trie
                if(!(Character.isLetterOrDigit(line.charAt(i)) || line.charAt(i) == '\'' || line.charAt(i) == ' ')) {
                    //Check we've traversed a non-empty word
                    if(!reference.equals(documentTrie.getRoot())) {
                        reference.addOccurrence(occurrence, sectionNumber);
                    }

                    //Set occurrence position for next word
                    occurrence = new HashPair<>(lineNumber, i + 2); //Column number = i + 1: next word starts at i + 2
                    reference = documentTrie.getRoot();
                    continue;

                    //Handle space characters - end of word, add to trie
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
                    //Move start of word up one if apostrophe at start of word
                    if(i == 0 || (line.charAt(i - 1) == ' ')) {
                        occurrence.setRightValue(occurrence.getRightValue() + 1);
                        continue;
                    }

                    //Ignore apostrophes on ends of word
                    if (i + 1 == line.length() || (line.charAt(i + 1) == ' ')){
                        continue;
                    }
                }

                //General case - add leter and traverse down Trie
                reference = reference.addChild(line.charAt(i));

            }

            //Handle end of line
            reference.addOccurrence(occurrence, sectionNumber);
            lineNumber++;

            //Check if we've moved to new section
            if((sectionNumber + 1 < this.sectionStarts.size()) && (lineNumber >= this.sectionStarts.get(sectionNumber + 1))) {
                sectionNumber++;
            }
        }
    }

    /**
     * Determines the number of times the word appears in the document.
     *
     * @bigO
     *      O(word.length()): calls a O(word.length()) function, performs constant
     *      time comparison and calls constant time function
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

    /**
     * Finds all occurrences of the phrase in the document.
     * A phrase may be a single word or a sequence of words.
     *
     * @bigO
     *      O(firstWord.length() + firstWord.numOccurrences * phrase.length):
     *          O(firstWord.length()): traverses phrase to find a ' ' performing constant comparison operation
     *              on each character to split first word then traverses trie firstWordLength number of times
     *          O(firstWord.numOccurrences * phrase.length): Runs O(phrase.length) function for each occurrence
     *              of the first word in the phrase
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

        String word = phrase.split(" ")[0];

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

    /**
     * Private helper method to match phrase to text beginning at occurrence
     *
     * @bigO
     *      O(phrase.length()): iterates through each letter in phrase comparing it
     *      to character in text until mismatch or all letters traversed. HashMap accesses
     *      and character comparisons are O(1)
     *
     * @param occurrence
     *      <lineNumber, columnNumber> to start searching document from
     * @param phrase
     *      the phrase to search for
     *
     * @return
     *      Iff the phrase occurs at the specified occurence in the text
     */
    private boolean matchPatternFromOccurrence(HashPair<Integer, Integer> occurrence, String phrase) {
        int lineNumber = occurrence.getLeftValue();
        int columnNumber = occurrence.getRightValue();
        int stringIndex = 0;
        char c;

        while (stringIndex < phrase.length()) {
            //Skip blank lines
            if(this.textLines.get(lineNumber - 1).length() == 0) {
                lineNumber++;
                continue;
            }

            //Go to next line
            if (columnNumber > this.textLines.get(lineNumber - 1).length()) {
                //New line should correspond to space in search phrase
                if(phrase.charAt(stringIndex) != ' ') {
                    return false;
                }
                stringIndex++;
                lineNumber++;
                columnNumber = 1;
                continue;
            }

            c = this.textLines.get(lineNumber - 1).charAt(columnNumber - 1);

            //Valid char for comparison
            if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || (c == ' ')) {
                if (c != phrase.charAt(stringIndex++)) {
                    return false;
                }
                //Handle apostrophe
            } else if (c == '\'') {
                if ((columnNumber + 1 > this.textLines.get(lineNumber - 1).length()) || (columnNumber == 1)
                        || (this.textLines.get(lineNumber - 1).charAt(columnNumber - 1 - 1) == ' ')
                        || (this.textLines.get(lineNumber - 1).charAt(columnNumber - 1 + 1) == ' ')) {
                    //Apostrophe at start/end of word
                } else {
                    if (c != phrase.charAt(stringIndex++)) {
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
     * @bigO:
     *      O((numOccurrencesPerNode + 1)x (27 ^ heightRoot)): calls
     *          O((numOccurrencesPerNode + 1)x (27 ^ heightRoot)) function and performs constant
     *          time comparison
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
     * @bigO:
     *      O(numWords * (wordLength + 1) + sum(numWordsOccurrences) * numWords):
     *
     *          Breaking it down
     *          O(numWords * (wordLength) + 1): getOccurrences runs in O(wordLength),
     *              ArrayList.append and HashSet.contains run in O(1), and this occurs numWords times
     *          O(sum(numWordsOccurrences) * numWords): finding intersection calls
     *              O(sum(numWordsOccurrences) * numWords) function
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
     * @bigO:
     *      O(numWords * (wordLength + 1) + O(sum(wordOccurrences.length) * words.length)):
     *          O(numWords * (wordLength) + 1): getOccurrences runs in O(wordLength),
     *              ArrayList.append and HashSet.contains run in O(1), and this occurs numWords times
     *          O(sum(wordOccurrences.length) * words.length): finding union calls
     *              O(sum(numElements) * numLists) function
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
        ArrayList<HashPair<Integer, Integer>> wordOccurences;
        //Get occurrences for every word
        for(int i = 0; i < words.length; i++) {
            if((words[i] == null) || (words[i].length() == 0)) {
                throw new IllegalArgumentException("Invalid search word");
            }

            if(!stopWords.contains(words[i])) {
                wordOccurences = documentTrie.getOccurrences(words[i].toLowerCase());
                if(wordOccurences.size() != 0) {
                    occurrences.append(wordOccurences);
                }
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
     * @bigO:
     *      O(numWords * (wordLength + 1) + sum(numWordsRequiredOccurrences) * numWordsRequired +
     *                          O(numRequiredOccurrences * wordsExcluded.length + sum(numExcludedOccurrences))):
     *
     *          Breaking it down
     *          O(numWords * (wordLength) + 1): getOccurrences runs in O(wordLength),
     *              ArrayList.append and HashSet.contains run in O(1), and this occurs numWords times
     *          O(sum(numWordsRequiredOccurrences) * numWordsRequired): finding intersection
     *                  calls O(sum(numElements) * numLists) function
     *           O(numRequiredOccurrences * wordsExcluded.length + sum(numExcludedOccurrences)): finding NOT calls
     *              O(numOccurrences * notOccurrences.length + sum(numNotOccurrences)) function
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


        //Get occurrences of not required words
        ArrayList<ArrayList<HashPair<Integer, Integer>>> excludedOccurrences = new ArrayList<>(wordsExcluded.length);
        for(int i = 0; i < wordsExcluded.length; i++) {
            if((wordsExcluded[i] == null) || (wordsExcluded[i].length() == 0)) {
                throw new IllegalArgumentException("Invalid excluded word");
            }

            if(!this.stopWords.contains(wordsExcluded[i])) {
                excludedOccurrences.append(documentTrie.getOccurrences(wordsExcluded[i].toLowerCase()));
            }
        }

        //Find intersection of required words
        ArrayList<Integer> intersection = Intersection.getIntersections(requiredOccurrences);

        //Remove occurrences of excluded words from intersection
        return Intersection.getNot(intersection, excludedOccurrences).toJavaArrayList();
    }

    /**
     * Returns the section numbers corresponding to the specified titles and a HashSet for each word of the section
     * numbers they occur in
     *
     * @bigO
     *      O(numTitles + numWords)
     *          O(numTitles): performs comparisons, HashMap access and array assignment (all O(1)) numTitles times
     *          O(numWords): performs comparisons, HashSet contains, ArrayList appends (all O(1)) numWords times
     *
     * @param titles
     *      An array of titles of sections in the document
     * @param words
     *      An array of words in the document
     * @return
     */
    private HashPair<Integer[], ArrayList<HashSet<Integer>>> setupSectionSearch(String[] titles, String[] words) {

        //The section numbers of the titles
        Integer[] sectionNumbers = new Integer[titles.length];
        for(int i = 0; i < titles.length; i++) {
            if((titles[i] == null) || (titles[i].length() == 0)) { throw new IllegalArgumentException("Invalid title");
            } else {
                HashPair<Integer, Integer> sectionHashPair = this.sectionIndexes.get(titles[i].toLowerCase());
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

    /**
     * Integer comparator based on primitive value, used for binary search of section numbers
     */
    private class IntegerComparator implements Comparator<Integer> {

        /**
         * Compares two integers based on value.
         *
         * @bigO
         *      O(1): constant time calculation
         *
         * @param a
         *      An Integer for comparison
         * @param b
         *      An Integer for comparison
         * @return
         *      Returns > 0 if a > b, 0 if a = b, and < 0 if a < b
         */
        @Override
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    /**
     * Adds all occurrences of the specified word in the section to occurrencesFound
     *
     * @bigO
     *      O(word.length() + log(numOccurrences) + numSectionOccurrences):
     *
     *          Breaking it down
     *          O(word.length()): traverses documentTrie down word.length() nodes and returns sectionNumbers
     *              and occurrences in O(1)
     *          O(log(numOccurrences)): binary search of section numbers to find sectionNumber
     *              (numSectionNumbers == numOccurrences)
     *          O(numSectionOccurrences): traverse all occurrences in specified section and performs constant append
     *
     * @param word
     *      The word to add occurrences of
     * @param sectionNumber
     *      The section number of occurrences to add
     * @param occurrencesFound
     *      The ArrayList to add <lineNumber, columnNumber, word> occurrences to
     */
    private void addAllOccurrencesOfWordInSection(String word, Integer sectionNumber,
                                                  ArrayList<Triple<Integer, Integer, String>> occurrencesFound) {

        ArrayList<Integer> sectionNumbers = documentTrie.getSectionNumbers(word);
        ArrayList<HashPair<Integer, Integer>> occurrences = documentTrie.getOccurrences(word);

        //Binary search to find index of an occurrence in section
        int sectionNumberIndex = sectionNumbers.binarySearch(new IntegerComparator(), sectionNumber);

        //Find all other occurrences in section (will be around sectionNumberIndex)
        int j = sectionNumberIndex;
        //Add all occurrences before
        while((j >= 0) &&(sectionNumbers.get(j).equals(sectionNumber))) {
            occurrencesFound.append(new Triple<>(occurrences.get(j).getLeftValue(),
                    occurrences.get(j).getRightValue(), word));
            j--;
        }

        //Add all occurrences after
        j = sectionNumberIndex + 1;
        while((j < sectionNumbers.size()) &&(sectionNumbers.get(j).equals(sectionNumber))) {
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
     * @bigO
     *      O(numTitles + numWords + numTitles*numWords(word.length() + log(numOccurrences) + numSectionOccurrences + 1))
     *
     *        Breaking it down
     *        O(numTitles + numWords): calls setupSectionSearch which runs in O(numTitles + numWords)
     *        O(numTitles*numWords(word.length() + log(numOccurrences) + numSectionOccurrences + 1)) : calls
     *        addOccurrencesAND numTitles times, it runs in O(numWords(word.length() + log(numOccurrences) + numSectionOccurrences + 1))
     *
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

    /**
     * Adds all occurrences of words in the specified section to occurrencesFound iff they all
     * occur in the section, otherwise does nothing.
     *
     * @bigO
     *      O(numWords(word.length() + log(numOccurrences) + numSectionOccurrences + 1))
     *
     *          Breaking it down
     *          O(numWords): calls HashSet contains O(1), numWords times
     *          O(numWords*(word.length() + log(numOccurrences) + numSectionOccurrences)): calls
     *              addAllOccurrencesOfWordInSection numWords times which runs in
     *              O(word.length() + log(numOccurrences) + numSectionOccurrences)
     *
     * @param sectionNumber
     *      The section number to add occurrences in
     * @param wordSections
     *      A HashSet for each word of the sections they occur in
     * @param words
     *      The words to possibly add their occurrences of
     * @param occurrencesFound
     *      An ArrayList of <lineNumber, columnNumber, word> to possibly add to
     */
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
     * @bigO
     *      O(numTitles + numWords + numTitles* numWords*(word.length() + log(numOccurrences) + numSectionOccurrences))
     *
     *          Breaking it down
     *          O(numTitles + numWords): calls setupSectionSearch which runs in O(numTitles + numWords)
     *          O(numTitles* numWords*(word.length() + log(numOccurrences) + numSectionOccurrences)):
     *              calls addOccurrencesOr numTitles times which runs in
     *              O(numWords*(word.length() + log(numOccurrences) + numSectionOccurrences))
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


    /**
     * Adds all occurrences of words in the specified section to occurrencesFound iff one of them
     * occurs in the section, otherwise does nothing.
     *
     * @bigO
     *      O(numWords*(word.length() + log(numOccurrences) + numSectionOccurrences)):
     *          calls addAllOccurrencesOfWordInSection numWords times and this runs in
     *          O(word.length() + log(numOccurrences) + numSectionOccurrences)
     *
     * @param sectionNumber
     *      The section number to add occurrences in
     * @param wordSections
     *      A HashSet for each word of the sections they occur in
     * @param words
     *      The words to possibly add their occurrences of
     * @param occurrencesFound
     *      An ArrayList of <lineNumber, columnNumber, word> to possibly add to
     */
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
     * @bigO
     *      O(numTitles + numWordsRequired + numWordsExcluded + numTitles*(numWordsExcluded +
     *                          numWordsRequired * (word.length() + log(numOccurrences) + numSectionOccurrences + 1))):
     *
     *          Breaking it down
     *          O(numTitles + numWordsRequired + numWordsExcluded): calls setupSectionSearch which runs
     *              in O(numTitles + numWords) and performs constant HashSet contains query for each excluded word
     *          O(numTitles*(numWordsExcluded + numWordsRequired * (word.length() + log(numOccurrences) + numSectionOccurrences + 1))):
     *              calls addOccurrencesNOT for each title which runs in
     *              O(numWordsExcluded + numWordsRequired * (word.length() + log(numOccurrences) + numSectionOccurrences + 1))
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


    /**
     * Adds all occurrences of the required words in the specified section to occurrencesFound iff they
     * all occur in the specified section and none of the excluded words occur in the section
     * @bigO
     *      O(numWordsExcluded + numWordsRequired * (word.length() + log(numOccurrences) + numSectionOccurrences + 1)):
     *          O(numWordsRequired): performs constant HashSet query for each required word
     *          O(numWordsExcluded): performs constant HashSet query for each excluded word
     *          O(numWordsRequired * (word.length() + log(numOccurrences) + numSectionOccurrences)): calls
     *              addAllOccurrencesOfWordInSection for each required word, which runs in
     *              O(word.length() + log(numOccurrences) + numSectionOccurrences)
     *
     * @param sectionNumber
     *      The section number to add occurrences in
     * @param wordsRequiredSections
     *      A HashSet for each required word of the sections they occur in
     * @param wordsRequired
     *      The required words to possibly add their occurrences of
     * @param wordsExcludedSections
     *      A HashSet for each excluded word of the sections they occur in
     * @param occurrencesFound
     *      An ArrayList of <lineNumber, columnNumber, word> to possibly add to
     */
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
     * @bigO
     *      O(numAllWords + numTitles * (numAllWords*(word.length() + log(numOccurrences) + numSectionOccurrences + 1) + 1)):
     *
     *          Breaking it down
     *          O(numTitles + numAllWords): calls setupSectionSearch which runs
     *              in O(numTitles + numRequiredWords) and performs constant HashSet contains query for each or word
     *          O(numTitles * numAllWords*(word.length() + log(numOccurrences) + numSectionOccurrences + 1)):
     *              calls addOccurrencesANDOR for each title which runs in
     *              O(numAllWords*(word.length() + log(numOccurrences) + numSectionOccurrences + 1))
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

    /**
     * Adds all occurrences of the required words in the specified section to occurrencesFound iff they
     * all occur in the specified section and at least one or word also occurs in the section. Adds all
     * occurrences of the or words that occur in this section if the above condition is met
     *
     * @bigO
     *      O(numAllWords*(word.length() + log(numOccurrences) + numSectionOccurrences + 1)):
     *
     *          Breaking it down
     *          O(numAllWords): performs O(1) HashSet contains query for each required and or word
     *          O(numAllWords*(word.length() + log(numOccurrences) + numSectionOccurrences)): calls
     *              addAllOccurrencesOfWordInSection for each andWord and orWord which runs in
     *              O(word.length() + log(numOccurrences) + numSectionOccurrences)
     *
     *
     * @param sectionNumber
     *      The section number to add occurrences in
     * @param wordsRequiredSections
     *      A HashSet for each required word of the sections they occur in
     * @param wordsRequired
     *      The required words to possibly add their occurrences of
     * @param orWordsSections
     *      A HashSet for each or word of the sections they occur in
     * @param orWords
     *      The or words to possibly add their occurrences of
     * @param occurrencesFound
     *      An ArrayList of <lineNumber, columnNumber, word> to possibly add to
     */
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


    /**
     * Helper method to convert my ArrayList and HashPair implementations to the required java.util.ArrayList
     * Pair classes
     *
     * @param list
     *      Internal data representation
     *
     * @return
     *      External data representation
     */
    private java.util.ArrayList<Pair<Integer, Integer>> dataTypeAdapter(ArrayList<HashPair<Integer, Integer>> list) {
        java.util.ArrayList<Pair<Integer, Integer>> convertedList = new java.util.ArrayList<>(list.size());

        for(int i = 0; i < list.size(); i++) {
            convertedList.add(new Pair<>(list.get(i).getLeftValue(), list.get(i).getRightValue()));
        }

        return convertedList;
    }
}