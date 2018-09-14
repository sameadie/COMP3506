package comp3506.assn1.adts;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;


public class BoundedCubeTest {

    private Object[] getTestObjects(int number) {
        Object[] testObjects = new Object[number];

        for(int i = 0; i < number; i++) {
            testObjects[i] = new Object();
        }

        return testObjects;
    }


    @Test(timeout=500)
	public void testGetWithOneElement() {
		Cube<Object> testCube = new BoundedCube<>(5, 5, 5);
		Object element = new Object();
		testCube.add(1, 1, 1, element);
		assertThat("Only element at a position was not returned.", testCube.get(1, 1, 1), is(equalTo(element)));
	}

	@Test(timeout=500)
	public void testGetWithMultipleElements() {
		Cube<Object> testCube = new BoundedCube<>(5, 5, 5);
		Object element1 = new Object();
		Object element2 = new Object();
		testCube.add(1, 1, 1, element1);
		testCube.add(1, 1, 1, element2);
		assertThat("First element added at a position was not returned.", testCube.get(1, 1, 1), is(equalTo(element1)));
	}

	@Test(timeout=500)
	public void testGetAllWithMultipleElementsSize() {
		Cube<Object> testCube = new BoundedCube<>(5, 5, 5);
		Object element1 = new Object();
		Object element2 = new Object();
		testCube.add(1, 1, 1, element1);
		testCube.add(1, 1, 1, element2);
		IterableQueue<Object> queue = testCube.getAll(1, 1, 1);
		assertThat("Returned queue was wrong size.", queue.size(), is(equalTo(2)));
	}

	@Test(timeout=500)
	public void testIsMultipleElementsAtWithOneElement() {
		Cube<Object> testCube = new BoundedCube<>(5, 5, 5);
		testCube.add(1, 1, 1, new Object());
		assertThat("One element at a position indicates it is multiple.", 
				   testCube.isMultipleElementsAt(1, 1, 1), is(equalTo(false)));
	}

	@Test(timeout=500)
	public void testClearCube() {
		Cube<Object> testCube = new BoundedCube<>(3, 3, 3);
		testCube.add(0, 0, 0, new Object());
		testCube.add(1, 1, 1, new Object());
		testCube.add(1, 1, 1, new Object());
		testCube.add(2, 2, 2, new Object());
		testCube.clear();
		for (int i=0; i<3; i++)
			for (int j=0; j<3; j++)
				for (int k=0; k<3; k++)
					assertThat("", testCube.get(i, j, k), is(equalTo(null)));
	}

	/**
	 * Additional JUnit tests for the BoundedCube
     * @author Sam Eadie
	 */

	@Test(timeout=500, expected = IllegalArgumentException.class)
    public void invalidConstructorArguments() {
	    BoundedCube<Object> testCube = new BoundedCube<>(5, 5, -5);
    }

    @Test(timeout=500, expected = IndexOutOfBoundsException.class)
    public void invalidAddPosition() {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
        testCube.add(2, 4, 6, new Object());
    }

    @Test(timeout=500, expected = IndexOutOfBoundsException.class)
    public void negativeAddPosition() {
	    BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
	    testCube.add(1,2, -1, new Object());
    }

    @Test(timeout=500, expected = IndexOutOfBoundsException.class)
    public void invalidGetPosition() {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
        Object testObject = testCube.get(3, 6, 9);
    }

    @Test(timeout=500, expected = IndexOutOfBoundsException.class)
    public void negativeGetPosition() {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
        Object testObject = testCube.get(-3, 3, 3);
    }

    @Test(timeout=500, expected = IndexOutOfBoundsException.class)
    public void invalidIteratorPosition() {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
        IterableQueue<Object> testIterator = testCube.getAll(3, 6, 9);
    }

    @Test(timeout=500, expected = IndexOutOfBoundsException.class)
    public void negativeIteratorPosition() {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
        IterableQueue<Object> testIterator = testCube.getAll(3, 3, -3);
    }

    @Test(timeout=500)
    public void removeSingleElementFromMany() {
	    BoundedCube<Object> testCube = new BoundedCube<>(5, 5,5);
	    Object[] testObjects = getTestObjects(5);

	    for(int i = 0; i < testObjects.length; i++) {
            testCube.add(1, 2, 3, testObjects[i]);
        }

        boolean successfulRemove = testCube.remove(1, 2, 3, testObjects[3]);

        Assert.assertTrue(successfulRemove);
        Assert.assertEquals(testCube.getAll(1, 2, 3).size(), testObjects.length - 1);

        for(Object object : testCube.getAll(1, 2, 3)) {
            Assert.assertNotEquals(object, testObjects[3]);
        }
    }

    @Test(timeout=500)
    public void removeSingleElementFromSingle() {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5,5);
        Object testObject = new Object();

        testCube.add(1, 2, 3, testObject);

        boolean successfulRemove = testCube.remove(1, 2, 3, testObject);

        Assert.assertTrue(successfulRemove);
        Assert.assertEquals(testCube.getAll(1, 2, 3).size(), 0);
        Assert.assertFalse(testCube.getAll(1, 2, 3).iterator().hasNext());
    }

    @Test(timeout=500)
    public void removeManyElementsFromMany() {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5,5);
        Object[] testObjects = getTestObjects(5);

        for(int i = 0; i < testObjects.length; i++) {
            testCube.add(3, 2, 1, testObjects[i]);
        }

        for(int i = 1; i < 5; i++) {

            boolean successfulRemove = testCube.remove(3, 2, 1, testObjects[i]);

            Assert.assertTrue(successfulRemove);
            Assert.assertEquals(testCube.getAll(3, 2, 1).size(), testObjects.length - i);

            for (Object object : testCube.getAll(3, 2, 1)) {
                Assert.assertNotEquals(object, testObjects[i]);
            }
        }
    }

    @Test(timeout=500)
    public void removeManyElementFromSingle() {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5,5);
        Object testObject = new Object();
        Object unaddedObject = new Object();

        testCube.add(3, 2, 1, testObject);

        Assert.assertTrue(testCube.remove(3,2,1, testObject));
        Assert.assertEquals(testCube.getAll(3, 2, 1).size(), 0);

        Assert.assertFalse(testCube.remove(3, 2, 1, unaddedObject));
        Assert.assertEquals(testCube.getAll(3, 2, 1).size(), 0);

    }

    @Test(timeout =500, expected = IndexOutOfBoundsException.class)
    public void removeElementOutsideCube() {
	    BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
	    Object testObject = new Object();

	    testCube.remove(2, 4, 5, testObject);
    }

    @Test(timeout=500, expected = IndexOutOfBoundsException.class)
    public void removeElementFromNegativeIndex() {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
        Object testObject = new Object();

        testCube.remove(1, 2, -3, testObject);
    }

    @Test(timeout=500)
    public void removeAllTypical() {
	    BoundedCube<Object> testCube = new BoundedCube<>(5, 5,5);
	    Object[] testObjects = getTestObjects(5);

	    for(int i = 0; i < 5; i++) {
	        testCube.add(1, 2, 3, testObjects[i]);
        }

        testCube.removeAll(1, 2, 3);
	    Assert.assertEquals(testCube.getAll(1, 2, 3).size(), 0);
    }


    @Test(timeout=500)
    public void removeAllFromEmpty() {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5,5);

        testCube.removeAll(1, 2, 3);
        Assert.assertEquals(testCube.getAll(1, 2, 3).size(), 0);
    }

    @Test(timeout=500, expected = IndexOutOfBoundsException.class)
    public void removeAllFromOutsideCube() {
	    BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
	    testCube.removeAll(6, 6, 6);
    }

    @Test(timeout=500, expected = IndexOutOfBoundsException.class)
    public void removeAllFromNegativePosition() {
	    BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
	    testCube.removeAll(-1, -1, -1);
    }

    @Test(timeout = 500)
    public void clearEmptyCube () {
	    BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
	    testCube.clear();

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                for(int k = 0; k < 5; k++) {
                    if(testCube.getAll(i, j, k) != null) {
                        Assert.assertEquals(testCube.getAll(i, j, k).size(), 0);
                    }
                }
            }
        }
    }

    @Test(timeout = 500)
    public void clearCubeWithSinglePositionFilled () {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
        Object[] testObjects = getTestObjects(5);
        for(int i = 0; i < 5; i++) {
            testCube.add(1, 2, 3, testObjects[i]);
        }

        testCube.clear();

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                for(int k = 0; k < 5; k++) {
                    if(testCube.getAll(i, j, k) != null) {
                        Assert.assertEquals(testCube.getAll(i, j, k).size(), 0);
                    }
                }
            }
        }
    }

    @Test(timeout = 500)
    public void clearCubeWithMultiplePositionsFilled () {
        BoundedCube<Object> testCube = new BoundedCube<>(5, 5, 5);
        Object[] testObjects = getTestObjects(25);

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                testCube.add(i, i, i, testObjects[(5 * i) + j]);
            }
        }

        testCube.clear();
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                for(int k = 0; k < 5; k++) {
                    if(testCube.getAll(i, j, k) != null) {
                        Assert.assertEquals(testCube.getAll(i, j, k).size(), 0);
                    }
                }
            }
        }
    }
}

