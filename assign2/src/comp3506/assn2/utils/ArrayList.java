package comp3506.assn2.utils;

import java.util.Arrays;

public class ArrayList <E> {

    private final static int DEFAULT_START_SIZE = 4;
    private final static int RESIZING_FACTOR = 2;

    private E[] array;
    private int numElements;
    private int size;

    public ArrayList(int initialSize) {
        this.numElements = 0;
        this.size = initialSize;
        this.array = (E[])new Object[this.size];
    }

    public ArrayList() {
        this(DEFAULT_START_SIZE);
    }

    public int size() { return this.numElements;}

    public E get(int i) {
        return array[i];
    }

    public void set(int index, E element) {
        if(index < this.array.length) {
            this.array[index] = element;
        }
    }

    public void append(E element) {
        //Allocate more memory if required
        if(this.numElements >= this.size) {
            increaseSize(RESIZING_FACTOR);
        }

        this.array[this.numElements++] = element;
    }

    public void extend(ArrayList<E> other) {
        for(int i = 0; i < other.size(); i++) {
            this.append(other.get(i));
        }
    }

    public void add(int index, E element) {
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

    public E remove(int index) {
        E removedElement = this.array[index];

        //Shift elements in front of index back one
        for(int i = index; i < this.numElements; i++) {
            this.array[i] = this.array[i + 1];
        }

        this.numElements--;

        return removedElement;
    }

    public boolean remove(Object value) {
        for(int i = 0; i < this.array.length; i++) {
            if(this.array[i].equals(value)) {
                this.remove(i);
                this.numElements--;
                return true;
            }
        }
        return false;
    }

    public boolean contains(Object o) {
        for(int i = 0; i < this.numElements; i++) {
            if(this.array[i].equals(o)) {
                return true;
            }
        }
        return false;
    }

    public E[] toArray() {
        return this.array;
    }

    private void increaseSize(int factor) {
        this.size *= factor;
        E[] newArray = (E[])new Object[this.size];;
        java.lang.System.arraycopy(this.array, 0, newArray, 0, this.numElements);
        this.array = newArray;
    }

    public java.util.ArrayList<E> toJavaArrayList() {
        java.util.ArrayList<E> javaArrayList = new java.util.ArrayList<>(this.numElements);
        for(int i = 0; i < this.numElements; i++) {
            javaArrayList.add(this.array[i]);
        }

        return javaArrayList;
    }

    public String toString() {
        return Arrays.toString(this.array);
    }

}
