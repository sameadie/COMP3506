package comp3506.assn1.application;


/**
 * Simple representation of an aircraft in the OneSky simulation.
 * 
 * @author Richard Thomas <richard.thomas@uq.edu.au>
 *
 */
public class Aircraft {
	
	private String id;		// Aircraft's unique registration identifier.
	private int airSpaceXCoord;
	private int airSpaceYCoord;
	private int altitude;	// In metres.
	private int speed;		// In metres per second.
	private double course;	// In degrees, north is 0 degrees.
	
	public Aircraft(String id, int xCoord, int yCoord, int altitude, int speed, double course) {
		this.id = id;
		this.airSpaceXCoord = xCoord;
		this.airSpaceYCoord = yCoord;
		this.altitude = altitude;
		this.speed = speed;
		this.course = course;
	}
	
	/**
	 * @return the aircraft's id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the aircraft's airSpaceXCoord
	 */
	public int getAirSpaceXCoord() {
		return airSpaceXCoord;
	}

	/**
	 * @return the aircraft's airSpaceYCoord
	 */
	public int getAirSpaceYCoord() {
		return airSpaceYCoord;
	}

	/**
	 * @return the aircraft's altitude
	 */
	public int getAltitude() {
		return altitude;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		try {
			return id.equalsIgnoreCase(((Aircraft)obj).id);
		} catch (ClassCastException cce) {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Aircraft: " + id + " at " + altitude + " metres, on course: " + course
               + " degrees, at " + speed + " metres per second";
	}
	
}
