package comp3506.assn2.utils;

/**
 * A set implementation backed by a HashMap
 *
 * @bigO
 *      O(N) space complexity: contains HashMap with O(N) space complexity
 *
 * @param <E>
 *      The type of elements stored in the set
 */
public class HashSet<E> {

    private static final Integer VALUE = 1;

    private HashMap<E, Integer> hashMap;

    /**
     * Initialises an empty HashSet of specified initial capacity
     *
     * @bigO
     *      O(1): constant time initialisation
     *
     * @param initialSize
     *      The initial capacity of the HashSet
     */
    public HashSet(int initialSize) {
        hashMap = new HashMap(initialSize);
    }

    /**
     * Initialises an empty HashSet with default initial size
     *
     * @bigO
     *      O(1): constant time initialisation
     */
    public HashSet() {
        hashMap = new HashMap();
    }

    /**
     * Returns the number of elements in the set
     *
     * @bigO
     *      O(1): calls constant time function
     *
     * @return
     *      The number of elements in the set
     */
    public int getSize() {
        return this.hashMap.getSize();
    }

    /**
     * Adds the specified element to the set
     *
     * @bigO
     *      O(1) average case, O(N) worst case, calls HashMap.put
     *
     * @param element
     *      The element to add to the set
     */
    public void put(E element) {
        hashMap.put(element, VALUE);
    }

    /**
     * Removes the specified element from the set
     *
     * @bigO
     *     O(1) average case, O(N) worst case, calls HashMap.remove
     *
     * @param element
     *      The element to remove from the set
     */
    public void remove(E element){
        hashMap.remove(element);
    }

    /**
     * Returns true iff the set contains the specified element, else false
     *
     * @bigO
     *      O(1) average case, O(N) worst case, calls HashMap.get
     *
     * @param element
     *      The element to query existence in the set
     *
     * @return
     *      True iff the set contains the specified element, else false
     */
    public boolean contains(E element) {
        return hashMap.get(element) == VALUE;
    }
}
