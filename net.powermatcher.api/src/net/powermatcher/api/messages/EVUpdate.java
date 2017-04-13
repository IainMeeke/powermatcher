package net.powermatcher.api.messages;

import java.util.Calendar;
import java.util.Date;

public class EVUpdate {

    private final double carChargeDesire;
    private final double chargeLevel;
    private final Calendar arriveHomeTime;
    private final Calendar desiredChargeTime;
    private final boolean pluggedIn;
    private final boolean isCharging;
    private final Date simTime;

    public EVUpdate(double carChargeDesire,
                    double chargeLevel,
                    Calendar arriveHomeTime,
                    Calendar desiredChargeTime,
                    boolean pluggedIn,
                    boolean isCharging,
                    Date simTime) {
        this.carChargeDesire = carChargeDesire;
        this.chargeLevel = chargeLevel;
        this.arriveHomeTime = arriveHomeTime;
        this.desiredChargeTime = desiredChargeTime;
        this.pluggedIn = pluggedIn;
        this.isCharging = isCharging;
        this.simTime = simTime;
    }

    public double getCarChargeDesire() {
        return carChargeDesire;
    }

    public double getChargeLevel() {
        return chargeLevel;
    }

    public Calendar getArriveHomeTime() {
        return arriveHomeTime;
    }

    public Calendar getDesiredChargeTime() {
        return desiredChargeTime;
    }

    public boolean isPluggedIn() {
        return pluggedIn;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public Date getSimTime() {
        return simTime;
    }
}
