package comp3506.assn2.utils;

import java.lang.reflect.Array;

public class ArrayList<V> {

    private final static int DEFAULT_START_SIZE = 4;
    private final static int RESIZING_FACTOR = 2;

    private V[] array;
    private int numElements;
    private int size;

    public ArrayList(int initialSize) {
        this.numElements = 0;
        this.size = initialSize;
        this.array = (V[])new Object[this.size];
    }

    public ArrayList() {
        this(DEFAULT_START_SIZE);
    }

    public int size() { return this.numElements;}

    public V get(int i) {
        return array[i];
    }

    public void append(V element) {

        //Allocate more memory if required
        if(this.numElements >= this.size) {
            increaseSize(RESIZING_FACTOR);
        }

        this.array[this.numElements++] = element;
    }

    public void extend(ArrayList<V> other) {
        for(int i = 0; i < other.size(); i++) {
            this.append(other.get(i));
        }

        this.numElements += other.size();
    }

    public void add(int index, V element) {
        //Allocate more memory if required
        if(this.numElements >= this.size) {
            increaseSize(RESIZING_FACTOR);
        }

        //Create space for element, move elements along one
        for(int i = this.numElements; i > index; i--) {
            this.array[i] = this.array[i - 1];
        }

        //Insert element
        this.array[index] = element;
        this.numElements++;
    }

    public void remove(int index) {
        //Shift elements in front of index back one
        for(int i = index; i < this.numElements; i++) {
            this.array[i] = this.array[i + 1];
        }

        this.numElements--;
    }

    public boolean remove(V value) {
        for(int i = 0; i < this.array.length; i++) {
            if(this.array[i].equals(value)) {
                this.remove(i);
                this.numElements--;
                return true;
            }
        }
        return false;
    }

    public boolean contains(V element) {
        for(int i = 0; i < this.numElements; i++) {
            if(this.array[i].equals(element)) {
                return true;
            }
        }

        return false;
    }

    private void increaseSize(int factor) {
        this.size *= factor;
        V[] newArray = (V[])new Object[this.size];;
        java.lang.System.arraycopy(this.array, 0, newArray, 0, this.numElements);
        this.array = newArray;
    }
}
