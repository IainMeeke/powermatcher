package net.powermatcher.api.messages;

import net.powermatcher.api.data.Prediction;

public class PredictionUpdate {

    private final int preditcionNumber;
    private final Prediction prediction;

    public PredictionUpdate(Prediction prediction, int predictionNumber) {
        if (prediction == null) {
            throw new NullPointerException("prediction");
        }
        this.prediction = prediction;
        preditcionNumber = predictionNumber;
    }

    public int getPreditcionNumber() {
        return preditcionNumber;
    }

    public Prediction getPrediction() {
        return prediction;
    }
}
