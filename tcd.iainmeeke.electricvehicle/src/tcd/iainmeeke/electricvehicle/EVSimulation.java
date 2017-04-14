package tcd.iainmeeke.electricvehicle;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
	private double chargingAt; // the value the car is being charged at 
	private long timeToCharge;// how long it will take to charge the car to the
								// desired charge capacity
	private long timeLastUpdated; //time in milliseconds that the car status was last updated

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
	 * CHARGING.....do I need a run method to thread it? or just do it in
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
		chargingAt = 0;
		this.context = context;
		timeLastUpdated = context.currentTimeMillis();
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
		if(this.currentChargeKwh == this.batteryCapacity){
		    return 0;
		}
		else{	    
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
	}


	/**
	 * checks if the car should be plugged in (current time is past arrive home
	 * time and before desired charge time) and updates pluggedIn accordingly
	 * Also updates how much the car is charging
	 * Also update the desiredChargeTime and arriveHomeTime so they make sense
	 * 
	 * 
	 * This method is called whenever a bid is made (through the isPluggedIn method) and whenever a priceUpdate happens (through the setCharging method)
	 * This should keep everything accurate
	 */
	private void updatePlugChargeStatus() {
	    
		Long currentTimeMillis = context.currentTimeMillis();
		//update the desiredCharge and arriveHome times
		//if the current time is past the desired charge time then we have gone through a cycle and should set them to tomorrow
		if(desiredChargeTime.getTimeInMillis() < currentTimeMillis){
		    desiredChargeTime.add(Calendar.DATE, 1);
		    arriveHomeTime.add(Calendar.DATE, 1);
		}
		//update plugged in
		if (currentTimeMillis >= arriveHomeTime.getTimeInMillis() && currentTimeMillis <= desiredChargeTime.getTimeInMillis()) {
			pluggedIn = true;
		}
		else{
		    pluggedIn = false;
		}
		//update how much has been charged
		BigDecimal timeSpentChargingMillis = BigDecimal.valueOf(currentTimeMillis - timeLastUpdated);
		BigDecimal timeSpentChargingHours =  timeSpentChargingMillis.divide(BigDecimal.valueOf(1000*60*60), 10, RoundingMode.HALF_UP); //convert the time charging to be a decimal in hours
		//LOGGER.info("\ncuurrentTimeMillis = {}\n timeLastUpdated = {}\ntimeSpentChargingHours db = {}\ntimeSpentChargingHours bd = {}", currentTimeMillis, timeLastUpdated, timeSpentChargingHours.doubleValue(), timeSpentChargingHours);
		currentChargeKwh = Math.min(currentChargeKwh + chargingAt*timeSpentChargingHours.doubleValue(), batteryCapacity); //charge the battery if it is not at capacity
		timeLastUpdated = currentTimeMillis;
		//LOGGER.info("battery current charge = {}KWh", currentChargeKwh);
	}


    public static Logger getLogger() {
        return LOGGER;
    }

    public double getCurrentChargeKwh() {
        return currentChargeKwh;
    }

    public Calendar getDesiredChargeTime() {
        return desiredChargeTime;
    }

    public Calendar getArriveHomeTime() {
        return arriveHomeTime;
    }

    public double getBatteryCapacity() {
        return batteryCapacity;
    }

    public long getTimeToCharge() {
        return timeToCharge;
    }

    public long getTimeLastUpdated() {
        return timeLastUpdated;
    }

    public FlexiblePowerContext getContext() {
        return context;
    }

    public Calendar getHomeTimeLower() {
        return homeTimeLower;
    }

    public Calendar getHomeTimeHigher() {
        return homeTimeHigher;
    }

    public Calendar getChargeByLower() {
        return chargeByLower;
    }

    public Calendar getChargeByHigher() {
        return chargeByHigher;
    }

    /**
	 * sets the car to be either charging or not charging. Throws an exception
	 * if the car is not plugged in
	 * 
	 * @param power
	 *            boolean to set charging to. True is charging, as per final
	 *            booleans above
	 */
	public void setCharging(double power) {
		if (!pluggedIn) {
			LOGGER.error("Should not be trying to change charge state if the car is not plugged in");
			throw new IllegalArgumentException("Invalid change of charging state");
		}
		chargingAt = power;
		updatePlugChargeStatus();
		LOGGER.info("car charging is being set to " + chargingAt);
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
		updatePlugChargeStatus();
		if(pluggedIn!=oldStatus){
		    LOGGER.info("changing the pluggedIn status to be "+pluggedIn);
		}
		return pluggedIn;
	}

	public double getChargingAt(){
	    return chargingAt;
	}
	/*
	 * if the charge power is greater than 0 then the car is charging
	 */
	public boolean isCharging() {
	    if(chargingAt > 0){
	        return CHARGING;
	    }
	    return NOT_CHARGING;
	}

}
