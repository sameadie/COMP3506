package comp3506.assn2.utils;

/**
 * A Pair subclass that implements equals and hashCode
 *
 * @bigO
 *      O(L + R) space complexity: size proportional to size of left and right values
 *
 * @param <L>
 *          The type of the left element
 * @param <R>
 *          The type of the right element
 */
public class HashPair<L, R> extends Pair<L, R> {

    /**
     * Initialises a HashPait
     *
     * @bigO
     *      O(1): initilisation is constant time access
     *
     * @param leftValue
     *      The left value in the pair
     * @param rightValue
     *      The right value in the pair
     */
    public HashPair(L leftValue, R rightValue) {
        super(leftValue, rightValue);
    }

    /**
     * Overrides the string method to show the left and right value's strings
     *
     *  @bigO
     *      O(O(L.toString()) + O(r.toString()) + output.length())
     *
     * @return
     *      A string representation of the HashPair class
     */
    @Override
    public String toString() {
        return String.format("(%s, %s)", this.getLeftValue().toString(), this.getRightValue().toString());
    }

    /**
     * Overrides the equals method to compare the left and right values
     *
     * @bigO
     *      O(O(l.equals()) + O(r.equals())): constant time comparisons followed by
     *      calling the left and right values' .equals methods
     *
     * @param o
     *      The other object for comparison
     *
     * @return
     *      Iff self.leftValue.equals(o.leftValue) && self.rightValue.equals(o.rightValue)
     */
    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(this.getClass() != o.getClass()) {
            return false;
        }

        Pair<L, R> otherPair = (Pair<L, R>) o;

        return otherPair.getLeftValue().equals(this.getLeftValue()) &&
                otherPair.getRightValue().equals(this.getRightValue());
    }

    /**
     * Overrides hashCode method to use the left and right elements' hashcode
     *
     * @bigO
     *      O(O(l.hashCode()) + O(r.hashCode())): calls the hashCode method for the
     *      left and right objects and performs constant time MAC operation
     *
     * @return
     *      The HashPair's hashcode
     */
    @Override
    public int hashCode() {
        return (this.getLeftValue().hashCode() * 7) + this.getRightValue().hashCode();
    }
}
