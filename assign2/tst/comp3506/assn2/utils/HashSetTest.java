package comp3506.assn2.utils;

import org.junit.Assert;
import org.junit.Test;

public class HashSetTest {

    private Object[] getTestObjects(int numObjects) {
        Object[] testObjects = new Object[numObjects];

        for(int i = 0; i < numObjects; i++) {
            testObjects[i] = new Object();
        }

        return testObjects;
    }

    @Test
    public void put() throws Exception {
        HashSet testHashSet = new HashSet();

        Object[] testObjects = getTestObjects(5);

        for(int i = 0; i < testObjects.length; i++) {
            testHashSet.put(testObjects[i]);
            Assert.assertEquals(i + 1, testHashSet.getSize());
        }
    }

    @Test
    public void putDuplicates() throws Exception {
        HashSet testHashSet = new HashSet();

        Object[] testObjects = getTestObjects(5);

        for(int i = 0; i < testObjects.length; i++) {
            testHashSet.put(testObjects[i]);
        }

        //Add duplicates
        for(int i = 0; i < testObjects.length; i++) {
            testHashSet.put(testObjects[i]);

            Assert.assertEquals(testObjects.length, testHashSet.getSize());
        }
    }


    @Test
    public void contains() throws Exception {
        HashSet testHashSet = new HashSet();

        Object[] testObjects = getTestObjects(5);

        for(int i = 0; i < testObjects.length; i++) {
            testHashSet.put(testObjects[i]);

            Assert.assertTrue(testHashSet.contains(testObjects[i]));

            for(int j = i + 1; j < testObjects.length; j++) {
                Assert.assertFalse(testHashSet.contains(testObjects[j]));
            }
        }
    }

    @Test
    public void remove() throws Exception {
        HashSet testHashSet = new HashSet();

        Object[] testObjects = getTestObjects(5);

        for(int i = 0; i < testObjects.length; i++) {
            testHashSet.put(testObjects[i]);
        }

        for(int i = 0; i < testObjects.length; i++) {
            testHashSet.remove(testObjects[i]);

            Assert.assertEquals(testObjects.length - i - 1, testHashSet.getSize());
            Assert.assertFalse(testHashSet.contains(testObjects[i]));
        }
    }

}