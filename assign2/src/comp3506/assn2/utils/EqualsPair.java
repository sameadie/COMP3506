package comp3506.assn2.utils;

public class EqualsPair<L, R> extends Pair<L, R> {

    public EqualsPair(L leftVal, R rightVal) {
        super(leftVal, rightVal);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof EqualsPair)) {
            return false;
        } else {
            EqualsPair other = (EqualsPair) o;
            return (this.getLeftValue() == other.getLeftValue()) &&
                    (this.getRightValue() == other.getRightValue());
        }
    }


}
