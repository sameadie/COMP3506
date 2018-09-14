package comp3506.assn2.utils;



/**
 * A generic trie
 * 
 * @author Sam Eadie <s.eadie@uq.edu.au>
 *
 */
public class Trie {

    protected final static int INITAL_CHILDREN_SPACES = 4;
    private final static TrieNode ROOT_NODE= new TrieNode();

	private TrieNode root;
	private int depth;
	private int initialChildrenSpaces;

	public Trie(int initialChildrenSpaces) {
		this.root = ROOT_NODE;
		this.initialChildrenSpaces = initialChildrenSpaces;
		this.depth = 0;
	}

	public Trie() {
	    this(INITAL_CHILDREN_SPACES);
    }

    /**
     * Returns the longest prefix of the specified word in the trie and the
     * length of this prefix
     * @param word
     *      the word to attempt to find in the try
     * @return
     *      TrieNode - the node terminating the longest suffix of the specified
     *                  word in the trie
     *      int      - the length of the longest found prefix
     */
    public EqualsPair<TrieNode, Integer> getLongestPrefixNode(String word) {
        TrieNode reference = this.root;
        TrieNode next = reference;
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

	public ArrayList<EqualsPair<Integer, Integer>> getOccurences(String word) {
        EqualsPair<TrieNode, Integer> longestPrefix = getLongestPrefixNode(word);

        //Word was in trie: longest prefix == word
        if(longestPrefix.getRightValue() == word.length()) {
            return longestPrefix.getLeftValue().getOccurences();
        } else {
            return null;
        }
    }

    private void recursiveNodeHelper(ArrayList<EqualsPair<Integer, Integer>> occurences, TrieNode node) {
        occurences.extend(node.getOccurences());

        for(int i = 0; i < node.getOccurences().size(); i++) {
            recursiveNodeHelper(occurences, node.getChildren().get(i));
        }
    }

    public ArrayList<EqualsPair<Integer, Integer>> getOccurencesForSubtree(String prefix) {
        Pair<TrieNode, Integer> longestPrefix= getLongestPrefixNode(prefix);

        //Prefix not in trie
        if(longestPrefix.getRightValue() != prefix.length()) {
            return null;
        }

        ArrayList<EqualsPair<Integer, Integer>> occurences = new ArrayList<EqualsPair<Integer, Integer>>();

        //Traverse subtree rooted at longestPrefix's node - accumulate occurences
        recursiveNodeHelper(occurences, longestPrefix.getLeftValue());
        return occurences;

    }

    public void add(String word, EqualsPair<Integer, Integer> occurence) {
        //Find longest prefix of word already in trie
        EqualsPair<TrieNode, Integer> longestPrefix = getLongestPrefixNode(word);
        int stringIndex = longestPrefix.getRightValue();
        TrieNode reference = longestPrefix.getLeftValue();

        //Add additional letters to trie
        while(stringIndex < word.length()) {
            reference = new TrieNode(word.charAt(stringIndex++), reference, false);
        }

        //Add occurence to end of word in trie
        reference.addOccurence(occurence);
    }

    public boolean removeOccurence(String word, EqualsPair<Integer, Integer> occurence) {
        EqualsPair<TrieNode, Integer> longestPrefix = getLongestPrefixNode(word);

        //Word was in trie: longest prefix == word
        if(longestPrefix.getRightValue() == word.length()) {
            //Check for empty occurence list upon successful removal
            if(longestPrefix.getLeftValue().getOccurences().remove(occurence)) {
                if(longestPrefix.getLeftValue().getOccurences().size() == 0) {
                    longestPrefix.getLeftValue().setIsWord(false);
                }

                return true;
            }

            return false;

        //Can't remove occurence from word not in trie
        } else {
            return false;
        }
    }

	
}
