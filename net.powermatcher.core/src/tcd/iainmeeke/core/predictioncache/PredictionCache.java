package tcd.iainmeeke.core.predictioncache;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.powermatcher.api.messages.PredictionUpdate;

public class PredictionCache {

    private final Map<String, PredictionUpdate> agentPredictions;

    private volatile boolean predictionChanged;
    private transient AggregatedPrediction lastPrediction;

    public PredictionCache() {
        agentPredictions = new ConcurrentHashMap<String, PredictionUpdate>();
        predictionChanged = true;
    }

    public void updateAgentPrediction(String agentId, PredictionUpdate prediction) {
        if (prediction == null) {
            agentPredictions.remove(agentId);
        } else {
            predictionChanged = true;
            agentPredictions.put(agentId, prediction);
        }
    }

    public AggregatedPrediction aggregate() {
        if (!predictionChanged && lastPrediction != null) {
            return lastPrediction;
        }

        AggregatedPrediction.Builder builder = new AggregatedPrediction.Builder();
        for (Entry<String, PredictionUpdate> entry : agentPredictions.entrySet()) {
            builder.addAgentPrediction(entry.getKey(), entry.getValue());
        }
        synchronized (this) {
            lastPrediction = builder.build();
            predictionChanged = false;

            return lastPrediction;
        }
    }

    public void removePredictionOfAgent(String agentId) {
        predictionChanged = true;
        agentPredictions.remove(agentId);
    }

}
