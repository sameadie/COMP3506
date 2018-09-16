package comp3506.assn2.utils;



/**
 * A trie structure for OccurenceTrieNodes 
 * 
 * @author Sam Eadie <s.eadie@uq.edu.au>
 *
 */
public class OccurenceTrie {

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
     * Returns the longest prefix of the specified word in the trie and the
     * length of this prefix
     * @param word
     *      the word to attempt to find in the try
     * @return
     *      OccurenceTrieNode - the node terminating the longest suffix of the specified
     *                  word in the trie
     *      int      - the length of the longest found prefix
     */
    public EqualsPair<OccurenceTrieNode, Integer> getLongestPrefixNode(String word) {
        OccurenceTrieNode reference = this.root;
        OccurenceTrieNode next = reference;
        int stringIndex = 0;

        do {
            reference = next;
            next = reference.getChild(word.charAt(stringIndex++));
        } while((stringIndex < word.length()) && (next != null));

        if(next == null) {
            return new EqualsPair(reference, stringIndex - 1);
        } else {
            return new EqualsPair(next, stringIndex);
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
    public ArrayList<EqualsPair<Integer, Integer>> getOccurences(String word) {
        EqualsPair<OccurenceTrieNode, Integer> longestPrefix = getLongestPrefixNode(word);

        //Word was in trie: longest prefix == word
        if(longestPrefix.getRightValue() == word.length()) {
            return longestPrefix.getLeftValue().getOccurences();
        }
        return null;
    }

    /**
     * Helper method to recursively traverse subtree and accumulate occurences
     *
     * @param root
     *      The root of the subtree to traverse
     * @param occurences
     *      An ArrayList of occurences to add to
     */
    private void getOccurencesRecursiveHelper(OccurenceTrieNode root, ArrayList<EqualsPair<Integer, Integer>> occurences) {
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
    public ArrayList<EqualsPair<Integer, Integer>> getOccurencesForSubtree(String prefix) {
        Pair<OccurenceTrieNode, Integer> longestPrefix= getLongestPrefixNode(prefix);

        //Prefix not in trie
        if(longestPrefix.getRightValue() != prefix.length()) {
            return null;
        }

        ArrayList<EqualsPair<Integer, Integer>> occurences = new ArrayList<EqualsPair<Integer, Integer>>();

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
    public void addOccurence(String word, EqualsPair<Integer, Integer> occurence) {
        //Find longest prefix of word already in trie
        EqualsPair<OccurenceTrieNode, Integer> longestPrefix = getLongestPrefixNode(word);
        int stringIndex = longestPrefix.getRightValue();
        OccurenceTrieNode reference = longestPrefix.getLeftValue();

        //Add additional letters to trie
        while(stringIndex < word.length()) {
            reference = new OccurenceTrieNode(word.charAt(stringIndex++), reference, true);
        }

        //Add occurence to end of word in trie
        reference.addOccurence(occurence);
    }

    public boolean removeOccurence(String word, EqualsPair<Integer, Integer> occurence) {
        EqualsPair<OccurenceTrieNode, Integer> longestPrefix = getLongestPrefixNode(word);

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
