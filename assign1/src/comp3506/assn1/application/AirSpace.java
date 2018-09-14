package comp3506.assn1.application;

import comp3506.assn1.adts.Cube;
import comp3506.assn1.adts.BoundedCube;


/**
 * Very simple manager for the data structure holding the airspace model.
 * 
 * @author Richard Thomas <richard.thomas@uq.edu.au>
 *
 */
public class AirSpace {
	
	private Cube<Aircraft> airSpaceMap;
	
	// Dimensions of the air space in kilometres.
	private int length;
	private int breadth;
	private int height;
	
	public AirSpace(int length, int breadth, int height) {
		this.length = length;
		this.breadth = breadth;
		this.height = height;
		airSpaceMap = new BoundedCube<>(length, breadth, height);
	}
	
	/**
	 * @return The length of the airspace.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return The breadth of the airspace.
	 */
	public int getBreadth() {
		return breadth;
	}

	/**
	 * @return The height of the airspace.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Add a new aircraft to the airspace model.
	 * 
	 * @param aircraft to be added to the model.
	 * @return true if there are multiple aircraft in a single airspace cell, false otherwise.
	 */
	public boolean addAircraft(Aircraft aircraft) {
		final int METRES_PER_KILOMETRE = 1000;
		
		airSpaceMap.add(aircraft.getAirSpaceXCoord(), aircraft.getAirSpaceYCoord(), 
				        aircraft.getAltitude()/METRES_PER_KILOMETRE, aircraft);
		return airSpaceMap.isMultipleElementsAt(aircraft.getAirSpaceXCoord(), aircraft.getAirSpaceYCoord(), 
				                                aircraft.getAltitude()/METRES_PER_KILOMETRE);
	}
	
}
