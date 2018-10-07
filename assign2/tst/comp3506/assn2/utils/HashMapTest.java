package comp3506.assn2.utils;

import org.junit.Assert;
import org.junit.Test;

public class HashMapTest {

    @Test
    public void getSize() throws Exception {
        HashMap<HashPair<Integer, Integer>, Integer> integerHashMap = new HashMap<>();
        integerHashMap.put(new HashPair(1, 2), 6);
        Assert.assertTrue(integerHashMap.get(new HashPair(1, 2)).equals(6));
        integerHashMap.put(new HashPair(1, 2), 7);
        Assert.assertTrue(integerHashMap.get(new HashPair(1, 2)).equals(7));

        String string = "this is a string";
        String string2 = "this is a different string";

        HashMap<Integer, String> linesHashMap = new HashMap<>();
        linesHashMap.put(3, string);
        Assert.assertTrue(linesHashMap.get(3).equals("this is a string"));
        linesHashMap.put(3, "this is a different string");
        Assert.assertTrue(linesHashMap.get(3).equals(string2));

        HashMap<Integer, Integer> intHashMap = new HashMap<>();
        intHashMap.put(3, 7);
        Assert.assertTrue(intHashMap.get(3).equals(7));
        intHashMap.put(3, 6);
        Assert.assertTrue(intHashMap.get(3).equals(6));

        HashMap<String, HashPair<Integer, Integer>> sectionIndexes = new HashMap<>();
        sectionIndexes.put(string, new HashPair<>(1,2));
        Assert.assertTrue(sectionIndexes.get(string).equals(new HashPair<>(1, 2)));
        sectionIndexes.put("this is a string", new HashPair<>(4, 6));
        Assert.assertTrue(sectionIndexes.get(string).equals(new HashPair<>(4, 6)));
    }

    @Test
    public void get() throws Exception {
    }

    @Test
    public void put() throws Exception {
    }

    @Test
    public void remove() throws Exception {
    }

}