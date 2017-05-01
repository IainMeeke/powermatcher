package tcd.iainmeeke.core.predictioncache;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.powermatcher.api.data.Prediction;
import net.powermatcher.api.messages.PredictionUpdate;

public class AggregatedPrediction
    extends Prediction {

    public static final class Builder {

        private final Map<String, Integer> agentPredictionReferences;
        private double aggregatedPredictionLoad;
        private final long aggregratedPredictionDuration;
        private Calendar aggregratedPredictionStartTime;

        public Builder() {
            agentPredictionReferences = new HashMap<String, Integer>();
            aggregatedPredictionLoad = 0;
            aggregratedPredictionDuration = 0;

        }

        public Builder addAgentPrediction(String agentId, PredictionUpdate predictionUpdate) {
            if (!agentPredictionReferences.containsKey(agentId)) {
                agentPredictionReferences.put(agentId, predictionUpdate.getPreditcionNumber());
                addPrediction(predictionUpdate.getPrediction());
            }
            return this;
        }

        public Builder addPrediction(Prediction prediction) {
            // TO:DO how do we actually add new predictions
            aggregatedPredictionLoad += prediction.getPredictedLoad();
            if (aggregratedPredictionStartTime == null) {
                aggregratedPredictionStartTime = prediction.getPredictedTime();
            }
            return this;
        }

        public AggregatedPrediction build() {
            return new AggregatedPrediction(aggregatedPredictionLoad,
                                            agentPredictionReferences,
                                            aggregratedPredictionDuration,
                                            aggregratedPredictionStartTime);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((agentPredictionReferences == null) ? 0 : agentPredictionReferences.hashCode());
            long temp;
            temp = Double.doubleToLongBits(aggregatedPredictionLoad);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + (int) (aggregratedPredictionDuration ^ (aggregratedPredictionDuration >>> 32));
            result = prime * result
                     + ((aggregratedPredictionStartTime == null) ? 0 : aggregratedPredictionStartTime.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Builder other = (Builder) obj;
            if (agentPredictionReferences == null) {
                if (other.agentPredictionReferences != null) {
                    return false;
                }
            } else if (!agentPredictionReferences.equals(other.agentPredictionReferences)) {
                return false;
            }
            if (Double.doubleToLongBits(aggregatedPredictionLoad) != Double.doubleToLongBits(other.aggregatedPredictionLoad)) {
                return false;
            }
            if (aggregratedPredictionDuration != other.aggregratedPredictionDuration) {
                return false;
            }
            if (aggregratedPredictionStartTime == null) {
                if (other.aggregratedPredictionStartTime != null) {
                    return false;
                }
            } else if (!aggregratedPredictionStartTime.equals(other.aggregratedPredictionStartTime)) {
                return false;
            }
            return true;
        }
    }

    private final Map<String, Integer> agentPredictionReferences;

    AggregatedPrediction(double predictionLoad,
                         Map<String, Integer> agentPredictionReferences,
                         long predictionDuration,
                         Calendar predictionStart) {
        super("test", predictionLoad, predictionStart, predictionDuration);
        this.agentPredictionReferences =
                                       Collections.unmodifiableMap(new HashMap<String, Integer>(agentPredictionReferences));
    }

    public AggregatedPrediction(Prediction prediction, Map<String, Integer> agentPredictionReferences) {
        super(prediction.getTestString(),
              prediction.getPredictedLoad(),
              prediction.getPredictedTime(),
              prediction.getPredictedDuration());
        this.agentPredictionReferences =
                                       Collections.unmodifiableMap(new HashMap<String, Integer>(agentPredictionReferences));
    }

    public Map<String, Integer> getAgentPredictionReferences() {
        return agentPredictionReferences;
    }

}
