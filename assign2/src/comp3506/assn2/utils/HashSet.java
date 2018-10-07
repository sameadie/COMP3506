package comp3506.assn2.utils;

/**
 * A set implementation using a HashMap
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
     * @param initialSize
     *      The initial capacity of the HashSet
     */
    public HashSet(int initialSize) {
        hashMap = new HashMap(initialSize);
    }

    /**
     * Initialises an empty HashSet with default initial size
     */
    public HashSet() {
        hashMap = new HashMap();
    }

    /**
     * Returns the number of elements in the set
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
     * @param element
     *      The element to add to the set
     */
    public void put(E element) {
        hashMap.put(element, VALUE);
    }

    /**
     * Removes the specified element from the set
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
