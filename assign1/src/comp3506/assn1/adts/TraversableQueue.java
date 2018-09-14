package comp3506.assn1.adts;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * A generic-type variable-sized queue which provides an iterator to allow
 * for traversal.
 *
 * @memory O(n) linear space complexity since it stores a constant amount for each node, n, hence
 * growing linearly with added elements.
 *
 * @author Sam Eadie
 * @param <T> stored data type
 */
public class TraversableQueue<T> implements IterableQueue<T> {

    protected class QueueNode<T> {

        private T data;
        private QueueNode<T> next;

        /**
         * Constructor for a node with specified data and next node
         * @bigO O(1) constant time for object assignment
         *
         * @param data The data held in this node
         * @param next The next node in the queue
         */
        public QueueNode(T data, QueueNode<T> next) {
            this.data = data;
            this.next = next;
        }

        /**
         * Constructor for a node with specified data
         * @bigO O(1) constant time for object assignment
         *
         * @param data The data held in this node
         */
        public QueueNode(T data) {
            this(data, null);
        }

    }

    public QueueNode<T> head;
    public QueueNode<T> tail;
    private int size;

    /**
     * Constructs an empty traversable queue
     * @bigO O(1) constant time for object assignment
     */
    public TraversableQueue() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Constructs a traversable queue populated by data. Useful for testing
     * @bigO O(n) linear time, performs enqueue (constant time) n times
     *
     * @param data an array of data to populate the queue with
     */
    public TraversableQueue(T[] data) {
        if(data.length == 0) {
            this.head = null;
            this.tail = null;
            this.size = 0;
        } else {
            for(int i = 0; i < data.length; i++) {
                this.enqueue(data[i]);
            }

            this.size = data.length;
        }
    }

    /**
     * Adds an element to the end of the queue
     * @bigO O(1) constant time since tail reference exists
     *
     * @param element The element to be added to the queue.
     * @throws IllegalStateException
     */
    public void enqueue(T element) throws IllegalStateException {
        QueueNode<T> toAdd = new QueueNode(element);

        if(this.size == 0) {
            this.head = toAdd;
            this.tail = toAdd;
        } else {
            this.tail.next = toAdd;
            this.tail = toAdd;
        }

        this.size++;
    }

    /**
     * Returns and removes the element at the start of the queue
     * @bigO O(1) constant time since head reference exists
     *
     * @return the removed element from the start of the queue
     * @throws IndexOutOfBoundsException
     */
    public T dequeue() throws IndexOutOfBoundsException {
        if(this.size == 0) {
            throw new IndexOutOfBoundsException();
        }

        QueueNode<T> oldHead = this.head;

        this.head = this.head.next;

        this.size--;

        return oldHead.data;
    }

    /**
     * Returns the number of elements in the queue
     * @bigO O(1) size reference exists
     *
     * @return the number of elements in the queue
     */
    public int size() {
        return this.size;
    }

    /**
     * Returns an iterator over the elements in the queue
     * @bigO O(1) returns a new element
     *
     * @return an iterator over the elements in the queue
     */
    public Iterator<T> iterator() {

        /**
         * An anonymous iterator class for the traversable queue
         */
        return new Iterator<T>() {

            private QueueNode<T> reference = new QueueNode<T>(null, head);

            /**
             * Returns true iff the iterator has a next element
             * @bigO O(1) constant time equality check
             *
             * @return true iff the iterator has a next element, else false
             */
            @Override
            public boolean hasNext() {
                return this.reference.next != null;
            }

            /**
             * Returns the next element in the iterator and moves the iterator forward one place
             * @bigO O(1) constant time since reference exists
             *
             * @return the next element in the iterator
             * @throws NoSuchElementException
             */
            @Override
            public T next() throws NoSuchElementException{
                this.reference = this.reference.next;

                if(this.reference == null) {
                    throw new NoSuchElementException();
                } else {
                    return this.reference.data;
                }
            }

            /**
             * Performs the specified action on each remaining element in the queue
             *
             * @param action the consumer to be performed on each element
             */
            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                while(hasNext()) {
                    action.accept(next());
                }
            }
        };
    }
}

/**
 * Justification of design choices for Traversable Queue
 *
 * The TraversableQueue is implemented by a singly linked list with references
 * to the queue's head, tail and size. The head and tail reference allows for
 * constant time enqueueing and dequeueing. A doubly linked list was not implemented
 * since the queue is only ever traversed in one direction. In addition, a singly linked
 * list consumes less overhead memory. The iterator is implemented as an anonymous class
 * for brevity.
 *
 * All class methods run in constant time except for the second constructor. This
 * constructor runs in O(n) since it must enqueue n elements. This provides good
 * performance for the air traffic simulator. In addition, the variable sizing of this queue
 * is advantageous for the air traffic simulator since the BoundedCube will be sparsely populated.
 * This implementation allows for memory to be added as required as opposed to allocating a fixed
 * amount of memory upon the initialisation of each TraversableQueue.
 *
 * Possible additions to this class is a 'peak' method to allow access to the head's data
 * without dequeueing the item.
 *
 *
 *
 */