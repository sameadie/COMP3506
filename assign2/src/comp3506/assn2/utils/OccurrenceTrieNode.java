package comp3506.assn2.utils;

/**
 * A trie node for storing occurrences of words in text
 * 
 * @author Sam Eadie <s.eadie@uq.edu.au>
 *
 */
public class OccurrenceTrieNode {
    protected final static int INITIAL_CHILDREN_SPACE = 4;
    private final static int INITIAL_OCCURRENCES_SPACE = 2;

    private char value;
    private boolean isWord;

    private ArrayList<OccurrenceTrieNode> children;
    private OccurrenceTrieNode parent;

    //Occurrences of the word terminating at this node in the form HashPair<lineNumber, columnNumber>
	private ArrayList<HashPair<Integer, Integer>> occurrences;

	//Section numbers occurences of this word occur in
	private ArrayList<Integer> sectionNumbers;
	private HashSet<Integer> sectionSet;

    /**
     * Constructor for OccurrenceTrieNode
     *
     * @bigO
     *      O(1): constant time initialisation
     *
     * @param value
     *      char stored in this node
     * @param isWord
     *      whether this node is the end of a word
     * @param initialChildrenSpace
     *      initial size of array to store chilren
     */
	public OccurrenceTrieNode(char value, OccurrenceTrieNode parent, boolean isWord, int initialChildrenSpace) {
        this.value = value;
        this.parent = parent;
        this.children = new ArrayList<>(initialChildrenSpace);
        this.isWord = isWord;
		this.occurrences = new ArrayList<>(INITIAL_OCCURRENCES_SPACE);
		this.sectionNumbers = new ArrayList<>();
		this.sectionSet = new HashSet<>();
	}

    /**
     * Basic constructor for OccurrenceTrieNode using default initial array size
     *
     * @bigO
     *      O(1): calls constant time function
     *
     * @param value
     *      char stored in this node
     * @param isWord
     *      whether this node is the end of a word
     */
	public OccurrenceTrieNode(char value, OccurrenceTrieNode parent, boolean isWord) {
	    this(value, parent, isWord, INITIAL_CHILDREN_SPACE);
    }

    /**
     * Empty constructor for OccurrenceTrieNode, uses null value and default initial array size
     *
     * @bigO
     *      O(1): calls constant time function
     *
     */
    public OccurrenceTrieNode() {
	    this((char)(0), null, false);
    }

    /**
     * Returns the value stored at this node
     *
     * @bigO
     *      O(1): constant time access to variable reference
     *
     * @return
     *      The value stored at this node
     */
    public char getValue() {
        return value;
    }

    /**
     * Returns all occurrences of the word terminated by this OccurrenceTrieNode
     *
     * @bigO
     *      O(1): constant time access to variable reference
     *
     * @return
     *      A list of occurrences of this word as HashPairs of line number and
     *      initial column number
     */
    public ArrayList<HashPair<Integer, Integer>> getOccurrences() {
        return this.occurrences;
    }

    /**
     * Returns a HashSet of all sections the word terminating in this node occurs at
     *
     * @bigO
     *      O(1): constant time access to variable reference
     *
     * @return
     *      A HashSet of all sections this word occurs in
     *
     */
    public HashSet<Integer> getSectionSet() { return this.sectionSet; }

    /**
     * Returns an ArrayList of section numbers with corresponding indices to the
     * occurences of this node
     *
     * @bigO
     *      O(1): constant time access to variable reference
     *
     * @return
     *      A list of section numbers the occurrences of this node occur in
     */
    public ArrayList<Integer> getSectionNumbers() {
        return sectionNumbers;
    }

    /**
     * Adds an occurence to the node
     *
     * @bigO
     *      O(1): ArrayList append and HashSet put occur in constant time
     *
     * @param occurrence
     *      A lineNumber, columnNumber pair denoting the occurrence
     * @param sectionNumber
     *      The sectionNumber the word terminated by this node occured in
     */
	public void addOccurrence(HashPair<Integer, Integer> occurrence, Integer sectionNumber) {
	    if(occurrence != null) {
            this.setIsWord(true);
            this.occurrences.append(occurrence);
            this.sectionNumbers.append(sectionNumber);
            this.sectionSet.put(sectionNumber);
        }
    }

    /**
     * Returns the parent of this node
     *
     * @bigO
     *      O(1): returns stored reference in constant time
     *
     * @return
     *      the node' parent
     */
    public OccurrenceTrieNode getParent() {return this.parent;}

    /**
     * Returns the child of this node with the specified letter
     *
     * @bigO
     *      O(numChildren): searches children for letter, .getValue() runs in O(1)
     *      likewise character comparison is constant time. Could be implemented using
     *      the HashMap but maximum numChildren is 27 (alphabet + ') so linear runtime i
     *      sn't too bad here
     *
     * @param letter
     *      The value of the child node
     * @return
     *      The node's child with specified letter, else null
     */
    public OccurrenceTrieNode getChild(char letter) {
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
     * @bigO
     *      O(1): returns stored reference in constant time
     *
     * @return
     *      An array of children OccurrenceTrieNodes
     */
    public ArrayList<OccurrenceTrieNode> getChildren() { return children; }

    /**
     * Returns if this node marks the end of a valid word
     *
     * @bigO
     *      O(1): returns stored reference in constant time
     *
     * @return
     *      whether this node marks the end of a valid word
     */
    public boolean isWord() { return this.isWord; }

    /**
     * Sets whether this node marks the end of a valid word
     *
     * @bigO
     *      O(1): constant time value assignment
     *
     * @param isWord
     *      whether this node marks the end of a valid word
     *
     */
    public void setIsWord(boolean isWord) { this.isWord = isWord; }

    /**
     * Adds the value as a child of this OccurrenceTrieNode if not already and
     * returns the child node who's letter is the specified value
     *
     * @bigO
     *      O(numChildren): checks all children for letter corresponding to
     *      specified value. Array access and append run in O(1). Could be
     *      implemented in a HashMap to improve search time but numChildren
     *      is confined to 27 (alphabet + ') so not too important
     *
     * @param value
     *      The value of the child node
     * @return
     *      The child node
     */
    public OccurrenceTrieNode addChild(char value) {
        //If value already a child, return child
        for(int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i).getValue() == value) {
                return this.children.get(i);
            }
        }

        //Add child with value to children
        OccurrenceTrieNode newChild = new OccurrenceTrieNode(value, this, false);
        this.children.append(newChild);

        return newChild;
    }

    /**
     * Returns the string representation of this OccurrenceTrieNode
     *
     * @return
     *      The string representation of the class
     */
    @Override
    public String toString() {
        return String.format("{%c}", this.value);
    }
}