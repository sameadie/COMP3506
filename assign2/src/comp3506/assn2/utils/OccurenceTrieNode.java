package comp3506.assn2.utils;

/**
 * A trie node for storing occurences of words in text
 * 
 * @author Sam Eadie <s.eadie@uq.edu.au>
 *
 */
public class OccurenceTrieNode {
    protected final static int INITAL_CHILDREN_SPACE = 4;

    private char value;
    private ArrayList<OccurenceTrieNode> children;
    private OccurenceTrieNode parent;
    private boolean isWord;

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
        this.value = value;
        this.parent = parent;
        this.children = new ArrayList<>(initialChildrenSpace);
        this.isWord = isWord;
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
	    this(value, parent, isWord, INITAL_CHILDREN_SPACE);
    }

    /**
     * Empty constructor for OccurenceTrieNode, uses null value and default initial array size
     */
    public OccurenceTrieNode() {
	    this((char)(0), null, false);
    }

    /**
     * Returns all occurences of the word terminated by this OccurenceTrieNode
     *
     * @return
     *      A list of occurences of this word as EqualsPairs of line number and
     *      initial column number
     */
    public ArrayList<EqualsPair<Integer, Integer>> getOccurences() {
        return this.occurences;
    }

    /**
     * Returns the number of occurences of the word terminated by this OccurenceTrieNode
     *
     * @return
     *      The number of occurences of this word
     */
    public int getNumOccurences() { return this.occurences.size(); }

    /**
     * Adds the location of an occurence of the word terminated by this
     * OccurenceTrieNode
     *
     * @param occurence
     *      An occurence as a EqualsPair of line number, initial column number
     */
	public void addOccurence(EqualsPair<Integer, Integer> occurence) {
	    this.setIsWord(true);
        this.occurences.append(occurence);
    }

    /**
     * Returns the value stored at this node
     *
     * @return
     *      The value stored at this node
     */
    public char getValue() {
        return value;
    }

    /**
     * Returns the parent of this node
     *
     * @return
     *      the node' parent
     */
    public OccurenceTrieNode getParent() {return this.parent;}

    /**
     * Returns the child of this node with the specified letter
     *
     * @param letter
     *      The value of the child node
     * @return
     *      The node's child with specified letter, else null
     */
    public OccurenceTrieNode getChild(char letter) {
        for(int i = 0; i < this.children.size(); i++) {
            if(this.children.get(i).getValue() == letter) {
                return this.children.get(i);
            }
        }
        return null;
    }

    /**
     * Returns the children of this node
     *
     * @return
     *      An array of children OccurenceTrieNodes
     */
    public ArrayList<OccurenceTrieNode> getChildren() { return children; }

    /**
     * Returns the number of children this node has
     *
     * @return
     *      The number of children this node has
     */
    public int getNumChildren() { return this.children.size(); }

    /**
     * Returns if this node marks the end of a valid word
     *
     * @return
     *      whether this node marks the end of a valid word
     */
    public boolean isWord() { return this.isWord; }

    /**
     * Sets whether this node marks the end of a valid word
     *
     * @param isWord
     *      whether this node marks the end of a valid word
     *
     */
    public void setIsWord(boolean isWord) { this.isWord = isWord; }

    /**
     * Adds the value as a child of this OccurenceTrieNode if not already.
     *
     * @param value
     *      The value of the child node
     * @return
     *      The child node
     */
    public OccurenceTrieNode addChild(char value) {
        //If value already a child, return child
        for(int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i).getValue() == value) {
                return this.children.get(i);
            }
        }

        //Add child with value to children
        OccurenceTrieNode newChild = new OccurenceTrieNode(value, this, false);
        this.children.append(newChild);

        return newChild;
    }

    @Override
    public String toString() {
        return String.format("{%c}", this.value);
    }
}