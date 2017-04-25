package net.powermatcher.api.messages;

public class PVUpdate {

    private final double outputPower;

    public PVUpdate(double outputPower) {

        this.outputPower = outputPower;
    }

    public double getOutputPower() {
        return outputPower;
    }

}
