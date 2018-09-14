package comp3506.assn1.application;

import java.util.Iterator;

import comp3506.assn1.adts.IterableQueue;
import comp3506.assn1.adts.TraversableQueue;


/**
 * Very simple manager that coordinates multiple radar sites feeding data to the airspace.
 * Aircraft are queued as they are identified by a radar site, to be processed by the airspace.
 * 
 * @author Richard Thomas <richard.thomas@uq.edu.au>
 *
 */
public class AirTrafficTracker {
	
	private IterableQueue<Aircraft> radarQueue;
	private AirSpace airSpace;
	
	public AirTrafficTracker(AirSpace airSpace) {
		this.airSpace = airSpace;
		radarQueue = new TraversableQueue<Aircraft>();
	}
	
	/**
	 * Add a new aircraft to the queue of aircraft identified by a radar site.
	 * @param aircraft to add to the queue
	 */
	public void newAircraft(Aircraft aircraft) {
		radarQueue.enqueue(aircraft);
	}
	
	/**
	 * Take the next aircraft in the queue and add it to the airspace.
	 * Outputs a warning message if the aircraft encroaches on the space of another aircraft.
	 * Should really return an error value to be dealt with at the UI layer.
	 */
	public void processRadarQueue() {
		if (radarQueue.size() != 0) {
			Aircraft aircraft = radarQueue.dequeue();
			if (airSpace.addAircraft(aircraft)) {
				System.out.println(aircraft + " has entered an occupied cell in the air space!");
			}
		}
	}
	
	/**
	 * 
	 * @return An iterator that provides access to all aircraft in the queue.
	 */
	public Iterator<Aircraft> radarQueueIterator() {
		return radarQueue.iterator();
	}
	
	/**
	 * @return The air space's length.
	 */
	public int getLength() {
		return airSpace.getLength();
	}

	/**
	 * @return The air space's breadth.
	 */
	public int getBreadth() {
		return airSpace.getBreadth();
	}

	/**
	 * @return The air space's height.
	 */
	public int getHeight() {
		return airSpace.getHeight();
	}
	
}
