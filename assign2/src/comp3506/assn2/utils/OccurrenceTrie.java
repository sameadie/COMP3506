package comp3506.assn2.utils;


/**
 * A trie structure for OccurrenceTrieNodes 
 *
 * @bigO
 *      O(N) space complexity: space proportionate to number of nodes
 *
 * @author Sam Eadie <s.eadie@uq.edu.au>
 *
 */
public class OccurrenceTrie {

    private final static OccurrenceTrieNode ROOT_NODE= new OccurrenceTrieNode();
    protected final static int INITAL_CHILDREN_SPACES = 4;

	private OccurrenceTrieNode root;
	private int depth;
	private int initialChildrenSpaces;

    /**
     * Constructor for specifying initial root node children capacity
     *
     * @bigO
     *      O(1): value assignment in constant time
     *
     * @param initialChildrenSpaces
     *      Initial root node children capacity
     */
	public OccurrenceTrie(int initialChildrenSpaces) {
		this.root = ROOT_NODE;
		this.initialChildrenSpaces = initialChildrenSpaces;
		this.depth = 0;
	}

    /**
     * Basic constructor using default children capacity
     *
     * @bigO
     *      O(1): calls constant time function
     */
	public OccurrenceTrie() {
	    this(INITAL_CHILDREN_SPACES);
    }

    /**
     * Returns the root of the Trie
     *
     * @bigO
     *      O(1): returns stored reference in constant time
     *
     * @return
     *      The OccurrenceTrieNode at the root of the tree
     */
    public OccurrenceTrieNode getRoot() {
        return this.root;
    }

    /**
     * Returns the longest prefix of the specified word in the trie and the
     * length of this prefix
     *
     * @bigO
     *      O(numChildren * word.length()): traverses down trie at most word.length() number
     *      of times. Getting children runs in O(numChildren) each time, O(numChildren) is essentially
     *      constant O(27) but can be improved using HashMap. Essentially O(word.length())
     *
     *
     * @param word
     *      the word to attempt to find in the try
     * @return
     *      OccurrenceTrieNode - the node terminating the longest suffix of the specified
     *                  word in the trie
     *      int      - the length of the longest found prefix
     */
    public HashPair<OccurrenceTrieNode, Integer> getLongestPrefixNode(String word) {
        OccurrenceTrieNode reference = this.root;
        OccurrenceTrieNode next = reference;
        int stringIndex = 0;

        if(word.length() == 0) {
            return new HashPair<>(this.root, 0);
        }

        //Traverse down trie
        do {
            reference = next;
            next = reference.getChild(word.charAt(stringIndex++));
        } while((stringIndex < word.length()) && (next != null));

        if(next == null) {
            return new HashPair(reference, stringIndex - 1);
        } else {
            return new HashPair(next, stringIndex);
        }
    }

    /**
     * Returns the OccurencesTrieNode that terminates the specified word. Returns
     * null if the word is not in the trie
     *
     * @bigO
     *      O(word.length()): calls O(word.length()) function and performs
     *      constant time integer comparison
     *
     * @param word
     *      The word to traverse the tree for
     * @return
     *      The node that terminates the specified word in the trie, else null
     */
    public OccurrenceTrieNode getNodeTerminatingWord(String word) {
        HashPair<OccurrenceTrieNode, Integer> longestPrefix = getLongestPrefixNode(word);

        //Word was in trie: longest prefix == word
        if(longestPrefix.getRightValue() == word.length()) {
            return longestPrefix.getLeftValue();
        }

        return null;
    }

    /**
     * Returns the occurrences of the specified word. Returns null if word not in trie
     *
     * @bigO
     *      O(word.length()): calls O(word.length()) function and performs constant time
     *      comparison
     *
     * @param word
     *      The word being queried for occurrences
     * @return
     *      The occurrences of the word in line number, column number pairs
     */
    public ArrayList<HashPair<Integer, Integer>> getOccurrences(String word) {
        OccurrenceTrieNode terminatingNode = getNodeTerminatingWord(word);

        if(terminatingNode == null) {
            return new ArrayList<HashPair<Integer, Integer>>();
        }

        return terminatingNode.getOccurrences();
    }

    /**
     * Returns the sections that the word occurs in
     *
     * @bigO
     *      O(word.length()): calls O(word.length()) function and performs constant time
     *      comparison
     *
     * @param word
     *      The query word
     *
     * @return
     *      A list of section numbers that the word occurs in
     */
    public ArrayList<Integer> getSectionNumbers(String word) {
        OccurrenceTrieNode terminatingNode = getNodeTerminatingWord(word);

        if(terminatingNode == null) {
            return new ArrayList<>();
        }

        return terminatingNode.getSectionNumbers();
    }

    /**
     * Helper method to recursively traverse subtree and accumulate occurrences
     *
     * @param root
     *      The root of the subtree to traverse
     * @param occurrences
     *      An ArrayList of occurrences to add to
     */
    private void getOccurrencesRecursiveHelper(OccurrenceTrieNode root, ArrayList<HashPair<Integer, Integer>> occurrences) {
        occurrences.extend(root.getOccurrences());

        for(int i = 0; i < root.getChildren().size(); i++) {
            getOccurrencesRecursiveHelper(root.getChildren().get(i), occurrences);
        }
    }

    /**
     * Returns all occurrences of the given prefix
     *
     * @bigO
     *      O((numOccurrencesPerNode)x (27 ^ heightRoot)):
     *
     *          Breaking it down
     *          O(numChildrenInSubtree*(numOccurrences + 1)) because .extend() is called on every child in
     *          subtree and this calls .append() numOccurrences times and runs in O(1). Nodes are visited in
     *          constant time and numChildrenInSubtree nodes are visited hence the +1 in the brackets
     *
     *          numChildrenInSubtree = childrenPerParent ^ longestSuffix = 27 ^ heightRoot
     *
     *          Combining these give: O((numOccurrencesPerNode)x (27 ^ heightRoot))
     *
     * @param prefix
     *      The prefix to query for occurrences
     * @return
     *      The occurrences of the specified prefix in the trie
     */
    public ArrayList<HashPair<Integer, Integer>> getOccurrencesForSubtree(String prefix) {
        OccurrenceTrieNode subtreeRoot = getNodeTerminatingWord(prefix);

        //Prefix not in trie
        if(subtreeRoot == null) {
            return null;
        }

        ArrayList<HashPair<Integer, Integer>> occurrences = new ArrayList<>();

        //Traverse subtree rooted at longestPrefix's node - accumulate occurrences
        getOccurrencesRecursiveHelper(subtreeRoot, occurrences);
        return occurrences;

    }

    /**
     * Adds an occurrence to the node terminating the specified word, creating
     * branches as required
     *
     * @bigO:
     *      O(word.length()): calls O(word.length()) function. Will add nodes and traverse until
     *      word.length() if not performed by getLongestPrefixNode therefore this runs in O(word.length()).
     *      Adding occurrences to node occurs in O(1)
     *
     * @param word
     *      The word to add an occurrence to
     * @param occurrence
     *      The line number, column number pair of the occurrence of the word
     */
    public void addOccurrence(String word, HashPair<Integer, Integer> occurrence, Integer sectionNumber) {
        //Find longest prefix of word already in trie
        HashPair<OccurrenceTrieNode, Integer> longestPrefix = getLongestPrefixNode(word);
        int stringIndex = longestPrefix.getRightValue();
        OccurrenceTrieNode reference = longestPrefix.getLeftValue();

        //Add additional letters to trie
        while(stringIndex < word.length()) {
            reference = new OccurrenceTrieNode(word.charAt(stringIndex++), reference, true);
        }

        //Add occurrence and sectionNumber to end of word in trie
        reference.addOccurrence(occurrence,sectionNumber);
    }
}
