package comp3506.assn2.utils;

public class HashMap<K, V> {
    public class Entry {
        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

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

    public HashMap(int initialSize) {
        this.size = initialSize;
        entries = (Entry[])new Object[initialSize];
        AVAILABLE_POSITION = new Entry(null, null);
    }

    public HashMap() {
        this(DEFAULT_INITIAL_SIZE);
    }

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

    public V get(K key) {
        int i = findKeyEntry(key);
        if (i < 0) {
            return null;
        } else {
            return this.entries[i].getValue();
        }
    }

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

    public void remove(K key) {
        int i = findKeyEntry(key);

        if(i >= 0) {
            this.entries[i] = AVAILABLE_POSITION;
            this.numEntries--;
        }
    }

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
