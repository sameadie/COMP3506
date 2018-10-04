package comp3506.assn2.utils;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArrayListTest {

    private java.util.ArrayList<Object> comparisonArrayList;
    private ArrayList<Object> testArrayList;

    @BeforeEach
    void setUp() {
        testArrayList = new ArrayList<>();
        comparisonArrayList = new java.util.ArrayList<>();
    }

    @AfterEach
    void tearDown() {
    }

    private Object[] getTestObjects(int number) {
        Object[] testObjects = new Object[number];

        for(int i = 0; i < number; i++) {
            testObjects[i] = new Object();
        }

        return testObjects;
    }

    private boolean compareArrayLists(ArrayList<Object> test, java.util.ArrayList<Object> comparison) {

        if(test.size() != comparison.size()) {
            return false;
        }

        for(int i = 0; i < test.size(); i++) {
            if(!test.get(i).equals(comparison.get(i))) {
                return false;
            }
        }

        return true;

    }

    @Test
    public void testAppend() {
        int numObjects = 5;
        Object[] testObjects = getTestObjects(numObjects);

        for(int i = 0; i < numObjects; i++) {
            comparisonArrayList.add(testObjects[i]);
            testArrayList.append(testObjects[i]);

            Assert.assertTrue(compareArrayLists(testArrayList, comparisonArrayList));
        }
    }

    @Test
    public void testRemoveIndex() {
        testAppend();

        int[] removeOrder = new int[]{0, 2, 2, 0, 0};
        for(int i = 0; i < testArrayList.size(); i++) {
            comparisonArrayList.remove(removeOrder[i]);
            testArrayList.remove(removeOrder[i]);

            Assert.assertEquals(testArrayList.size(), comparisonArrayList.size());
            Assert.assertTrue(compareArrayLists(testArrayList, comparisonArrayList));
        }
    }

    @Test
    public void testRemoveElement() {
        int numObjects = 5;
        Object[] testObjects = getTestObjects(numObjects);

        for(int i = 0; i < numObjects; i++) {
            comparisonArrayList.add(testObjects[i]);
            testArrayList.append(testObjects[i]);
        }

        int[] removeOrder = new int[]{4, 0, 1, 0};
        for(int i = 0; i < removeOrder.length; i++) {
            Assert.assertEquals(testArrayList.remove(testObjects[i]),
                    comparisonArrayList.remove(testObjects[i]));

            Assert.assertTrue(compareArrayLists(testArrayList, comparisonArrayList));

        }
    }

    @Test
    public void testGet(){
        testAppend();

        for(int i = 0; i < testArrayList.size(); i++) {
            Assert.assertEquals(testArrayList.get(i), comparisonArrayList.get(i));
        }
    }

    @Test
    public void testAdd() {
        int numObjects = 5;
        Object[] testObjects = getTestObjects(numObjects);

        for(int i = 0; i < numObjects; i++) {
            comparisonArrayList.add(testObjects[i]);
            testArrayList.append(testObjects[i]);
        }

        int[] insertPositions = new int[]{5, 5, 0, 3, 2};
        for(int i = 0; i < insertPositions.length; i++) {
            Object newObject = new Object();
            comparisonArrayList.add(insertPositions[i], newObject);
            testArrayList.add(insertPositions[i], newObject);

            Assert.assertTrue(compareArrayLists(testArrayList, comparisonArrayList));
        }

    }

    @Test
    public void testExtend(){
        testAppend();
        ArrayList<Object> extendArrayList = new ArrayList<>();
        java.util.ArrayList<Object> extendComparisonArrayList = new java.util.ArrayList<>();

        for(int i = 0; i < 10; i++) {
            Object newObject = new Object();
            extendArrayList.append(newObject);
            extendComparisonArrayList.add(newObject);
        }

        testArrayList.extend(extendArrayList);
        extendComparisonArrayList.forEach(object -> {
            comparisonArrayList.add(object);
        });

        for(int i = 0; i < testArrayList.size(); i++) {
            Assert.assertTrue(comparisonArrayList.contains(testArrayList.get(i)));
        }

        comparisonArrayList.forEach(object -> {
            Assert.assertTrue(testArrayList.contains(object));
        });
    }

    @Test
    public void testContains(){
        int numObjects = 5;
        Object[] testObjects = getTestObjects(numObjects);

        for(int i = 0; i < numObjects; i++) {
            testArrayList.append(testObjects[i]);
        }

        for(int i = 0; i < testObjects.length; i++) {
            Assert.assertTrue(testArrayList.contains(testObjects[i]));
        }

        for(int i = 0; i < testObjects.length; i++) {
            Assert.assertNotEquals(testArrayList.remove(testObjects[i]),
                    testArrayList.contains(testObjects[i]));
        }
    }

}