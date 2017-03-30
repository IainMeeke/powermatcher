package tcd.iainmeeke.electricvehicle;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class EVSimulation {

	public static final boolean CHARGING = true;
	public static final boolean NOT_CHARGING = false;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EVSimulation.class);
	
	private double currentChargeKwh; //how charged the car is in Kwh
	private Calendar desiredChargeTime; //the time the car has to be charged by
	private double batteryCapacity; //the size of the battery in Kwh
	private double chargePower; //the power the car charges at
	private boolean pluggedIn; //is the car actually at home
	private boolean charging; //is the car being charged
	private long timeToCharge;//how long it will take to charge the car to the desired charge capacity
	
	/*
	 * TODO 
	 * - figure out a way to plug in the car
	 * - give a demand curve based on time it will take to charge vs charging rates vs desired charge time
	 * 			- maybe add ways for stepped desire charge time (by 10pm be at 20%, by 3am be at 50%)
	 * - some way to update the currentCharge if it is charging.....do I need a run method to thread it? or just do it in EV.java
	 * - can I have different charge rates?
	 * 
	 */
	
	
	
	public EVSimulation(EVType car){
		this.batteryCapacity = car.getCapacity();
		this.chargePower = car.getChargePower();
		currentChargeKwh = 0;
		pluggedIn = true;
		charging = false;
		
		// set desired charge time to 6am tomorrow morning
		//TODO make this customizable or something
		Calendar date = new GregorianCalendar();
		date.set(Calendar.HOUR_OF_DAY, 6);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		date.add(Calendar.DAY_OF_MONTH, 1);
		desiredChargeTime = date;
				
	}
	/**
	 * get the ratio of time left until needs to be charged to time it takes to charge. 
	 * i.e 6 hours until the car needs to be charged, takes 2 hours. This returns 3.
	 * @return the factor of how much bigger time until is
	 */
	public double getTimeToChargeRatio(){
		//how long will it take to charge the battery from current charge?
		double timeToChargeHours = (batteryCapacity-currentChargeKwh)/chargePower;
		double timeToChargeMilli = timeToChargeHours * 3600000;
		
		//how long do we have until it needs to be charged
		//TODO: change this current time to simulated time
		double timeUntilDesired = desiredChargeTime.getTimeInMillis() - System.currentTimeMillis();
		
		double ratio  = timeUntilDesired/timeToChargeMilli;
		
		
		return ratio;
				
	}
	
	public double getChargePower(){
		return chargePower;
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
