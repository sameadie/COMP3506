package comp3506.assn2.application;

import com.sun.xml.internal.bind.v2.TODO;
import comp3506.assn2.utils.*;

import java.io.*;
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

    private OccurenceTrie documentTrie;
    private HashMap<String, Integer> sectionIndexes;
    private HashSet<String> stopWords;

    //TODO: this
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
        //TODO: Error handling
        try {
            BufferedReader documentReader = new BufferedReader(new FileReader("files/" + documentFileName));
            //Remove byte order mark
            documentReader.read();
            this.documentTrie = OccurenceTrie.formTrieFromFile(documentReader);
            sectionIndexes = readSectionIndexes(new BufferedReader(new FileReader("files/" + indexFileName)));
            stopWords = readStopWords(new BufferedReader(new FileReader("files/" + stopWordsFileName)));
        } catch (Exception e) {
            System.err.println("IOException ");
        }
    }

    private HashMap<String, Integer> readSectionIndexes(BufferedReader documentReader) throws IOException {
        String line;
        String[] lineParts;

        HashMap<String, Integer> sectionIndexes = new HashMap<>();

        while ((line = documentReader.readLine()) != null) {
            line = line.toLowerCase();
            lineParts = line.split(",", 2);

            sectionIndexes.put(lineParts[0].toLowerCase(), Integer.parseInt(lineParts[1]));
        }

        return sectionIndexes;
    }

    private HashSet<String> readStopWords(BufferedReader documentReader) throws IOException {
        String line;
        HashSet<String> stopWords = new HashSet<>();

        while ((line = documentReader.readLine()) != null) {
            stopWords.put(line.toLowerCase());
        }

        return stopWords;
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

        ArrayList<Pair<Integer, Integer>> occurences = this.documentTrie.getOccurences(word);

        if(occurences == null) {
            return 0;
        } else {
            return occurences.size();
        }
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
    //TODO: going to have to redo this, search for phrases not just single words, can be over multiple lines
    public List<Pair<Integer,Integer>> phraseOccurrence(String phrase) throws IllegalArgumentException {
        if(phrase.length() == 0) {
            return new java.util.ArrayList<>();
        }

        ArrayList<Pair<Integer, Integer>> occurences = this.documentTrie.getOccurences(phrase);

        if(occurences == null) {
            return new java.util.ArrayList<Pair<Integer, Integer>>();
        } else {
            return occurences.toJavaArrayList();
        }
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

        ArrayList<Pair<Integer, Integer>> occurences = this.documentTrie.getOccurencesForSubtree(prefix);

        if(occurences == null) {
            return new java.util.ArrayList<Pair<Integer, Integer>>();
        } else {
            return occurences.toJavaArrayList();
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

        ArrayList<ArrayList<Pair<Integer, Integer>>> occurences = new ArrayList<>(words.length);

        //Get occurences of every word
        for(int i = 0; i < words.length; i++) {
            if((words[i] == null) || (words[i].length() == 0)) {
                throw new IllegalArgumentException("Invalid search word");
            }

            if(!stopWords.contains(words[i])) {
                occurences.append(documentTrie.getOccurences(words[i].toLowerCase()));
            }
        }

        //Find interesection of occurences
        return Intersection.getIntersections(occurences).toJavaArrayList();
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

        ArrayList<ArrayList<Pair<Integer, Integer>>> occurences = new ArrayList<>(words.length);

        //Get occurences for every word
        for(int i = 0; i < words.length; i++) {
            if((words[i] == null) || (words[i].length() == 0)) {
                throw new IllegalArgumentException("Invalid search word");
            }

            if(!stopWords.contains(words[i])) {
                occurences.append(documentTrie.getOccurences(words[i].toLowerCase()));
            }
        }

        //Find union of occurences
        return Intersection.getUnion(occurences).toJavaArrayList();
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
        ArrayList<ArrayList<Pair<Integer, Integer>>> requiredOccurences = new ArrayList<>(wordsRequired.length);

        if((wordsRequired == null ) || (wordsExcluded == null) ||
                (wordsRequired.length == 0) || (wordsExcluded.length == 0)) {
            throw new IllegalArgumentException("Invalid search words");
        }

        //Get occurences of required words
        for(int i = 0; i < wordsRequired.length; i++) {
            if((wordsRequired[i] == null) || (wordsRequired[i].length() == 0)) {
                throw new IllegalArgumentException("Invalid required word");
            }

            if(!stopWords.contains(wordsRequired[i])) {
                requiredOccurences.append(documentTrie.getOccurences(wordsRequired[i].toLowerCase()));
            }
        }

        //Find interesection of occurences
        ArrayList<Integer> intersection = Intersection.getIntersections(requiredOccurences);

        ArrayList<ArrayList<Pair<Integer, Integer>>> excludedOccurences = new ArrayList<>(wordsExcluded.length);
        ArrayList<Integer> pointers = new ArrayList<>(wordsExcluded.length);

        int includedPointer = 0;
        //Get occurences of not required words
        for(int i = 0; i < wordsExcluded.length; i++) {
            if((wordsExcluded[i] == null) || (wordsExcluded[i].length() == 0)) {
                throw new IllegalArgumentException("Invalid excluded word");
            }

            if(!this.stopWords.contains(wordsExcluded[i])) {
                excludedOccurences.append(documentTrie.getOccurences(wordsExcluded[i].toLowerCase()));
                pointers.append(0);
            }
        }


        while(includedPointer < intersection.size()) {
            boolean allGreater = true;
            for(int i = 0; i < pointers.size(); i++) {
                if(excludedOccurences.get(i).get(pointers.get(i)).getLeftValue() < intersection.get(includedPointer)) {
                    pointers.set(i, pointers.get(i) + 1);
                    allGreater = false;
                } else if (excludedOccurences.get(i).get(pointers.get(i)).getLeftValue().equals(intersection.get(includedPointer))) {
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
        throw new UnsupportedOperationException("Search.simpleAndSearch() Not Implemented.");
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
        throw new UnsupportedOperationException("Search.simpleOrSearch() Not Implemented.");
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
        throw new UnsupportedOperationException("Search.simpleNotSearch() Not Implemented.");
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
        throw new UnsupportedOperationException("Search.compoundAndOrSearch() Not Implemented.");
    }


}
