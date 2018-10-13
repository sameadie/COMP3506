package comp3506.assn2.utils;

import java.util.Arrays;
import java.util.Comparator;

/**
 * A generic ArrayList implementation
 *
 * @bigO
 *      O(n) memory efficiency: each element stored once with a few
 *      constant values to keep track of number of elements and size
 *
 * @param <E>
 *     The type of the elements stored in the ArrayList
 */
public class ArrayList <E> {

    private final static int DEFAULT_START_SIZE = 4;
    private final static int RESIZING_FACTOR = 2;

    private E[] array;
    private int numElements;
    private int size;

    /**
     * Initialises an ArrayList with specific initial size
     *
     * @bigO
     *      O(1): Initialisation runs in O(1)
     *
     * @param initialSize
     *      The initial capacity of the ArrayList
     */
    public ArrayList(int initialSize) {
        this.numElements = 0;
        this.size = initialSize;
        this.array = (E[])new Object[this.size];
    }

    /**
     * Initialises an ArrayList with default initial capacity
     *
     * @bigO
     *      O(1): calls constructor which runs in O(1)
     */
    public ArrayList() {
        this(DEFAULT_START_SIZE);
    }

    /**
     * Returns the number of elements in the array
     *
     * @bigO
     *      O(1): calls O(1) function
     * @return
     *      The number of elements in the array
     */
    public int size() { return this.numElements;}

    /**
     * Returns the element at the specified position in the ArrayList
     *
     * @bigO
     *      O(1): array provides constant time access
     *
     * @param i
     *      The index of the element to get from the list
     *
     * @return
     *      The element at the specified position in the list
     */
    public E get(int i) {
        return array[i];
    }

    /**
     * Sets the value of the element at the specified position. If no element exists at the
     * specified position, the ArrayList is unchanged
     *
     * @bigO
     *      O(1): array provides constant time set
     *
     * @param index
     *      The index in the list to update
     * @param element
     *      The new value to update the list with
     */
    public void set(int index, E element) {
        if(index < this.size()) {
            this.array[index] = element;
        }
    }

    /**
     * Appends the specified element to the end of the list
     *
     * @bigO
     *      O(1): constant time set with amortised O(1) time resizing 
     * 
     * @param element
     *      The element to append to the list
     */
    public void append(E element) {
        //Allocate more memory if required
        if(this.numElements >= this.size) {
            increaseSize(RESIZING_FACTOR);
        }

        this.array[this.numElements++] = element;
    }

    /**
     * Appends all elements in other to this list
     *
     * @bigO
     *      O(N): performs O(1) function for every element in other
     * @param other
     *      An ArrayList of elements to extend this list with
     */
    public void extend(ArrayList<E> other) {
        for(int i = 0; i < other.size(); i++) {
            this.append(other.get(i));
        }
    }

    /**
     * Inserts the element into the specified index within
     * the list, shuffling all elements after index back one position.
     * If index is greater than the number of elements in the list,
     * the element is appended to the list
     *
     * @bigO
     *      O(N): must shift N elements to make room for insertion
     *  
     * @param index
     *      The index to insert the element at
     * @param element
     *      The element to insert
     */
    public void add(int index, E element) {
        //Allocate more memory if required
        if(this.numElements >= this.size) {
            increaseSize(RESIZING_FACTOR);
        }

        //Elements in list must be continuous
        if(index >= this.numElements) {
            this.append(element);
            return;
        }

        //Create space for element, move elements along one
        for(int i = this.numElements; i > index; i--) {
            this.array[i] = this.array[i - 1];
        }

        //Insert element
        this.array[index] = element;
        this.numElements++;
    }

    /**
     * Removes and returns the element at the specified position, else
     * returns null if index out of range
     *
     * @bigO
     *      O(N): must shift elements back to fill gap 
     * 
     * @param index
     *      The index of the element to remove from the list
     *
     * @return
     *      The element removed, else null
     */
    public E remove(int index) {
        if(index >= this.numElements) {
            return null;
        }

        E removedElement = this.array[index];

        //Shift elements in front of index back one
        for(int i = index; i < this.numElements - 1; i++) {
            this.array[i] = this.array[i + 1];
        }

        this.numElements--;

        return removedElement;
    }

    /**
     * Attempts to remove the specified element from the list.
     * Returns true iff successful, else false
     *
     * @bigO
     *      O(N): iterates through list checking each element, may perform O(N) function once
     *      *assuming constant time comparison for stored data type
     * 
     * @param element
     *      The element to remove from the lust
     *
     * @return
     *      True iff successful, else false
     */
    public boolean remove(E element) {
        for(int i = 0; i < this.array.length; i++) {
            if(this.array[i].equals(element)) {
                this.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true iff the list contains the specified element, else false
     *
     * @bigO
     *      O(N): checks each element in list
     *      **assuming constant time comparison for stored data type
     * 
     * @param element
     *      The queried element
     * @return
     *      True iff the list contains the specified element, else false
     */
    public boolean contains(E element) {
        for(int i = 0; i < this.numElements; i++) {
            if(this.array[i].equals(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an array containing the elements in this list
     *
     * @bigO
     *      O(1): keeps reference to array 
     *      
     * @return
     *      An array of the elements in the list
     */
    public E[] toArray() {
        return this.array;
    }

    /**
     * Increases the capacity of the list by the specified factor
     *
     * @param factor
     *      The specified increase in capacity
     */
    private void increaseSize(int factor) {
        this.size *= factor;
        E[] newArray = (E[])new Object[this.size];;
        java.lang.System.arraycopy(this.array, 0, newArray, 0, this.size());
        this.array = newArray;
    }

    /**
     * Returns a java.util.ArrayList containing the elements in this list.
     *
     * @bigO
     *      O(N): adds all N elements to a java.util.ArrayList
     *
     * @return
     *      A java.util.ArrayList containing all elements in this list
     */
    public java.util.ArrayList<E> toJavaArrayList() {
        java.util.ArrayList<E> javaArrayList = new java.util.ArrayList<>(this.numElements);
        for(int i = 0; i < this.numElements; i++) {
            javaArrayList.add(this.array[i]);
        }

        return javaArrayList;
    }

    /**
     * Returns the index of the specified value, else returns -1.
     * This method assumes the array is sorted
     *
     * @bigO
     *      O(log(N)): search area is halved each iteration
     *      **assuming constant time comparison
     *
     * @param comparator
     *      The comparator used to compare values in the list
     * @param value
     *      The value to search for in the list
     * @return
     *      The index of the specified value, else -1 if value is not in the list
     */
    public int binarySearch(Comparator<E> comparator, E value) {
        int leftPointer = 0;
        int rightPointer = this.numElements - 1;

        while(leftPointer <= rightPointer) {
            int middlePointer = (leftPointer + rightPointer + 1) / 2;
            int comparison = comparator.compare(this.array[middlePointer], value);

            //Search left half
            if(comparison > 0) {
                rightPointer = middlePointer - 1;
            //Search right half
            } else if(comparison < 0) {
                leftPointer = middlePointer + 1;
            //Found match
            } else {
                return middlePointer;
            }
        }

        return -1;
    }

    /**
     * Returns a string representation of the list
     *
     * @bigO
     *      O(N): must access all elements in array
     *
     * @return
     *      A string representation of the list
     */
    public String toString() {
        return Arrays.toString(this.array);
    }

}
