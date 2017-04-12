package tcd.iainmeeke.electricvehicle;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;

import org.flexiblepower.context.FlexiblePowerContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EVSimulation {

	public static final boolean CHARGING = true;
	public static final boolean NOT_CHARGING = false;

	private static final Logger LOGGER = LoggerFactory.getLogger(EVSimulation.class);

	private double currentChargeKwh; // how charged the car is in Kwh
	private Calendar desiredChargeTime; // the time the car has to be charged by
	private Calendar arriveHomeTime; // the next time the car will arrive home
	private double batteryCapacity; // the size of the battery in Kwh
	private double chargePower; // the power the car charges at
	private boolean pluggedIn; // is the car actually at home
	private boolean charging; // is the car being charged
	private long timeToCharge;// how long it will take to charge the car to the
								// desired charge capacity

	private FlexiblePowerContext context;
	private Calendar homeTimeLower;
	private Calendar homeTimeHigher;
	private Calendar chargeByLower;
	private Calendar chargeByHigher;

	/*
	 * TODO - figure out a way to plug in the car - give a demand curve based on
	 * time it will take to charge vs charging rates vs desired charge time -
	 * maybe add ways for stepped desire charge time (by 10pm be at 20%, by 3am
	 * be at 50%) - some way to update the currentCharge if it is
	 * charging.....do I need a run method to thread it? or just do it in
	 * EV.java - can I have different charge rates?
	 * 
	 */

	public EVSimulation(EVType car, FlexiblePowerContext context, Calendar homeTimeLower, Calendar homeTimeHigher,
			Calendar chargeByLower, Calendar chargeByHigher) {
		this.batteryCapacity = car.getCapacity();
		this.chargePower = car.getChargePower();
		/**
		 * need to change this so that it doesn't come back empty every day
		 */
		this.currentChargeKwh = 0;
		/**
		 * 
		 */
		this.homeTimeLower = homeTimeLower;
		this.homeTimeHigher = homeTimeHigher;
		this.chargeByLower = chargeByLower;
		this.chargeByHigher = chargeByHigher;
		desiredChargeTime = getRandomDate(chargeByLower, chargeByHigher);
		arriveHomeTime = getRandomDate(homeTimeLower, homeTimeHigher);
		pluggedIn = true;
		charging = false;
		this.context = context;
	}

	/**
	 * gets a random Calendar date between two times
	 * 
	 * @param lowerBound
	 *            the earliest the random date can be
	 * @param upperBound
	 *            the latest the random date can be
	 * @return the random date as a Calendar
	 */
	public Calendar getRandomDate(Calendar lowerBound, Calendar upperBound) {
		long random = ThreadLocalRandom.current().nextLong(lowerBound.getTimeInMillis(), upperBound.getTimeInMillis());
		Calendar randomDate = new GregorianCalendar();
		randomDate.setTimeInMillis(random);
		return randomDate;
	}

	/**
	 * get the ratio of time left until needs to be charged to time it takes to
	 * charge. i.e 6 hours until the car needs to be charged, takes 2 hours.
	 * This returns 3.
	 * 
	 * @return the factor of how much bigger time until is
	 */
	public double getTimeToChargeRatio() {
		// how long will it take to charge the battery from current charge?
		double timeToChargeHours = (batteryCapacity - currentChargeKwh) / chargePower;
		double timeToChargeMilli = timeToChargeHours * 3600000;

		// how long do we have until it needs to be charged
		double timeUntilDesired = desiredChargeTime.getTimeInMillis() - context.currentTimeMillis();
		// if it is going to take longer to charge than the time we have then
		// give a full demand
		if (timeUntilDesired < timeToChargeMilli) {
			return 1.0;
		}
		double ratio = timeUntilDesired / timeToChargeMilli;
		return ratio;
	}


	/**
	 * checks if the car should be plugged in (current time is past arrive home
	 * time and before desired charge time)
	 * 
	 * @return true if the car should be plugged in
	 */
	private boolean shouldPlugIn() {
		Long currentTimeMillis = context.currentTimeMillis();
		if (currentTimeMillis >= arriveHomeTime.getTimeInMillis() && currentTimeMillis <= desiredChargeTime.getTimeInMillis()) {
			return true;
		}
		return false;
	}

	/**
	 * sets the car to be either charging or not charging. Throws an exception
	 * if the car is not plugged in
	 * 
	 * @param charge
	 *            boolean to set charging to. True is charging, as per final
	 *            booleans above
	 */
	public void setCharging(boolean charge) {
		if (!pluggedIn) {
			LOGGER.error("Should not be trying to change charge state if the car is not plugged in");
			throw new IllegalArgumentException("Invalid change of charging state");
		}
		charging = charge;
		LOGGER.info("car charging is being set to " + charging);
	}

	public long timeToCharge(double percentage) {
		// TODO return the time it will take to get to be 'percentage' charged
		
		return 0;
	}
	

	public double getChargePower() {
		return chargePower;
	}

	public double getCurrentCharge() {
		return (currentChargeKwh / batteryCapacity) * 100;
	}

	/**
	 * checks if the car should be plugged in and does so if needs be. Then returns the plugged in status
	 * @return true if the car is plugged in
	 */
	public boolean getPluggedIn() {
	    boolean oldStatus = pluggedIn;
		boolean pluggedIn = shouldPlugIn();
		if(pluggedIn!=oldStatus){
		    LOGGER.debug("changing the pluggedIn status to be "+pluggedIn);
		}
		return pluggedIn;
	}

	public boolean getCharging() {
		return charging;
	}

}
