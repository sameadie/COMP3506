package comp3506.assn2.application;

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

    private final static char APOSTROPHE_CHAR = '\'';
    private OccurenceTrie documentTrie;

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
            BufferedReader documentReader = new BufferedReader(new FileReader(documentFileName));
            //Remove byte order mark
            documentReader.read();
            this.documentTrie = formTrieFromFile(documentReader);
        } catch (Exception e) {
            System.err.println("IOException ");
        }

        //BufferedReader indexReader = new BufferedReader(new FileReader(indexFileName));
        //BufferedReader stopWordsReader = new BufferedReader(new FileReader(stopWordsFileName));
    }

    //TODO: Delete this function
    public void printTrieOccurences() {

        documentTrie.getOccurencesForSubtree("").toJavaArrayList().forEach(occurence -> {
            if(occurence == null) {
                System.out.println("NULL");
            } else {
                System.out.println(String.format("(%d, %d)", occurence.getLeftValue(), occurence.getRightValue()));
            }
        });
    }

    public OccurenceTrie formTrieFromFile(BufferedReader documentReader) throws IOException {
        int lineNumber = 1;
        String line;
        OccurenceTrie documentTrie = new OccurenceTrie();
        OccurenceTrieNode reference;
        Pair<Integer, Integer> occurence;

        while((line = documentReader.readLine()) != null) {
            line = line.toLowerCase();
            //System.out.println(line);

            reference = documentTrie.getRoot();
            occurence = new Pair<>(lineNumber, 1);

            for(int i = 0; i < line.length(); i++) {
                //System.out.println(String.format("%d, %d", i, ((int)line.charAt(i))));
                if(line.charAt(i) == ' ') {
                    reference.addOccurence(occurence);
                    //System.out.println(String.format("Adding (%d, %d) to %s", occurence.getLeftValue(), occurence.getRightValue(), reference.getValue()));

                    //Column number = i + 1: next word starts at i + 2
                    occurence = new Pair<>(lineNumber, i + 2);
                    reference = documentTrie.getRoot();
                    continue;
                } else if (line.charAt(i) == APOSTROPHE_CHAR) {
                    //Ignore apostrophes on ends of word
                    if ((line.charAt(i + 1) == ' ') || (line.charAt(i - 1) == ' ')) {
                        continue;
                    }
                }

                //Traverse or add character to Trie
                reference = reference.addChild(line.charAt(i));

            }
            reference.addOccurence(occurence);
            //System.out.println(String.format("Adding (%d, %d) to %s", occurence.getLeftValue(), occurence.getRightValue(), reference.getValue()));
            lineNumber++;
        }
        return documentTrie;
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
        throw new UnsupportedOperationException("Search.wordsOnLine() Not Implemented.");
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
        throw new UnsupportedOperationException("Search.someWordsOnLine() Not Implemented.");
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
        throw new UnsupportedOperationException("Search.wordsNotOnLine() Not Implemented.");
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
