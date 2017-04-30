package net.powermatcher.api.data;

import java.util.Calendar;

public class Prediction {

    private final String testString;
    private final double predictedLoad;
    private final Calendar predictedTime;
    private final long predictedDuration;

    public Prediction(String testString, double predictedLoad, Calendar predictedTime, long predictedDuration) {
        this.testString = testString;

        this.predictedLoad = predictedLoad;
        this.predictedTime = predictedTime;
        this.predictedDuration = predictedDuration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof Prediction)) {
            return false;
        } else {
            Prediction other = (Prediction) obj;
            return ((predictedLoad == other.predictedLoad) &&
                    (predictedDuration == other.predictedDuration)
                    &&
                    (predictedTime == other.predictedTime));
        }
    }

    public String getTestString() {
        return testString;
    }

    public double getPredictedLoad() {
        return predictedLoad;
    }

    public Calendar getPredictedTime() {
        return predictedTime;
    }

    public long getPredictedDuration() {
        return predictedDuration;
    }
}
