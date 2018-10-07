package comp3506.assn2.utils;

public class HashPair<L, R> extends Pair<L, R> {

    public HashPair(L leftValue, R rightValue) {
        super(leftValue, rightValue);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", this.getLeftValue().toString(), this.getRightValue().toString());
    }

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

    @Override
    public int hashCode() {
        return (this.getLeftValue().hashCode() * 7) + this.getRightValue().hashCode();
    }
}
