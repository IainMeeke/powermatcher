package tcd.iainmeeke.electricvehicle;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class EVSimulation {

	public static final boolean CHARGING = true;
	public static final boolean NOT_CHARGING = false;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EVSimulation.class);
	
	protected double currentChargeKwh; //how charged the car is in Kwh
	protected Date desiredChargeTime; //the time the car has to be charged by
	protected double batteryCapacity; //the size of the battery in Kwh
	protected boolean pluggedIn; //is the car actually at home
	protected boolean charging; //is the car being charged
	
	/*
	 * TODO 
	 * - figure out a way to plug in the car
	 * - give a demand curve based on time it will take to charge vs possible charging rates vs desired charge time
	 * 			- maybe add ways for stepped desire charge time (by 10pm be at 20%, by 3am be at 50%)
	 * - some way to update the currentCharge if it is charging.....do I need a run method to thread it? or just do it in EV.java
	 * - can I have different charge rates?
	 * 
	 */
	
	
	public EVSimulation(){
		currentChargeKwh = 0;
	}
	
	
	public double getCurrentCharge(){
		return (currentChargeKwh/batteryCapacity)*100;
	}
	
	/**
	 * sets the car to be either charging or not charging. Throws an exception if the car is not plugged in
	 * @param charge boolean to set charging to. True is charging, as per final booleans above
	 */
	public void setCharging(boolean charge){
		if(!pluggedIn){
			LOGGER.error("Should not be trying to change charge state if the car is not plugged in");
			throw new IllegalArgumentException("Invalid change of charging state");
		}
		charging = charge;
	}
	
	public long timeToCharge(double percentage){
		//TODO return the time it will take to get to be 'percentage' charged
		return 0;
	}
	
	
}
