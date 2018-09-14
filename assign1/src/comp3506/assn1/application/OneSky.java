package comp3506.assn1.application;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Very simple simulation of air traffic management in the Australian airspace.
 * Does not attempt to be realistic and all events happen sequentially.
 * 
 * @author Richard Thomas <richard.thomas@uq.edu.au>
 *
 */
public class OneSky {
	
	public static final int AUSTRALIA_WIDTH = 5321;
	public static final int AUSTRALIA_LENGTH = 3428;
	public static final int FLIGHT_CEILING = 35;
	private AirSpace australia = new AirSpace(AUSTRALIA_WIDTH, AUSTRALIA_LENGTH, FLIGHT_CEILING);
	private AirTrafficTracker tracker = new AirTrafficTracker(australia);
	private List<Radar> radarSites = new ArrayList<>();
	public final int NUM_ITERATIONS;
	
	/**
	 * 
	 * @param numIterations Number of iterations executed in automated simulation mode.
	 */
	public OneSky(int numIterations) {
		NUM_ITERATIONS = numIterations;
		radarSites.add(new Radar(tracker, "BNE"));
		radarSites.add(new Radar(tracker, "SYD"));
	}
	
	/**
	 * Check all radar sites to see if they have identified any new aircraft.
	 */
	public void newAircraft() {
		for (Radar radar: radarSites) {
			radar.aircraftIdentified();
		}
	}
	
	/**
	 * Process any aircraft that have been identified but not added to airspace model.
	 */
	public void processAircraft() {
		tracker.processRadarQueue();
	}
	
	/**
	 * Simple coin flip.
	 * @return true or false based on a 50:50 random chance.
	 */
	private static boolean flipCoin() {
		return ThreadLocalRandom.current().nextInt(2) == 0;
	}

	/**
	 * Start the automated simulation process.
	 */
	public void automated() {
		for(int i = 0; i < NUM_ITERATIONS; i++) {
			if (flipCoin()) {
				newAircraft();
			} else {
				processAircraft();
			}
		}		
	}
	
	/**
	 * Prompt a user at the console with a yes/no question and return the result.
	 * 
	 * @param input Response is read from input.
	 * @param query Question to ask user.
	 * @return true If user entered "y" or "yes", false otherwise.
	 */
	private boolean prompt(Scanner input, String query) {
		System.out.println(query);
		String response = input.nextLine();
		return response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes");
	}
	
	/**
	 * Start the interactive simulation process.
	 */
	public void interactive() {
		Scanner input = new Scanner(System.in);
		boolean anotherIteration = true;
		
		while (anotherIteration) {
			if (prompt(input, "Do you want to add aircraft to the airspace (Y/N)?")) {
				newAircraft();
			}
			
			if (prompt(input, "Do you want to process aircraft in the tracking queue (Y/N)?")) {
				processAircraft();
			}
			
			if (prompt(input, "Do you want to search for an aircraft in the tracking queue (Y/N)?")) {
				System.out.println("Enter the aircraft id:");
				String id = input.nextLine();
				
				Iterator<Aircraft> it = tracker.radarQueueIterator();
				while (it.hasNext()) {
					Aircraft aircraft = it.next();
					System.out.println(aircraft);
					if (aircraft.getId().equalsIgnoreCase(id)) {
						System.out.println(aircraft + " was found in the tracking queue waiting to be processed.");
					}
				}
			}
			
			anotherIteration = prompt(input, "Do you want to perform another iteration of the simulation (Y/N)?");
		}
		input.close();
	}

	
	public static void main(String[] args) {
		if (args.length > 0 && args[0].equalsIgnoreCase("auto")) {		// Start automated simulation mode.
			OneSky simulation = new OneSky(Integer.parseInt(args[1]));
			simulation.automated();
		} else if (args.length > 0) {									// Output program usage pattern.
			System.out.println("Usage: java OneSky auto numberOfIterations or java OneSky");
		} else {														// Start interactive simulation mode.
			OneSky simulation = new OneSky(0);
			simulation.interactive();
		}
	}

}
