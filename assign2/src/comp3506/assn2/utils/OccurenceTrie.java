package comp3506.assn2.utils;


import java.io.BufferedReader;
import java.io.IOException;

/**
 * A trie structure for OccurenceTrieNodes 
 * 
 * @author Sam Eadie <s.eadie@uq.edu.au>
 *
 */
public class OccurenceTrie {

    private final static char APOSTROPHE_CHAR = '\'';
    private final static OccurenceTrieNode ROOT_NODE= new OccurenceTrieNode();
    protected final static int INITAL_CHILDREN_SPACES = 4;

	private OccurenceTrieNode root;
	private int depth;
	private int initialChildrenSpaces;

    /**
     * Constructor for specifying initial root node children capacity
     * 
     * @param initialChildrenSpaces
     *      Initial root node children capacity
     */
	public OccurenceTrie(int initialChildrenSpaces) {
		this.root = ROOT_NODE;
		this.initialChildrenSpaces = initialChildrenSpaces;
		this.depth = 0;
	}

    /**
     * Basic constructor using default children capacity
     */
	public OccurenceTrie() {
	    this(INITAL_CHILDREN_SPACES);
    }

    /**
     * Generates and returns a trie from a document
     *
     * @param documentReader
     *      A Buffered Reader for the file to generate a trie from
     * @return
     *      An OccurenceTrie populated from the specified file
     * @throws IOException
     *      If the file cannot be opened or accessed
     */
    public static OccurenceTrie formTrieFromFile(BufferedReader documentReader) throws IOException {
        int lineNumber = 1;
        String line;
        OccurenceTrie documentTrie = new OccurenceTrie();
        OccurenceTrieNode reference;
        Pair<Integer, Integer> occurence;

        while((line = documentReader.readLine()) != null) {
            line = line.toLowerCase();
            reference = documentTrie.getRoot();
            occurence = new Pair<>(lineNumber, 1);

            for(int i = 0; i < line.length(); i++) {

                //Ignore punctation that isn't apostrophes
                if(!(Character.isLetterOrDigit(line.charAt(i)) || line.charAt(i) == APOSTROPHE_CHAR || line.charAt(i) == ' ')) {
                    continue;

                    //Handle space characters - end of words
                } else if(line.charAt(i) == ' ') {
                    if(!reference.equals(documentTrie.getRoot())) {
                        reference.addOccurence(occurence);
                    }

                    //Column number = i + 1: next word starts at i + 2
                    occurence = new Pair<>(lineNumber, i + 2);
                    reference = documentTrie.getRoot();
                    continue;

                    //Handle apostrophe
                } else if (line.charAt(i) == APOSTROPHE_CHAR) {
                    //Ignore apostrophes on ends of word
                    if (i + 1 == line.length() || (line.charAt(i + 1) == ' ')){
                        continue;
                    }

                    //Move start of word up one if apostrophe at start of word
                    if(i == 0 || (line.charAt(i - 1) == ' ')) {
                        occurence.setRightValue(occurence.getRightValue() + 1);
                    }
                }

                //Traverse general case
                reference = reference.addChild(line.charAt(i));

            }

            //Handle end of line
            reference.addOccurence(occurence);
            lineNumber++;
        }
        return documentTrie;
    }

    /**
     * Returns the root of the Trie
     *
     * @return
     *      The OccurenceTrieNode at the root of the tree
     */
    public OccurenceTrieNode getRoot() {
        return this.root;
    }

    /**
     * Returns the longest prefix of the specified word in the trie and the
     * length of this prefix
     * @param word
     *      the word to attempt to find in the try
     * @return
     *      OccurenceTrieNode - the node terminating the longest suffix of the specified
     *                  word in the trie
     *      int      - the length of the longest found prefix
     */
    public Pair<OccurenceTrieNode, Integer> getLongestPrefixNode(String word) {
        OccurenceTrieNode reference = this.root;
        OccurenceTrieNode next = reference;
        int stringIndex = 0;

        if(word.length() == 0) {
            return new Pair<>(this.root, 0);
        }

        do {
            reference = next;
            next = reference.getChild(word.charAt(stringIndex++));
        } while((stringIndex < word.length()) && (next != null));

        if(next == null) {
            return new Pair(reference, stringIndex - 1);
        } else {
            return new Pair(next, stringIndex);
        }
    }

    /**
     * Returns the occurences of the specified word. Returns null if word not in trie
     *
     * @param word
     *      The word being queried for occurences
     * @return
     *      The occurences of the word in line number, column number pairs
     */
    public ArrayList<Pair<Integer, Integer>> getOccurences(String word) {
        Pair<OccurenceTrieNode, Integer> longestPrefix = getLongestPrefixNode(word);

        //Word was in trie: longest prefix == word
        if(longestPrefix.getRightValue() == word.length()) {
            return longestPrefix.getLeftValue().getOccurences();
        }
        return new ArrayList<Pair<Integer, Integer>>();
    }

    /**
     * Helper method to recursively traverse subtree and accumulate occurences
     *
     * @param root
     *      The root of the subtree to traverse
     * @param occurences
     *      An ArrayList of occurences to add to
     */
    private void getOccurencesRecursiveHelper(OccurenceTrieNode root, ArrayList<Pair<Integer, Integer>> occurences) {
        occurences.extend(root.getOccurences());

        for(int i = 0; i < root.getChildren().size(); i++) {
            getOccurencesRecursiveHelper(root.getChildren().get(i), occurences);
        }
    }

    /**
     * Returns all occurences of the given prefix
     *
     * @param prefix
     *      The prefix to query for occurences
     * @return
     *      The occurences of the specified prefix in the trie
     */
    public ArrayList<Pair<Integer, Integer>> getOccurencesForSubtree(String prefix) {
        Pair<OccurenceTrieNode, Integer> longestPrefix= getLongestPrefixNode(prefix);

        //Prefix not in trie
        if(longestPrefix.getRightValue() != prefix.length()) {
            return null;
        }

        ArrayList<Pair<Integer, Integer>> occurences = new ArrayList<Pair<Integer, Integer>>();

        //Traverse subtree rooted at longestPrefix's node - accumulate occurences
        getOccurencesRecursiveHelper(longestPrefix.getLeftValue(), occurences);
        return occurences;

    }

    /**
     * Adds an occurence to the node terminating the specified word, creating
     * branches as required
     *
     * @param word
     *      The word to add an occurence to
     * @param occurence
     *      The line number, column number pair of the occurence of the word
     */
    public void addOccurence(String word, Pair<Integer, Integer> occurence) {
        //Find longest prefix of word already in trie
        Pair<OccurenceTrieNode, Integer> longestPrefix = getLongestPrefixNode(word);
        int stringIndex = longestPrefix.getRightValue();
        OccurenceTrieNode reference = longestPrefix.getLeftValue();

        //Add additional letters to trie
        while(stringIndex < word.length()) {
            reference = new OccurenceTrieNode(word.charAt(stringIndex++), reference, true);
        }

        //Add occurence to end of word in trie
        reference.addOccurence(occurence);
    }

    public boolean removeOccurence(String word, Pair<Integer, Integer> occurence) {
        Pair<OccurenceTrieNode, Integer> longestPrefix = getLongestPrefixNode(word);

        //Word was in trie: longest prefix == word
        if(longestPrefix.getRightValue() == word.length()) {
            //Check for empty occurence list upon successful removal
            if (longestPrefix.getLeftValue().getOccurences().remove(occurence)) {
                if (longestPrefix.getLeftValue().getOccurences().size() == 0) {
                    longestPrefix.getLeftValue().setIsWord(false);
                }
                return true;
            }
        }
        return false;
    }

    protected OccurenceTrieNode createTrieNode(char value, OccurenceTrieNode parent, boolean isWord) {
	    return new OccurenceTrieNode(value, parent, isWord);
    }
}
