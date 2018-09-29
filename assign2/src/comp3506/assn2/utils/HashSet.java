package comp3506.assn2.utils;

public class HashSet<E> {

    private static final Integer VALUE = 1;

    private HashMap<E, Integer> hashMap;

    public HashSet(int initialSize) {
        hashMap = new HashMap<>(initialSize);
    }

    public HashSet() {
        hashMap = new HashMap<>();
    }

    public void put(E element) {
        hashMap.put(element, VALUE);
    }

    public void remove(E element){
        hashMap.remove(element);
    }

    public boolean contains(E element) {
        return hashMap.get(element) == VALUE;
    }
}
