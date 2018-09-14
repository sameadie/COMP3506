package comp3506.assn2.utils;



/**
 * A trie node for storing occurences of words in text
 * 
 * @author Sam Eadie <s.eadie@uq.edu.au>
 *
 */
public class OccurenceTrieNode extends TrieNode{
    private final static int INITIAL_OCCURENCES_SPACE = 2;


	private ArrayList<EqualsPair<Integer, Integer>> occurences;

    /**
     * Constructor for OccurenceTrieNode
     *
     * @param value
     *      char stored in this node
     * @param isWord
     *      whether this node is the end of a word
     * @param initialChildrenSpace
     *      initial size of array to store chilren
     */
	public OccurenceTrieNode(char value, OccurenceTrieNode parent, boolean isWord, int initialChildrenSpace) {
		super(value, parent, isWord, initialChildrenSpace);
		this.occurences = new ArrayList<>(INITIAL_OCCURENCES_SPACE);
	}

    /**
     * Basic constructor for OccurenceTrieNode using default initial array size
     *
     * @param value
     *      char stored in this node
     * @param isWord
     *      whether this node is the end of a word
     */
	public OccurenceTrieNode(char value, OccurenceTrieNode parent, boolean isWord) {
	    this(value, parent, isWord, super.INITAL_CHILDREN_SPACE);
    }

    /**
     * Empty constructor for TrieNode, uses null value and default initial array size
     */
    public OccurenceTrieNode() {
	    this((char)(0), null, false);
    }

    /**
     * Returns all occurences of the word terminated by this TrieNode
     *
     * @return
     *      A list of occurences of this word as EqualsPairs of line number and
     *      initial column number
     */
    public ArrayList<EqualsPair<Integer, Integer>> getOccurences() {
        return this.occurences;
    }

    /**
     * Returns the number of occurences of the word terminated by this TrieNode
     *
     * @return
     *      The number of occurences of this word
     */
    public int getNumOccurences() { return this.occurences.size(); }

    /**
     * Adds the location of an occurence of the word terminated by this
     * TrieNode
     *
     * @param occurence
     *      An occurence as a EqualsPair of line number, initial column number
     */
	public void addOccurence(EqualsPair<Integer, Integer> occurence) {
	    this.setIsWord(true);
        this.occurences.append(occurence);
    }
}
