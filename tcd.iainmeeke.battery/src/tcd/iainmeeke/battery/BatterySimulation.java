package tcd.iainmeeke.battery;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.flexiblepower.context.FlexiblePowerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class BatterySimulation {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatterySimulation.class);
    
    private double currentChargeKwh; //how charged the battery is
    private double batteryCapacity; //the size of the battery
    private double chargePower; //the power the battery can charge at
    private double outputPower;
    private double chargingAt; //the value the battery is charging at, goes negative if the battery is supplying power
    
    private long timeLastUpdated; //time in milliseconds that the battery status was last updated
    
    private FlexiblePowerContext context;
    
    
    public BatterySimulation(FlexiblePowerContext context,double outputPower, double capacity, double chargePower){
        this.batteryCapacity = capacity;
        this.context = context;
        this.currentChargeKwh = 0.2*batteryCapacity;
        this.chargePower = chargePower;
        this.outputPower = outputPower;
        this.chargingAt = 0;
        timeLastUpdated = context.currentTimeMillis();
    }
    
    /**
     * update the status of the battery
     * update the charge state
     */
    public void updateStatus(){
        Long currentTimeMillis = context.currentTimeMillis();
        BigDecimal timeSpentChargingMillis = BigDecimal.valueOf(currentTimeMillis - timeLastUpdated);
        BigDecimal timeSpentChargingHours = timeSpentChargingMillis.divide(BigDecimal.valueOf(1000 * 60 * 60), 10,
                RoundingMode.HALF_UP); //convert the time charging to be a decimal in hours
       if(chargingAt >= 0){
           currentChargeKwh = Math.min(currentChargeKwh + chargingAt * timeSpentChargingHours.doubleValue(),
                batteryCapacity); //charge the battery if it is not at capacity
       }
       else{
           currentChargeKwh = Math.max(currentChargeKwh + chargingAt * timeSpentChargingHours.doubleValue(),
                   0); //discharge the battery or set it to 0
       }
        timeLastUpdated = currentTimeMillis;
    }
    
    
    
    /**
     * set the new charging at value. Negative if the battery is producing power, zero if idle, positive if consuming power(charging)
     * @param newCharge the new value to set
     */
    public void setChargingAt(double newCharge){
        chargingAt = newCharge;
    }
    
    /**
     * get a percentage of how full the battery is
     * @return a double 
     */
    public double getCurrentChargeLevel(){
        if(currentChargeKwh == 0){
            return 0;
        }
        return currentChargeKwh/batteryCapacity;
    }
    
    public double getChargePower(){
        return chargePower;
    }
    
    public double getOutputPower(){
        return outputPower;
    }
    
    
}
