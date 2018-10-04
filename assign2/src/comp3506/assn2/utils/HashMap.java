package comp3506.assn2.utils;

/**
 * A generic HashMap implementation utilising linear-probing
 *
 * @param <K>
 *     The type of keys in the HashMap
 * @param <V>
 *     The type of values in the HashMap
 */
public class HashMap<K, V> {

    /**
     * A generic entry in the HashMap
     */
    public class Entry {
        private K key;
        private V value;

        /**
         * Initialises an Entry: a key, value pair
         *
         * @param key
         *      The key of this entry
         * @param value
         *      The value of this entry
         */
        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns the key of this Entry
         *
         * @return
         *      The key of this Entry
         */
        public K getKey() {
            return this.key;
        }

        /**
         * Returns the value of this Entry
         *
         * @return
         *      The value of this Entry
         */
        public V getValue() {
            return this.value;
        }

        /**
         * Sets the Entry's value
         *
         * @param newValue
         *      The new value for the entry
         */
        public void setValue(V newValue) {
            this.value = newValue;
        }
    }

    private static final double MAX_LOAD_FACTOR = 0.75;
    private static final int RESIZE_FACTOR = 2;
    private static final int DEFAULT_INITIAL_SIZE = 10;

    private Entry AVAILABLE_POSITION;
    private int size;
    private int numEntries = 0;
    private Entry[] entries;

    /**
     * Initialises a HashMap with a specified initial size
     *
     * @param initialSize
     *      The initial size of the HashMap's array
     */
    public HashMap(int initialSize) {
        this.size = initialSize;
        entries = (Entry[])new Object[initialSize];
        AVAILABLE_POSITION = new Entry(null, null);
    }

    /**
     * Initialises a HashMap with default initial size
     */
    public HashMap() {
        this(DEFAULT_INITIAL_SIZE);
    }

    /**
     * Returns the index of the key if found, else returns -i-1 where i
     * is the index of an available slot
     *
     * @param key
     *      The key to find the index of
     *
     * @return
     *      The index of the key, or an available empty slot
     *
     */
    private int findKeyEntry(K key) {
        int availableIndex = 0;
        if(key == null) {
            return -1;
        }

        int i = key.hashCode();
        int j = i;

        do {
            if(this.entries[i] == null) {
                return -i -1;
            } else if(this.entries[i] == AVAILABLE_POSITION) {
                availableIndex = i;
                i = (i+1) & this.size;
            } else if(key.equals(this.entries[i].getKey())) {
                return i;
            } else {
                i = (i + 1) % this.size;
            }
        } while (i != j);

        return -availableIndex - 1;
    }

    /**
     * Doubles the capacity of the HashMap, and rehashes all elements
     * into the new HashMap
     */
    private void rehashTable() {
        this.size *= RESIZE_FACTOR;
        Entry[] oldEntries = this.entries;
        this.entries = (Entry[])new Object[this.size];

        for(int i = 0; i < oldEntries.length; i++) {
            if((oldEntries[i] != null) && (oldEntries[i] != AVAILABLE_POSITION)) {
                int newIndex = findKeyEntry(oldEntries[i].getKey());
                this.entries[-newIndex-1] = oldEntries[i];
            }
        }
    }

    /**
     * Returns the value associated with the specified key. Returns null if
     * the key is not found
     *
     * @param key
     *      The key to search for
     *
     * @return
     *      The value associated with the specified key, else null
     */
    public V get(K key) {
        int i = findKeyEntry(key);
        if (i < 0) {
            return null;
        } else {
            return this.entries[i].getValue();
        }
    }

    /**
     * Adds the key, value pair to the HashMap. If the key already exists
     * in the HashMap, the associated value is updated
     *
     * @param key
     *      The key to add to the HashMap
     * @param value
     *      The value to add to the HashMap
     */
    public void put(K key, V value) {
        if(this.numEntries >= this.MAX_LOAD_FACTOR * this.size) {
            rehashTable();
        }

        int i = findKeyEntry(key);

        if(i < 0) {
            this.entries[-i-1] = new Entry(key, value);
            this.numEntries++;
        } else {
            this.entries[i].setValue(value);
        }
    }

    /**
     * Removes the key, value paired associated with the specified key
     *
     * @param key
     *      The key to remove
     */
    public void remove(K key) {
        int i = findKeyEntry(key);

        if(i >= 0) {
            this.entries[i] = AVAILABLE_POSITION;
            this.numEntries--;
        }
    }

    /**
     * Returns a String representation of the HashMap
     *
     * @return
     *      A String representation of the HashMap
     */
    @Override
    public String toString() {
        String stringRepresenation = "";

        for(int i = 0; i < this.size; i++) {
            if((this.entries[i] != null) && (this.entries[i] != AVAILABLE_POSITION)) {
                stringRepresenation += "(" + this.entries[i].getKey().toString() + ", " +
                        this.entries[i].getValue().toString() + ")";
            }
        }

        return stringRepresenation;
    }
}