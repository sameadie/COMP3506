package comp3506.assn1.adts;


/**
 * A three-dimensional data structure that holds items in a positional relationship to each other.
 * Each cell in the data structure can hold multiple items.
 * A bounded cube has a specified maximum size in each dimension.
 * The root of each dimension is indexed from zero.
 *
 * @memory O(lbhn) the BoundedCube consumes length x breadth x height x n memory for storing n elements
 *      in positions in an l x b x h cube.
 * 
 * @author Sam Eadie
 *
 * @param <T> The type of element held in the data structure.
 */
public class BoundedCube<T> implements Cube<T> {

	private TraversableQueue<T>[][][] cube;
	private int length, breadth, height;

	/**
	 * Constructor for a fixed sized cube.
     * @bigO O(1) Array initialisation, argument checking are constant time
     *
	 * @param length  Maximum size in the 'x' dimension.
	 * @param breadth Maximum size in the 'y' dimension.
	 * @param height  Maximum size in the 'z' dimension.
	 * @throws IllegalArgumentException If provided dimension sizes are not positive.
	 */
	public BoundedCube(int length, int breadth, int height) throws IllegalArgumentException {

	    if((length < 0) || (breadth < 0) || (height < 0)) {
	        throw new IllegalArgumentException("Cube size cannot be negative");
        }

        this.length = length;
        this.breadth = breadth;
        this.height = height;

	    cube = new TraversableQueue[length][breadth][height];
	}

    /**
     * Adds an element to the end of the queue at the given position in the cube
     * @bigO O(1) array access and .enqueue() are constant time
     *
     * @param x X Coordinate of the position of the element.
     * @param y Y Coordinate of the position of the element.
     * @param z Z Coordinate of the position of the element.
     * @param element The element to be added at the indicated position.
     * @throws IndexOutOfBoundsException
     */
    public void add(int x, int y, int z, T element) throws IndexOutOfBoundsException {
        try {
            //Only initialise queue for necessary positions
            if(this.cube[x][y][z] == null) {
                this.cube[x][y][z] = new TraversableQueue<>();
            }
            this.cube[x][y][z].enqueue(element);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(
                    "Cannot add to cube outside its dimensions"
            );
        }
    }

    /**
     * Returns the first element at the specified position
     * @bigO O(n) must requeue all elements
     *
     * @param x X Coordinate of the position of the element.
     * @param y Y Coordinate of the position of the element.
     * @param z Z Coordinate of the position of the element.
     * @return the first element at the specified position
     * @throws IndexOutOfBoundsException
     */
    public T get(int x, int y, int z) throws IndexOutOfBoundsException {
        try {
            TraversableQueue<T> positionQueue = this.cube[x][y][z];
            if(positionQueue == null) {
                return null;
            }

            T firstElement = positionQueue.dequeue();
            positionQueue.enqueue(firstElement);

            for(int i = 1; i < positionQueue.size(); i++) {
                positionQueue.enqueue(positionQueue.dequeue());
            }

            return firstElement;

        } catch(ArrayIndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(
                    "Can't get element outside of cube dimensions."
            );
        }
    }

    /**
     * Returns an IterableQueue for all elements at the given position in the cube
     * @bigO O(1) array access occurs in constant time
     *
     * @param x X Coordinate of the position of the element(s).
     * @param y Y Coordinate of the position of the element(s).
     * @param z Z Coordinate of the position of the element(s).
     * @return An IterableQueue for all elements at the given position in the cube
     * @throws IndexOutOfBoundsException
     */
    public IterableQueue<T> getAll(int x, int y, int z) throws IndexOutOfBoundsException {
        try {
            return this.cube[x][y][z];
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(
                    "Cannot getAll from outside cube dimensions."
            );
        }
    }

    /**
     * Returns true iff there are multiple elements at the specified position
     * @bigO O(1) array access, equality check are constant time
     *
     * @param x X Coordinate of the position of the element(s).
     * @param y Y Coordinate of the position of the element(s).
     * @param z Z Coordinate of the position of the element(s).
     * @return true iff there are multiple elements at the specified position, else false
     * @throws IndexOutOfBoundsException
     */
    public boolean isMultipleElementsAt(int x, int y, int z) throws IndexOutOfBoundsException {
        TraversableQueue<T> queue = this.cube[x][y][z];
        if(queue == null) {
            return false;
        }

        return queue.size() > 1;
    }

    /**
     * Removes the specified element from the specified position in the cube
     *@bigO O(n) must iterate through elements in traversable queue at position.
     *      Array access is constant time
     *
     * @param x X Coordinate of the position.
     * @param y Y Coordinate of the position.
     * @param z Z Coordinate of the position.
     * @param element The element to be removed from the indicated position.
     * @return true iff the specified element was removed
     * @throws IndexOutOfBoundsException
     */
    public boolean remove(int x, int y, int z, T element) throws IndexOutOfBoundsException {
        try {
            TraversableQueue<T> cubeQueue = this.cube[x][y][z];

            if(cubeQueue == null) {
                return false;
            }

            TraversableQueue<T> newQueue = new TraversableQueue<>();
            boolean removedFlag = false;

            while (cubeQueue.size() > 0) {
                T value = cubeQueue.dequeue();

                if (value.equals(element)) {
                    removedFlag = true;
                } else {
                    newQueue.enqueue(value);
                }
            }

            this.cube[x][y][z] = newQueue;
            return removedFlag;

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    "Cannot remove element from position outside cube."
            );
        }
    }

    /**
     * Removes all elements from the position in the cube
     * @bigO O(1) consant time array access and assignment
     *
     * @param x X Coordinate of the position.
     * @param y Y Coordinate of the position.
     * @param z Z Coordinate of the position.
     * @throws IndexOutOfBoundsException
     */
    public void removeAll(int x, int y, int z) throws IndexOutOfBoundsException {
        try {
            //Remove references - allows garbage collection
            this.cube[x][y][z] = new TraversableQueue<T>();

        } catch(ArrayIndexOutOfBoundsException c) {
            throw new IndexOutOfBoundsException(
                    "Cannot remove all elements from outside cube dimensions."
            );
        }
    }

    /**
     * Clears the cube, removing all elements from all queues
     * @bigO O(1) reallocates memory, allows garbage collection
     */
    public void clear(){
	    //Remove reference - allows for garbage collection
	    this.cube = new TraversableQueue[this.length][this.breadth][this.height];
    }
}

/**
 * Justification of design choices for BoundedCube
 *
 * TraversableQueues are not initialised at a position unless required since the
 *          majority of positions will be unoccupied. This said, this implementation
 *          has poor memory efficiency since it represents all of the air space. Since
 *          this air space will be sparsely populated, the vast majority will be wasted.
 *          Perhaps a more suitable implementation for this context is a Map. For instance,
 *          a HashMap that associates all occupied (x, y, z) positions with the
 *          corresponding TraversableQueue. An effective HashMap implementation will have
 *          twice the space as the number of used keys. Regardless, twice the 20,000 maximum number
 *          of aircraft is a minute fraction of the total number of positions in a 3D array
 *          implementation of a BoundedCube representing Australia's airspace.
 * BoundedCube.get method provides iterates through queue since TraversableQueue
 *          provides no 'peak' method. This was deemed sufficient since the cube
 *          is sparsely filled and hence there will be few, if any, objects at
 *          each position
 *
 *
 *
 */