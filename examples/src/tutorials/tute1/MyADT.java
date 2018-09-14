package tutorials.tute1;

public interface MyADT<T> {
    /**
     * Returns the length of the parameter
     * @param param
     *          A generic parameter
     * @return the length of the parameter
     */
    int myMethod(T param);
}
