package comp3506.assn2.utils;



/**
 * A trie node for storing symbols from text
 * 
 * @author Sam Eadie <s.eadie@uq.edu.au>
 *
 */
public class TrieNode {

    protected final static int INITAL_CHILDREN_SPACE = 4;

	private char value;
	private ArrayList<TrieNode> children;
	private TrieNode parent;
    private boolean isWord;

    /**
     * Constructor for TrieNode
     *
     * @param value
     *      char stored in this node
     * @param isWord
     *      whether this node is the end of a word
     * @param initialChildrenSpace
     *      initial size of array to store chilren
     */
	public TrieNode(char value, TrieNode parent, boolean isWord, int initialChildrenSpace) {
		this.value = value;
		this.parent = parent;
		this.children = new ArrayList<>(initialChildrenSpace);
		this.isWord = isWord;
	}

    /**
     * Basic constructor for TrieNode using default initial array size
     *
     * @param value
     *      char stored in this node
     * @param isWord
     *      whether this node is the end of a word
     */
	public TrieNode(char value, TrieNode parent, boolean isWord) {
	    this(value, parent, isWord, INITAL_CHILDREN_SPACE);
    }

    /**
     * Empty constructor for TrieNode, uses null value and default initial array size
     */
    public TrieNode() {
	    this((char)(0), null, false);
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

	public TrieNode getParent() {return this.parent;}

	public TrieNode getChild(char letter) {
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
     *      An array of children TrieNodes
     */
	public ArrayList<TrieNode> getChildren() { return children; }

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
     * Adds the value as a child of this TrieNode if not already.
     *
     * @param value
     *      The value of the child node
     * @return
     *      The child node
     */
	public TrieNode addChild(char value) {
	    //If value already a child, return child
	    for(int i = 0; i < this.children.size(); i++) {
	        if (this.children.get(i).getValue() == value) {
	            return this.children.get(i);
            }
        }

        //Add value to children
        TrieNode newChild = new TrieNode(value, this, false);
        this.children.append(newChild);

        return newChild;
    }
}
