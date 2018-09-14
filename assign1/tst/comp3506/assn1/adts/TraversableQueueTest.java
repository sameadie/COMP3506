package comp3506.assn1.adts;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class TraversableQueueTest {

    private Object[] getTestObjects(int number) {
        Object[] testObjects = new Object[number];

        for(int i = 0; i < number; i++) {
            testObjects[i] = new Object();
        }

        return testObjects;
    }

	@Test(timeout=500)
	public void testNewQueueIsEmpty() {
		IterableQueue<Object> testQueue = new TraversableQueue<>();
		assertThat("A newly created queue does not have a size of 0.", testQueue.size(), is(equalTo(0)));
	}

	@Test(timeout=500, expected = IndexOutOfBoundsException.class)
	public void testDequeueEmptyQueue() {
		IterableQueue<Object> testQueue = new TraversableQueue<>();
		testQueue.dequeue();	// Nothing to dequeue.
	}

	@Test(timeout=500)
	public void testSingleElementQueueSize() {
		IterableQueue<Object> testQueue = new TraversableQueue<>();
		testQueue.enqueue(new Object());
		assertThat("A queue with one element does not have a size of 1.", testQueue.size(), is(equalTo(1)));

	}

	@Test(timeout=500)
	public void testSingleElementQueue() {
		IterableQueue<Object> testQueue = new TraversableQueue<>();
		Object element = new Object();
		testQueue.enqueue(element);
		assertThat("Enqueing and Dequeing one element does not return that element.", 
				   testQueue.dequeue(), is(equalTo(element)));
	}

	@Test(timeout=500)
	public void testIteratorHasNextOnSingleEntityQueue() {
		IterableQueue<Object> testQueue = new TraversableQueue<>();
		testQueue.enqueue(new Object());
		Iterator<Object> it = testQueue.iterator();
		assertThat("Iterator before first position on a queue of one element does not have a next.", 
				   it.hasNext(), is(equalTo(true)));
		it.next();
		assertThat("Iterator before second position on a queue of one element has a next.", 
				   it.hasNext(), is(equalTo(false)));
	}

    /**
     * Additional JUnit testing for Traversable Queue class
     * @author Sam Eadie
     */

    @Test(timeout=500)
    public void testPopulateConstructor() {
        Object[] testObjects = getTestObjects(10);

        TraversableQueue<Object> testQueue = new TraversableQueue<>(testObjects);

        Assert.assertEquals(testQueue.size(), 10);

        for(int i = 0; i < 10; i++) {
            Assert.assertEquals(testQueue.size(), 10 - i);
            Assert.assertEquals(testQueue.dequeue(), testObjects[i]);
        }
    }
    @Test(timeout=500, expected = IndexOutOfBoundsException.class)
    public void testEmptyPopulateConstructor() {
        TraversableQueue<Object> testQueue = new TraversableQueue<>(getTestObjects(0));
        Assert.assertEquals(testQueue.size(), 0);
        testQueue.dequeue();
    }

    @Test(timeout=500)
    public void testMultipleQueuesDequeues(){
        Object[] testObjects = getTestObjects(5);
        TraversableQueue<Object> testQueue = new TraversableQueue();

        //Repeatedly fill and empty queue
        for(int i = 0; i < 5; i++) {

            //Fill queue
            for(int j = 0; j < 5; j++) {
                testQueue.enqueue(testObjects[i]);
                Assert.assertEquals(testQueue.size(), j + 1);
            }

            //Empty queue
            for(int k = 0; k < 5; k++) {
                Assert.assertEquals(testQueue.size(), 5 - k);
                Assert.assertEquals(testQueue.dequeue(), testObjects[i]);
            }
        }
    }

    @Test(timeout = 500)
    public void testMultipleElementIteratorForEach() {
        Object[] testObjects = getTestObjects(10);
        TraversableQueue<Object> testQueue = new TraversableQueue<>(testObjects);

        Iterator<Object> iterator = testQueue.iterator();

        iterator.forEachRemaining((iteratedObject) -> {
            Assert.assertEquals(iteratedObject, testQueue.dequeue());
        });
    }

    @Test(timeout = 500, expected = NoSuchElementException.class)
    public void testEmptyIterator() {
        TraversableQueue<Object> testQueue = new TraversableQueue<>();
        Iterator<Object> testIterator = testQueue.iterator();

        Assert.assertFalse(testIterator.hasNext());

        testIterator.next();
    }

}
