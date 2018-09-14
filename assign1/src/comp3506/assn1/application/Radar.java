package comp3506.assn1.application;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Very simplistic source that generates aircraft for the OneSky simulation.
 * 
 * @author Richard Thomas <richard.thomas@uq.edu.au>
 *
 */
public class Radar {
	
	private AirTrafficTracker tracker;
	
	// aircraftIdTag and id are used to generate unique identifiers for each aircraft.
	private String aircraftIdTag;
	private int id;

	public Radar(AirTrafficTracker tracker, String aircraftIdTag) {
		this.tracker = tracker;
		this.aircraftIdTag = aircraftIdTag;
		id = 10;
	}
	
	/**
	 * For the purposes of the OneSky simulation, this generates a new aircraft object to add to the air space.
	 */
	public void aircraftIdentified() {
		final int MIN_AIRSPEED = 220;		// metres per second
		final int MAX_AIRSPEED = 275;		// metres per second
		final double NORTH = 0;				// degrees
		final double HEADING_RANGE = 360;	// degrees
		final int METRES_PER_KILOMETRE = 1000;
		
		tracker.newAircraft(new Aircraft(aircraftIdTag+id++, 
										 ThreadLocalRandom.current().nextInt(0, tracker.getLength()), 
										 ThreadLocalRandom.current().nextInt(0, tracker.getBreadth()), 
										 ThreadLocalRandom.current().nextInt(0, tracker.getHeight()*METRES_PER_KILOMETRE), 
										 ThreadLocalRandom.current().nextInt(MIN_AIRSPEED, MAX_AIRSPEED), 
										 ThreadLocalRandom.current().nextDouble(NORTH, HEADING_RANGE)));
	}

}